export type Lang = 'FR' | 'AR' | 'DARIJA'

export interface User {
  userId: string
  name: string
  email: string
  lang: Lang
  phoneWa?: string
  latitude?: number | null
  longitude?: number | null
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  userId: string
  name: string
  email: string
  lang: Lang
  phoneWa?: string
  latitude?: number | null
  longitude?: number | null
}

export interface Plant {
  id: string
  name: string
  species?: string
  location?: string
  photoUrl?: string
  soilMin: number
  soilMax: number
  tempMin: number
  tempMax: number
  lightMin: number
  createdAt: string
  updatedAt: string
  wateringIntervalDays?: number
  fertilisingIntervalDays?: number
  repottingIntervalDays?: number
  lastWateredAt?: string
  lastFertilisedAt?: string
  lastRepottedAt?: string
}

export interface CreatePlantRequest {
  name: string
  species?: string
  location?: string
  photoUrl?: string
  soilMin?: number
  soilMax?: number
  tempMin?: number
  tempMax?: number
  lightMin?: number
}

export interface UpdatePlantRequest extends Partial<CreatePlantRequest> {}

export interface SensorReading {
  id: number
  deviceId: string
  temperature?: number
  humidity?: number
  soilMoisture?: number
  lightLux?: number
  source: 'SENSOR' | 'MANUAL'
  recordedAt: string
}

export interface Alert {
  id: string
  plantId?: string
  plantName?: string
  type: 'SOIL_DRY' | 'SOIL_WET' | 'TEMP_HIGH' | 'TEMP_LOW' | 'LIGHT_LOW' | 'DEVICE_OFFLINE'
  severity: 'LOW' | 'MEDIUM' | 'HIGH'
  sensorValue?: number
  msgFr?: string
  msgAr?: string
  msgDarija?: string
  waSent: boolean
  triggeredAt: string
  ackAt?: string
}

export interface AlertPreference {
  soilDryEnabled: boolean
  soilWetEnabled: boolean
  tempHighEnabled: boolean
  tempLowEnabled: boolean
  lightLowEnabled: boolean
  deviceOfflineEnabled: boolean
  quietHoursStart?: number
  quietHoursEnd?: number
  channelWhatsapp: boolean
  channelPush: boolean
}

export interface Device {
  id: string
  deviceId: string
  label?: string
  plantId?: string
  plantName?: string
  online: boolean
  lastSeenAt?: string
  claimedAt?: string
  readIntervalMs: number
  soilDryValue: number
  soilWetValue: number
}

export interface DeviceConfigRequest {
  readIntervalMs: number;
  soilDryValue: number;
  soilWetValue: number;
}

export interface WeatherData {
  temperature: number
  humidity: number
  windSpeed: number
  weatherCode: number
  precipitation: number
  weatherDescription: string
  weatherIcon: string
  daily: DailyForecast[]
}

export interface DailyForecast {
  date: string
  tempMax: number
  tempMin: number
  weatherCode: number
  weatherDescription: string
  weatherIcon: string
}

export interface CareSchedule {
  watering: CareTask
  fertilising: CareTask
  repotting: CareTask
  recentLog: CareLogEntry[]
}

export interface CareTask {
  intervalDays: number
  lastDoneAt?: string
  nextDueAt?: string
  daysRemaining: number
  overdue: boolean
}

export interface CareLogEntry {
  id: string
  taskType: string
  doneAt: string
  notes?: string
}
