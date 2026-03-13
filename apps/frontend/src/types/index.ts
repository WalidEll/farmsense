export type Lang = 'FR' | 'AR' | 'EN'

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
  msgEn?: string
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

// ── Crop Database ──────────────────────────────────────
export type CropCategory = 'VEGETABLE' | 'FRUIT' | 'HERB' | 'GRAIN' | 'LEGUME' | 'OTHER'
export type CropDifficulty = 'EASY' | 'MEDIUM' | 'HARD'
export type NutrientLevel = 'LOW' | 'MEDIUM' | 'HIGH'
export type IssueType = 'PEST' | 'DISEASE' | 'DEFICIENCY'

export interface Crop {
  id: string
  name: string
  nameAr?: string
  nameEn?: string
  scientificName?: string
  category: CropCategory
  description?: string
  descriptionAr?: string
  descriptionEn?: string
  imageUrl?: string
  growingSeason?: string
  daysToHarvest?: number
  difficulty?: CropDifficulty
  createdAt: string
  updatedAt: string
}

export interface CropRequirement {
  id: string
  soilMoistureMin?: number
  soilMoistureMax?: number
  tempMin?: number
  tempMax?: number
  lightMin?: number
  lightMax?: number
  humidityMin?: number
  humidityMax?: number
  soilType?: string
  phMin?: number
  phMax?: number
  waterFrequency?: string
}

export interface CropGrowthStage {
  id: string
  stageOrder: number
  name: string
  nameAr?: string
  nameEn?: string
  durationDays?: number
  description?: string
  descriptionAr?: string
  descriptionEn?: string
}

export interface CropNutrient {
  id: string
  nitrogenNeed?: NutrientLevel
  phosphorusNeed?: NutrientLevel
  potassiumNeed?: NutrientLevel
  fertilizerType?: string
  fertilizerTypeAr?: string
  fertilizerTypeEn?: string
  applicationFrequency?: string
}

export interface CropIssue {
  id: string
  issueType: IssueType
  name: string
  nameAr?: string
  nameEn?: string
  symptoms?: string
  symptomsAr?: string
  symptomsEn?: string
  treatment?: string
  treatmentAr?: string
  treatmentEn?: string
  prevention?: string
}

export interface CropDetail extends Crop {
  requirements?: CropRequirement
  stages: CropGrowthStage[]
  nutrients?: CropNutrient
  issues: CropIssue[]
}

export interface CreateCropRequest {
  name: string
  nameAr?: string
  nameEn?: string
  scientificName?: string
  category: CropCategory
  description?: string
  descriptionAr?: string
  descriptionEn?: string
  imageUrl?: string
  growingSeason?: string
  daysToHarvest?: number
  difficulty?: CropDifficulty
}

export type UpdateCropRequest = Partial<CreateCropRequest>

// ── Crop Planning ────────────────────────────────────────
export type PlanSeason = 'SPRING' | 'SUMMER' | 'AUTUMN' | 'WINTER' | 'YEAR_ROUND'
export type PlanStatus = 'DRAFT' | 'ACTIVE' | 'COMPLETED' | 'ARCHIVED'
export type LocationType = 'BED' | 'POT' | 'ROW' | 'GREENHOUSE' | 'FIELD' | 'INDOOR' | 'OTHER'
export type PlantingStatus = 'PLANNED' | 'SOWN' | 'TRANSPLANTED' | 'GROWING' | 'HARVESTING' | 'COMPLETED' | 'FAILED'
export type YieldUnit = 'KG' | 'UNITS' | 'BUNCHES' | 'LITERS'
export type PlantingTaskType = 'SOW' | 'TRANSPLANT' | 'WATER' | 'FERTILIZE' | 'PRUNE' | 'HARVEST' | 'PEST_CHECK'
export type PlantingTaskStatus = 'PENDING' | 'DONE' | 'SKIPPED'

export interface CropPlan {
  id: string
  name: string
  nameAr?: string
  nameEn?: string
  description?: string
  descriptionAr?: string
  descriptionEn?: string
  season: PlanSeason
  year: number
  status: PlanStatus
  plantingCount: number
  createdAt: string
  updatedAt: string
}

export interface FarmLocation {
  id: string
  name: string
  nameAr?: string
  nameEn?: string
  description?: string
  descriptionAr?: string
  descriptionEn?: string
  locationType: LocationType
  areaM2?: number
  latitude?: number
  longitude?: number
  createdAt: string
  updatedAt: string
}

export interface PlantingItem {
  id: string
  cropPlanId: string
  cropId: string
  cropName: string
  farmLocationId?: string
  farmLocationName?: string
  status: PlantingStatus
  quantity?: number
  areaM2?: number
  notes?: string
  plannedSowDate?: string
  plannedTransplantDate?: string
  plannedHarvestDate?: string
  actualSowDate?: string
  actualTransplantDate?: string
  actualHarvestDate?: string
  yieldAmount?: number
  yieldUnit?: YieldUnit
  qualityRating?: number
  harvestNotes?: string
  failureReason?: string
  createdAt: string
  updatedAt: string
}

