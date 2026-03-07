# FarmSense ESP32 Firmware

Plug-and-play plant sensor firmware. One binary flashed on all devices. User configures WiFi via captive portal — no code required.

## Hardware

| Component | Pin | Notes |
|-----------|-----|-------|
| DHT22 | GPIO 4 | Temp + humidity |
| Capacitive soil sensor | GPIO 34 | ADC, 3.3V power |
| BH1750 light sensor | SDA=21, SCL=22 | I2C |
| Status LED | GPIO 2 | Built-in on most DevKit V1 boards |
| Reset button | GPIO 0 | Built-in BOOT button |

## Wiring Diagram

```
ESP32 DevKit V1
┌─────────────────────────────┐
│  3V3 ──┬── DHT22 VCC        │
│        └── BH1750 VCC       │
│        └── Soil sensor VCC  │
│  GND ──┬── DHT22 GND        │
│        └── BH1750 GND       │
│        └── Soil sensor GND  │
│  GPIO4  ── DHT22 DATA       │
│  GPIO34 ── Soil sensor AOUT │
│  GPIO21 ── BH1750 SDA       │
│  GPIO22 ── BH1750 SCL       │
└─────────────────────────────┘

4.7kΩ pull-up resistor between DHT22 DATA and VCC.
```

## Required Arduino Libraries

Install via Arduino IDE Library Manager or arduino-cli:

```bash
arduino-cli lib install "DHT sensor library"
arduino-cli lib install "BH1750"
arduino-cli lib install "WiFiManager"
arduino-cli lib install "ArduinoJson"
arduino-cli lib install "ArduinoOTA"
```

## Flash a Single Device

```bash
# Via Arduino IDE:
# 1. Open apps/esp32/src/main.ino
# 2. Tools > Board > ESP32 Dev Module
# 3. Tools > Partition Scheme > Huge APP (3MB No OTA/1MB SPIFFS)
# 4. Edit DEVICE_ID at top of file (or use stamp-device.py for batch)
# 5. Upload

# Via arduino-cli:
arduino-cli compile --fqbn esp32:esp32:esp32 \
  --build-property 'compiler.cpp.extra_flags=-DDEVICE_ID=\"FS-00001\"' \
  src/

arduino-cli upload --fqbn esp32:esp32:esp32 \
  --port /dev/ttyUSB0 \
  src/
```

## Batch Flash (Production)

```bash
pip install pyserial esptool
python scripts/stamp-device.py --start 1 --count 50 --port /dev/ttyUSB0
```

## User Setup Flow (after flash)

1. Plug in ESP32 — LED blinks 3 times
2. Phone shows WiFi network **FarmSense-Setup** — connect
3. Browser auto-opens captive portal (or go to 192.168.4.1)
4. Select home WiFi, enter password
5. Enter **FarmSense Setup Code** from the app
6. Submit — ESP32 reboots, connects, and starts sending data

## Reset WiFi (US-034)

Hold the **BOOT button** for 5 seconds → LED blinks rapidly → WiFi credentials cleared → provisioning portal opens again.

Device claim is preserved — user does not need a new setup code.

## OTA Updates

Push new firmware remotely:

```bash
# Using arduino-cli OTA upload
arduino-cli upload --fqbn esp32:esp32:esp32 \
  --port FS-00001.local \   # mDNS hostname
  --upload-field password=farmsense-ota \
  src/
```

## Sensor Calibration

Soil sensor values vary by hardware. Calibrate `SOIL_DRY` and `SOIL_WET` in `main.ino`:

```cpp
// Place sensor in dry air → read analogRead(34) → set as SOIL_DRY
// Place sensor in water   → read analogRead(34) → set as SOIL_WET
const int SOIL_DRY = 3200;  // adjust for your sensor
const int SOIL_WET = 1200;  // adjust for your sensor
```

Open Serial Monitor at 115200 baud to see live readings during calibration.
