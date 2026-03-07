/**
 * FarmSense NodeMCU v2 (ESP8266) Firmware — v0
 * ─────────────────────────────────────────────────────────────
 * Board:    NodeMCU v2 (ESP8266)
 * Sensors:  DHT22 (temp + humidity)  → D4 (GPIO2)
 *           Capacitive soil moisture → A0, power-switched via D6 (GPIO12)
 *           LDR light level          → A0, power-switched via D7 (GPIO13)
 *           BH1750 light sensor      → I2C SDA=D5(GPIO14), SCL=D1(GPIO5)
 *
 * Note: Soil + LDR share A0 and are multiplexed via power pins.
 *       I2C uses D5/D1 to keep D6/D7 free for power switching.
 *
 * Provisioning: WiFiManager captive portal — US-031
 * Claim:    POST /api/v1/devices/claim on first connect — US-032
 * Readings: POST /api/v1/readings every READ_INTERVAL_MS — US-020
 * Offline:  Circular in-memory buffer — US-062
 * OTA:      ArduinoOTA enabled
 * Reset:    Hold D3 (GPIO0) for 5s to clear WiFi — US-034
 * Persist:  LittleFS JSON config (/farmsense.json)
 */

#include <Arduino.h>
#include <ArduinoJson.h>
#include <ArduinoOTA.h>
#include <BH1750.h>
#include <DHT.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <LittleFS.h>
#include <WiFiManager.h>
#include <Wire.h>

// ── Pin configuration ─────────────────────────────────────────
#define DHT_PIN 2 // D4 on NodeMCU (GPIO2)
#define DHT_TYPE DHT22
#define SOIL_PIN A0                // Only ADC pin on ESP8266 (10-bit, 0–1023)
#define SOIL_POWER_PIN 12          // D6 — powers capacitive soil sensor
#define LDR_POWER_PIN 13           // D7 — powers LDR
#define RESET_PIN 0                // D3 — BOOT/FLASH button (INPUT_PULLUP)
#define STATUS_LED_PIN LED_BUILTIN // active LOW

// ── I2C for BH1750: D5=GPIO14 (SDA), D1=GPIO5 (SCL)
// Moved off D6/D7 so those pins remain free for soil/LDR power switching
#define I2C_SDA 14
#define I2C_SCL 5

// ── Timing ───────────────────────────────────────────────────
#define READ_INTERVAL_MS 900000UL // 15 minutes
#define RESET_HOLD_MS 5000UL      // 5s hold to reset WiFi
#define BUFFER_SIZE 96            // 24h at 15min intervals

// ── Device identity ──────────────────────────────────────────
#ifndef DEVICE_ID
#define DEVICE_ID "FS-00001"
#endif
#define FIRMWARE_VERSION "0.1.0"

// ── Config file on LittleFS ───────────────────────────────────
#define CONFIG_FILE "/farmsense.json"

// ── API host (overridden by WiFiManager portal) ───────────────
char apiHost[120] = "http://192.168.11.109:8080";

// ── Globals ───────────────────────────────────────────────────
DHT dht(DHT_PIN, DHT_TYPE);
BH1750 lightMeter;
WiFiClient wifiClient;

bool isClaimed = false;
uint32_t lastReadTime = 0;
uint32_t resetHoldStart = 0;
bool resetButtonHeld = false;

// Circular offline buffer
struct Reading {
  float temperature;
  float humidity;
  int soilPercent;
  float lightLux;
  int lightRaw; // raw LDR analog value (0–1023)
  uint32_t timestamp;
  bool used;
};

Reading offlineBuffer[BUFFER_SIZE];
int bufferHead = 0;
int bufferCount = 0;

// ── LED helpers ───────────────────────────────────────────────
void ledOn() { digitalWrite(STATUS_LED_PIN, LOW); }
void ledOff() { digitalWrite(STATUS_LED_PIN, HIGH); }
void ledBlink(int n, int ms = 100) {
  for (int i = 0; i < n; i++) {
    ledOn();
    delay(ms);
    ledOff();
    delay(ms);
  }
}

// ── Config persistence (LittleFS + ArduinoJson) ───────────────
StaticJsonDocument<512> cfg;

// ── Soil moisture ─────────────────────────────────────────────
int soilToPercent(int raw) {
  // ESP8266 ADC is 10-bit (0–1023).
  int soilDry =
      cfg.containsKey("soilDryValue") ? cfg["soilDryValue"].as<int>() : 1024;
  int soilWet =
      cfg.containsKey("soilWetValue") ? cfg["soilWetValue"].as<int>() : 300;
  int pct = map(raw, soilDry, soilWet, 0, 100);
  return constrain(pct, 0, 100);
}