export interface PlantingNote {
  id: string
  plantingId: string
  content: string
  createdAt: string
}

export interface PlantingTask {
  id: string
  plantingId: string
  taskType: PlantingTaskType
  title: string
  titleAr?: string
  titleEn?: string
  dueDate: string
  status: PlantingTaskStatus
  completedAt?: string
  skipReason?: string
  createdAt: string
}

export interface CreateCropPlanRequest {
  name: string
  nameAr?: string
  nameEn?: string
  description?: string
  descriptionAr?: string
  descriptionEn?: string
  season: PlanSeason
  year: number
}

export interface UpdateCropPlanRequest {
  name?: string
  nameAr?: string
  nameEn?: string
  description?: string
  descriptionAr?: string
  descriptionEn?: string
  season?: PlanSeason
  year?: number
  status?: PlanStatus
}

export interface CreateLocationRequest {
  name: string
  nameAr?: string
  nameEn?: string
  description?: string
  descriptionAr?: string
  descriptionEn?: string
  locationType: LocationType
  areaM2?: number
  latitude?: number
  longitude?: number
}

export interface CreatePlantingRequest {
  cropId: string
  farmLocationId?: string
  quantity?: number
  areaM2?: number
  notes?: string
  plannedSowDate?: string
  plannedTransplantDate?: string
  plannedHarvestDate?: string
}

export interface UpdatePlantingRequest {
  farmLocationId?: string
  status?: PlantingStatus
  quantity?: number
  areaM2?: number
  notes?: string
  plannedSowDate?: string
  plannedTransplantDate?: string
  plannedHarvestDate?: string
  failureReason?: string
}

export interface PlantingYieldRequest {
  yieldAmount: number
  yieldUnit: YieldUnit
  qualityRating?: number
  harvestNotes?: string
}

// ── Poultry Module ────────────────────────────────────────
export type FlockPurpose = 'LAYERS' | 'BROILERS'
export type FlockStatus = 'ACTIVE' | 'SOLD' | 'FINISHED'

export interface Flock {
  id: string
  name: string
  nameAr?: string
  nameEn?: string
  breed?: string
  birdCount: number
  currentBirdCount: number
  purpose: FlockPurpose
  status: FlockStatus
  startDate?: string
  supplierId?: string
  supplierName?: string
  source?: string
  notes?: string
  createdAt: string
  updatedAt: string
}

export interface Supplier {
  id: string
  name: string
  phone?: string
  email?: string
  address?: string
  productsSupplied?: string
  notes?: string
  createdAt: string
  updatedAt: string
}

export interface Customer {
  id: string
  name: string
  phone?: string
  email?: string
  address?: string
  notes?: string
  createdAt: string
  updatedAt: string
}

export interface CreateFlockRequest {
  name: string
  nameAr?: string
  nameEn?: string
  breed?: string
  birdCount: number
  purpose: FlockPurpose
  startDate?: string
  supplierId?: string
  source?: string
  notes?: string
}

export interface UpdateFlockRequest extends Partial<CreateFlockRequest> {
  status?: FlockStatus
}

export interface CreateSupplierRequest {
  name: string
  phone?: string
  email?: string
  address?: string
  productsSupplied?: string
  notes?: string
}

export interface UpdateSupplierRequest extends Partial<CreateSupplierRequest> {}

export interface CreateCustomerRequest {
  name: string
  phone?: string
  email?: string
  address?: string
  notes?: string
}

export interface UpdateCustomerRequest extends Partial<CreateCustomerRequest> {}

// ── Smart Farm Accounting ──────────────────────────────────
export type TransactionType = 'EXPENSE' | 'INCOME'
export type ApprovalStatus = 'DRAFT' | 'PENDING' | 'APPROVED' | 'REJECTED'
export type PaymentMethod = 'CASH' | 'BANK_TRANSFER' | 'CHECK' | 'MOBILE' | 'OTHER'
export type OcrStatus = 'PENDING' | 'PROCESSED' | 'FAILED'
export type TeamRole = 'OWNER' | 'MANAGER' | 'VIEWER'
export type MemberStatus = 'PENDING' | 'ACTIVE' | 'REVOKED'
export type ApprovalAction = 'SUBMITTED' | 'APPROVED' | 'REJECTED'

export interface Tag {
  id: string
  name: string
  color: string
}

export interface TagResponse extends Tag {}

export interface CreateTagRequest {
  name: string
  color?: string
}

export interface UpdateTagRequest extends Partial<CreateTagRequest> {}

