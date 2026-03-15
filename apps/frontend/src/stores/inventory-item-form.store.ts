import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/services/api'
import type {
  InventoryItem,
  CreateInventoryItemRequest,
  UpdateInventoryItemRequest,
  InventoryCategory,
  QualityGrade,
  StockStatus
} from '@/types'

interface FormState {
  id?: string
  name: string
  code: string
  categoryType: string
  unitCost: number
  minQuantity: number
  maxQuantity: number
  reorderLevel: number
  description?: string
  status: StockStatus | 'UNKNOWN'
  qualityGrade?: QualityGrade

  // Form state flags
  isEditMode: boolean
  isLoading: boolean
  isValid: boolean
}

export const useInventoryItemFormStore = defineStore('inventory-item-form', () => {
  // ── Form State ──
  const form: Ref<FormState> = ref({
    name: '',
    code: '',
    categoryType: '',
    unitCost: 0,
    minQuantity: 0,
    maxQuantity: 0,
    reorderLevel: 0,
    description: '',
    status: 'UNKNOWN',
    qualityGrade: undefined,
    isEditMode: false,
    isLoading: false,
    isValid: false
  })

  const categories = ref<InventoryCategory[]>([])

  // ── Computed ──
  const isValidated = computed(() => {
    return (
      form.value.name.length > 0 &&
      form.value.code.length > 0 &&
      form.value.categoryType !== '' &&
      form.value.unitCost >= 0 &&
      form.value.minQuantity >= 0 &&
      form.value.maxQuantity >= form.value.minQuantity &&
      form.value.reorderLevel >= 0
    )
  })

  const isDirty = computed(() => {
    return Object.keys(form.value).some(key => {
      const currentValue = (form.value as any)[key]
      const defaultValue = getDefaultValue(key)
      return currentValue !== defaultValue
    })
  })

  // ── Reset ──
  function resetForm() {
    form.value = {
      name: '',
      code: '',
      categoryType: '',
      unitCost: 0,
      minQuantity: 0,
      maxQuantity: 0,
      reorderLevel: 0,
      description: '',
      status: 'UNKNOWN',
      qualityGrade: undefined,
      isEditMode: false,
      isLoading: false,
      isValid: false
    }
  }

  function getDefaultValue(key: string): any {
    const field = key as keyof FormState
    return form.value[field] === undefined ? '' : (form.value[field] as number) === 0 ? 0 : (form.value[field] as boolean) ? false : form.value[field]
  }

  // ── Validation ──
  function validateForm(): boolean {
    const errors: string[] = []
    let valid = true

    if (!form.value.name || form.value.name.trim().length === 0) {
      errors.push('Name is required')
      valid = false
    } else if (form.value.name.length > 255) {
      errors.push('Name must be less than 255 characters')
      valid = false
    }

    if (!form.value.code || form.value.code.trim().length === 0) {
      errors.push('Code is required')
      valid = false
    } else if (/[^a-zA-Z0-9-_]/.test(form.value.code)) {
      errors.push('Code can only contain letters, numbers, hyphens, and underscores')
      valid = false
    }

    if (!form.value.categoryType) {
      errors.push('Category is required')
      valid = false
    }

    if (form.value.unitCost < 0) {
      errors.push('Unit cost cannot be negative')
      valid = false
    }

    if (form.value.minQuantity < 0 || form.value.reorderLevel < 0) {
      errors.push('Quantities cannot be negative')
      valid = false
    }

    if (form.value.maxQuantity < form.value.minQuantity) {
      errors.push('Maximum quantity must be greater than minimum quantity')
      valid = false
    }

    form.value.isValid = valid
    return valid
  }

  // ── CRUD Operations ──
  async function fetchCategoryTypes() {
    categories.value = await api.get('/inventory/categories/types')
  }

  async function saveItem(): Promise<InventoryItem | null> {
    if (!validateForm()) {
      return null
    }

    form.value.isLoading = true
    try {
      let payload: CreateInventoryItemRequest | UpdateInventoryItemRequest

      if (form.value.isEditMode && form.value.id) {
        payload = {
          name: form.value.name,
          code: form.value.code,
          categoryType: form.value.categoryType,
          unitCost: form.value.unitCost,
          minQuantity: form.value.minQuantity,
          maxQuantity: form.value.maxQuantity,
          reorderLevel: form.value.reorderLevel,
          description: form.value.description || undefined,
          qualityGrade: form.value.qualityGrade || undefined
        } as UpdateInventoryItemRequest

        const data = await api.put<InventoryItem>(`/inventory/items/${form.value.id}`, payload)
        return data
      } else {
        payload = {
          name: form.value.name,
          code: form.value.code,
          categoryType: form.value.categoryType,
          unitCost: form.value.unitCost,
          minQuantity: form.value.minQuantity,
          maxQuantity: form.value.maxQuantity,
          reorderLevel: form.value.reorderLevel,
          description: form.value.description || undefined,
          qualityGrade: form.value.qualityGrade || undefined
        } as CreateInventoryItemRequest

        const data = await api.post<InventoryItem>('/inventory/items', payload)
        return data
      }
    } finally {
      form.value.isLoading = false
    }
  }

  async function cancelEdit() {
    resetForm()
  }

  // ── Form Data Management ──
  function loadItem(item: InventoryItem): void {
    form.value = {
      id: item.id,
      name: item.name || '',
      code: item.code || '',
      categoryType: item.categoryType || '',
      unitCost: item.unitCost || 0,
      minQuantity: item.minQuantity || 0,
      maxQuantity: item.maxQuantity || 0,
      reorderLevel: item.reorderLevel || 0,
      description: item.description || '',
      status: item.status || 'UNKNOWN',
      qualityGrade: item.qualityGrade || undefined,
      isEditMode: true,
      isLoading: false,
      isValid: true
    }
  }

  function setField<T extends keyof FormState>(field: T, value: FormState[T]): void {
    form.value[field] = value
  }

  function toggleEditMode(): void {
    form.value.isEditMode = !form.value.isEditMode
    if (!form.value.isEditMode) {
      form.value.id = undefined
    }
  }

  return {
    form,
    categories,
    isValidated,
    isDirty,
    resetForm,
    validateForm,
    fetchCategoryTypes,
    saveItem,
    cancelEdit,
    loadItem,
    setField,
    toggleEditMode
  }
})