void loadConfig() {
  if (!LittleFS.begin()) {
    Serial.println("[Config] LittleFS mount failed — formatting");
    LittleFS.format();
    LittleFS.begin();
  }
  if (LittleFS.exists(CONFIG_FILE)) {
    File f = LittleFS.open(CONFIG_FILE, "r");
    if (deserializeJson(cfg, f) != DeserializationError::Ok) {
      Serial.println("[Config] Parse error — using defaults");
    }
    f.close();
  }
  Serial.println("[Config] Loaded");
}

void saveConfig() {
  File f = LittleFS.open(CONFIG_FILE, "w");
  serializeJson(cfg, f);
  f.close();
}

String cfgGet(const char *key, const char *def = "") { return cfg[key] | def; }

bool cfgGetBool(const char *key, bool def = false) {
  if (!cfg.containsKey(key))
    return def;
  return cfg[key].as<bool>();
}

void cfgPutStr(const char *key, const char *val) {
  cfg[key] = val;
  saveConfig();
}
void cfgPutBool(const char *key, bool val) {
  cfg[key] = val;
  saveConfig();
}
void cfgPutInt(const char *key, int val) {
  cfg[key] = val;
  saveConfig();
}

// ── WiFiManager provisioning portal ──────────────────────────
void provisionWiFi() {
  WiFiManager wm;
  wm.setTitle("FarmSense — إعداد الجهاز");

  // Claim token entered by user from the FarmSense app
  char claimToken[12] = "";
  WiFiManagerParameter tokenParam(
      "claim_token", "FarmSense Setup Code (من التطبيق / Code de l'appli)",
      claimToken, 10);
  wm.addParameter(&tokenParam);

  // API host — advanced field (most users don't need to change)
  WiFiManagerParameter apiParam("api_host", "API Host", apiHost, 120);
  wm.addParameter(&apiParam);

  wm.setAPStaticIPConfig(IPAddress(192, 168, 4, 1), IPAddress(192, 168, 4, 1),
                         IPAddress(255, 255, 255, 0));
  wm.setConfigPortalTimeout(300); // 5 min

  // Callback fires when user clicks Save in the portal
  wm.setSaveParamsCallback([&]() {
    strncpy(claimToken, tokenParam.getValue(), sizeof(claimToken));
    strncpy(apiHost, apiParam.getValue(), sizeof(apiHost));
    cfgPutStr("claim_token", claimToken);
    cfgPutStr("api_host", apiHost);
    Serial.printf("[WiFi] Saved token=%s  host=%s\n", claimToken, apiHost);
  });

  Serial.println("[WiFi] Starting portal FarmSense-Setup...");
  ledBlink(3, 300);

  if (!wm.autoConnect("FarmSense-Setup")) {
    Serial.println("[WiFi] Portal failed — restarting");
    ESP.restart();
  }

  Serial.printf("[WiFi] Connected. IP: %s\n",
                WiFi.localIP().toString().c_str());
}

// ── Device claim ──────────────────────────────────────────────
bool claimDevice() {
  String token = cfgGet("claim_token");
  if (token.isEmpty()) {
    Serial.println("[Claim] No token — skipping");
    return false;
  }

  HTTPClient http;
  String url = String(apiHost) + "/api/v1/devices/claim";
  http.begin(wifiClient, url);
  http.addHeader("Content-Type", "application/json");

  StaticJsonDocument<256> body;
  body["deviceId"] = DEVICE_ID;
  body["claimToken"] = token;
  body["firmwareVersion"] = FIRMWARE_VERSION;

  String bodyStr;
  serializeJson(body, bodyStr);

  int code = http.POST(bodyStr);
  http.end();

  if (code == 200 || code == 201) {
    Serial.printf("[Claim] Success (HTTP %d)\n", code);
    cfgPutStr("claim_token", ""); // consume the one-time token
    cfgPutBool("claimed", true);
    return true;
  }

  Serial.printf("[Claim] Failed (HTTP %d) — will retry next boot\n", code);
  return false;
}