export interface Transaction {
  id: string
  type: TransactionType
  category: string
  subcategory?: string
  amount: number
  quantity?: number
  unitPrice?: number
  transactionDate: string
  description?: string
  paymentMethod?: PaymentMethod
  referenceNumber?: string
  approvalStatus: ApprovalStatus
  supplierId?: string
  supplierName?: string
  customerId?: string
  customerName?: string
  receiptId?: string
  flockId?: string
  flockName?: string
  cropPlanId?: string
  cropPlanName?: string
  farmLocationId?: string
  farmLocationName?: string
  notes?: string
  tags: TagResponse[]
  approvedBy?: string
  approvedAt?: string
  createdAt: string
  updatedAt: string
}

export interface TransactionResponse extends Transaction {}

export interface CreateTransactionRequest {
  type: TransactionType
  category: string
  subcategory?: string
  amount: number
  quantity?: number
  unitPrice?: number
  transactionDate: string
  description?: string
  paymentMethod?: PaymentMethod
  referenceNumber?: string
  supplierId?: string
  customerId?: string
  receiptId?: string
  flockId?: string
  cropPlanId?: string
  farmLocationId?: string
  tagIds?: string[]
  notes?: string
}

export interface UpdateTransactionRequest extends Partial<CreateTransactionRequest> {
  approvalStatus?: ApprovalStatus
}

export interface TransactionFilter {
  type?: TransactionType
  category?: string
  from?: string
  to?: string
  approvalStatus?: ApprovalStatus
  tagIds?: string[]
}

export interface CategorySummary {
  category: string
  total: number
}

export interface DashboardResponse {
  totalIncome: number
  totalExpenses: number
  netProfit: number
  expensesByCategory: CategorySummary[]
  incomesByCategory: CategorySummary[]
  recentTransactions: TransactionResponse[]
  pendingApprovals: number
}

export interface Receipt {
  id: string
  originalFilename?: string
  contentType?: string
  fileSizeBytes?: number
  ocrVendor?: string
  ocrDate?: string
  ocrAmount?: number
  ocrCategory?: string
  ocrLineItems?: string // JSON string
  ocrRawJson?: string
  ocrConfidence?: number
  ocrStatus: OcrStatus
  transactionId?: string
  createdAt: string
  updatedAt: string
}

export interface ReceiptResponse extends Receipt {}

export interface ReceiptUploadResponse {
  id: string
  ocrStatus: OcrStatus
  originalFilename?: string
}

export interface ReceiptConfirmRequest {
  type: TransactionType
  category: string
  subcategory?: string
  amount: number
  transactionDate: string
  description?: string
  paymentMethod?: PaymentMethod
  supplierId?: string
  customerId?: string
  flockId?: string
  cropPlanId?: string
  farmLocationId?: string
  tagIds?: string[]
}

export interface LaborLog {
  id: string
  workerName: string
  workerRole?: string
  hourlyRate: number
  hoursWorked: number
  workDate: string
  activity?: string
  transactionId?: string
  flockId?: string
  cropPlanId?: string
  farmLocationId?: string
  notes?: string
  createdAt: string
  updatedAt: string
}

export interface LaborLogResponse extends LaborLog {}

export interface CreateLaborLogRequest {
  workerName: string
  workerRole?: string
  hourlyRate: number
  hoursWorked: number
  workDate: string
  activity?: string
  flockId?: string
  cropPlanId?: string
  farmLocationId?: string
  notes?: string
}

export interface UpdateLaborLogRequest extends Partial<CreateLaborLogRequest> {}

export interface Team {
  id: string
  name: string
  description?: string
  ownerName: string
  createdAt: string
}

export interface TeamResponse extends Team {}

export interface CreateTeamRequest {
  name: string
  description?: string
}

export interface UpdateTeamRequest extends Partial<CreateTeamRequest> {}

export interface TeamMember {
  id: string
  userId: string
  userName: string
  userEmail: string
  role: TeamRole
  status: MemberStatus
  invitedByName?: string
  invitedAt: string
  joinedAt?: string
}

export interface TeamMemberResponse extends TeamMember {}

export interface InviteMemberRequest {
  email: string
  role: TeamRole
}

export interface ApprovalRequest {
  action: ApprovalAction
  comment?: string
}

export interface ApprovalLogEntry {
  id: string
  action: ApprovalAction
  userName: string
  comment?: string
  createdAt: string
}

export interface ApprovalLogResponse extends ApprovalLogEntry {}

export type FieldStatus = 'OPTIMAL' | 'WARNING' | 'CRITICAL'
export type FieldShape = 'RECTANGLE' | 'POLYGON'

export interface Field {
  id: string
  name: string
  shape: FieldShape
  x: number
  y: number
  width: number
  height: number
  points?: { x: number; y: number }[]
  status: FieldStatus
  sensorId?: string
  cropType?: string
  area?: number
}

export interface CreateFieldRequest {
  id: string
  name: string
  shape: FieldShape
  x: number
  y: number
  width: number
  height: number
  points?: { x: number; y: number }[]
  sensorId?: string
  cropType?: string
}

export interface UpdateFieldRequest extends Partial<CreateFieldRequest> {}