// ── Sensor reading ────────────────────────────────────────────
Reading readSensors() {
  Reading r;
  r.timestamp = millis();
  r.temperature = dht.readTemperature();
  r.humidity = dht.readHumidity();

  // Read Soil Moisture (power-switched via D6)
  digitalWrite(SOIL_POWER_PIN, HIGH);
  delay(800); // let voltage stabilize (matching user code)
  int rawSoil = analogRead(SOIL_PIN);
  r.soilPercent = soilToPercent(rawSoil);
  Serial.printf("[Debug] Raw Soil: %d -> %d%%\n", rawSoil, r.soilPercent);
  digitalWrite(SOIL_POWER_PIN, LOW);

  // Bleed residual voltage on A0 by setting power pins low and waiting
  delay(100);

  // Read Light (LDR)
  digitalWrite(LDR_POWER_PIN, HIGH);
  delay(800); // let voltage stabilize (matching user code)
  r.lightRaw = analogRead(SOIL_PIN);
  Serial.printf("[Debug] Raw LDR: %d\n", r.lightRaw);
  digitalWrite(LDR_POWER_PIN, LOW);

  // Ensure LDR value is never negative (API expects positive lux)
  if (r.lightRaw < 0)
    r.lightRaw = 0;

  // Also read BH1750 via I2C if available (returns < 0 if unconnected/error)
  r.lightLux = lightMeter.readLightLevel();
  if (r.lightLux < 0)
    r.lightLux = 0; // Prevent returning -2 lux to the backend

  r.used = true;

  if (isnan(r.temperature))
    r.temperature = -999;
  if (isnan(r.humidity))
    r.humidity = -999;

  return r;
}

// ── HTTP POST reading ─────────────────────────────────────────
bool postReading(const Reading &r) {
  if (WiFi.status() != WL_CONNECTED)
    return false;

  HTTPClient http;
  String url = String(apiHost) + "/api/v1/readings";
  http.begin(wifiClient, url);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("X-Device-Key", DEVICE_ID);
  http.setTimeout(10000);

  StaticJsonDocument<300> doc;
  doc["deviceId"] = DEVICE_ID;
  if (r.temperature > -900)
    doc["temperature"] = r.temperature;
  if (r.humidity > -900)
    doc["humidity"] = r.humidity;
  doc["soilMoisture"] = r.soilPercent;
  doc["lightLux"] =
      r.lightRaw; // Sending raw LDR value as lightLux so it appears on frontend

  String body;
  serializeJson(doc, body);

  int code = http.POST(body);
  if (code == 200 || code == 201) {
    String response = http.getString();
    StaticJsonDocument<512> resDoc;
    if (deserializeJson(resDoc, response) == DeserializationError::Ok) {
      bool changed = false;

      int oldInterval = cfg.containsKey("readIntervalMs")
                            ? cfg["readIntervalMs"].as<int>()
                            : READ_INTERVAL_MS;
      if (resDoc.containsKey("readIntervalMs") &&
          resDoc["readIntervalMs"].as<int>() != oldInterval) {
        cfg["readIntervalMs"] = resDoc["readIntervalMs"].as<int>();
        changed = true;
      }

      int oldDry = cfg.containsKey("soilDryValue")
                       ? cfg["soilDryValue"].as<int>()
                       : 1024;
      if (resDoc.containsKey("soilDryValue") &&
          resDoc["soilDryValue"].as<int>() != oldDry) {
        cfg["soilDryValue"] = resDoc["soilDryValue"].as<int>();
        changed = true;
      }

      int oldWet =
          cfg.containsKey("soilWetValue") ? cfg["soilWetValue"].as<int>() : 300;
      if (resDoc.containsKey("soilWetValue") &&
          resDoc["soilWetValue"].as<int>() != oldWet) {
        cfg["soilWetValue"] = resDoc["soilWetValue"].as<int>();
        changed = true;
      }

      if (changed) {
        saveConfig();
        Serial.println("[Config] Settings updated from server");
      }
    }
  }

  http.end();

  return (code == 200 || code == 201);
}

// ── Offline buffer ────────────────────────────────────────────
void bufferReading(const Reading &r) {
  offlineBuffer[bufferHead] = r;
  bufferHead = (bufferHead + 1) % BUFFER_SIZE;
  if (bufferCount < BUFFER_SIZE)
    bufferCount++;
  Serial.printf("[Buffer] Stored (%d queued)\n", bufferCount);
}

void flushBuffer() {
  if (bufferCount == 0)
    return;
  Serial.printf("[Buffer] Flushing %d readings...\n", bufferCount);

  int flushed = 0;
  int start = (bufferHead - bufferCount + BUFFER_SIZE) % BUFFER_SIZE;

  for (int i = 0; i < bufferCount; i++) {
    int idx = (start + i) % BUFFER_SIZE;
    if (offlineBuffer[idx].used && postReading(offlineBuffer[idx])) {
      offlineBuffer[idx].used = false;
      flushed++;
      delay(200);
    }
  }
  bufferCount -= flushed;
  Serial.printf("[Buffer] Flushed %d\n", flushed);
}

// ── OTA ───────────────────────────────────────────────────────
void setupOTA() {
  ArduinoOTA.setHostname(DEVICE_ID);
  ArduinoOTA.setPassword(cfgGet("ota_password", "farmsense-ota").c_str());
  ArduinoOTA.onStart([]() { Serial.println("[OTA] Start"); });
  ArduinoOTA.onEnd([]() { Serial.println("[OTA] Done"); });
  ArduinoOTA.onError(
      [](ota_error_t e) { Serial.printf("[OTA] Error %u\n", e); });
  ArduinoOTA.begin();
  Serial.println("[OTA] Ready");
}

// ── Reset button ──────────────────────────────────────────────
void checkResetButton() {
  if (digitalRead(RESET_PIN) == LOW) {
    if (!resetButtonHeld) {
      resetButtonHeld = true;
      resetHoldStart = millis();
    } else if (millis() - resetHoldStart > RESET_HOLD_MS) {
      Serial.println("[Reset] Clearing WiFi credentials...");
      ledBlink(5, 200);
      WiFiManager wm;
      wm.resetSettings();
      cfgPutBool("claimed", false);
      ESP.restart();
    }
  } else {
    resetButtonHeld = false;
  }
}

// ── Setup ─────────────────────────────────────────────────────
void setup() {
  Serial.begin(115200);
  Serial.printf("\n\nFarmSense Firmware %s — Device %s (ESP8266)\n",
                FIRMWARE_VERSION, DEVICE_ID);

  pinMode(STATUS_LED_PIN, OUTPUT);
  pinMode(RESET_PIN, INPUT_PULLUP);
  pinMode(SOIL_POWER_PIN, OUTPUT);
  pinMode(LDR_POWER_PIN, OUTPUT);
  digitalWrite(SOIL_POWER_PIN, LOW);
  digitalWrite(LDR_POWER_PIN, LOW);
  ledOff();

  // Load persistent config from LittleFS
  loadConfig();
  String savedHost = cfgGet("api_host");
  if (!savedHost.isEmpty())
    strncpy(apiHost, savedHost.c_str(), sizeof(apiHost));
  isClaimed = cfgGetBool("claimed", false);

  // Init sensors
  Wire.begin(I2C_SDA, I2C_SCL); // SDA=D5(GPIO14), SCL=D1(GPIO5)
  dht.begin();
  lightMeter.begin();
  delay(1000);

  // Connect to WiFi (or open captive portal if no credentials saved)
  provisionWiFi();
  ledBlink(2);

  // Claim device with backend (one-time, uses token from portal)
  if (!isClaimed)
    isClaimed = claimDevice();

  // Replay any offline readings
  flushBuffer();

  setupOTA();

  Serial.println("[Setup] Complete — entering loop");
}

// ── Main loop ─────────────────────────────────────────────────
void loop() {
  ArduinoOTA.handle();
  checkResetButton();

  uint32_t now = millis();
  uint32_t currentInterval = cfg.containsKey("readIntervalMs")
                                 ? cfg["readIntervalMs"].as<uint32_t>()
                                 : READ_INTERVAL_MS;

  if (now - lastReadTime >= currentInterval || lastReadTime == 0) {
    lastReadTime = now;

    Reading r = readSensors();
    Serial.printf("[Reading] soil=%d%%  temp=%.1f°C  hum=%.1f%%  ldr_raw=%d  "
                  "i2c_lux=%.0lux\n",
                  r.soilPercent, r.temperature, r.humidity, r.lightRaw,
                  r.lightLux);

    if (WiFi.status() == WL_CONNECTED) {
      flushBuffer();
      if (postReading(r)) {
        Serial.println("[Reading] Posted successfully ✓");
        ledBlink(2);
      } else {
        Serial.println("[Reading] POST failed — buffering");
        bufferReading(r);
        ledBlink(1, 500);
      }
    } else {
      Serial.println("[Reading] WiFi offline — buffering");
      bufferReading(r);
      ledBlink(1, 1000);
    }
  }

  delay(100);
}
