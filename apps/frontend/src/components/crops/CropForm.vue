<template>
  <form @submit.prevent="onSubmit" class="space-y-4">
    <!-- Name fields -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crop_name') }} *</label>
        <input
          v-model="form.name"
          required
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crop_nameAr') }}</label>
        <input
          v-model="form.nameAr"
          dir="rtl"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crop_nameDarija') }}</label>
        <input
          v-model="form.nameDarija"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
        />
      </div>
    </div>

    <!-- Scientific name -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crop_scientificName') }}</label>
      <input
        v-model="form.scientificName"
        class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
      />
    </div>

    <!-- Category + Difficulty -->
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crop_category') }} *</label>
        <select
          v-model="form.category"
          required
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
        >
          <option value="">{{ t('common_select') }}</option>
          <option v-for="cat in categoryOptions" :key="cat" :value="cat">
            {{ t(`crops_${cat.toLowerCase()}`) }}
          </option>
        </select>
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crops_difficulty') }}</label>
        <select
          v-model="form.difficulty"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
        >
          <option value="">{{ t('common_select') }}</option>
          <option v-for="d in difficultyOptions" :key="d" :value="d">
            {{ t(`crop_difficulty_${d.toLowerCase()}`) }}
          </option>
        </select>
      </div>
    </div>

    <!-- Descriptions -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crop_description') }}</label>
      <textarea
        v-model="form.description"
        rows="3"
        class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
      ></textarea>
    </div>
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crop_description') }} ({{ t('crop_nameAr') }})</label>
        <textarea
          v-model="form.descriptionAr"
          dir="rtl"
          rows="2"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
        ></textarea>
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crop_description') }} ({{ t('crop_nameDarija') }})</label>
        <textarea
          v-model="form.descriptionDarija"
          rows="2"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
        ></textarea>
      </div>
    </div>

    <!-- Image URL -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crop_imageUrl') }}</label>
      <input
        v-model="form.imageUrl"
        type="url"
        class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
      />
    </div>

    <!-- Growing season + Days to harvest -->
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crop_growingSeason') }}</label>
        <input
          v-model="form.growingSeason"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('crops_daysToHarvest') }}</label>
        <input
          v-model.number="form.daysToHarvest"
          type="number"
          min="0"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500"
        />
      </div>
    </div>

    <!-- Actions -->
    <div class="flex items-center gap-3 pt-2">
      <button
        type="submit"
        :disabled="saving"
        class="px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 disabled:opacity-50 transition-colors"
      >
        {{ saving ? t('loading') : t('common_save') }}
      </button>
      <button
        type="button"
        @click="$emit('cancelled')"
        class="px-4 py-2 bg-gray-100 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-200 transition-colors"
      >
        {{ t('common_cancel') }}
      </button>
    </div>
  </form>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { useI18n } from '@/i18n'
import { useCropsStore } from '@/stores/crops.store'
import type { Crop, CropCategory, CropDifficulty } from '@/types'

const props = defineProps<{ crop?: Crop }>()
const emit = defineEmits<{ saved: [crop: Crop]; cancelled: [] }>()

const { t } = useI18n()
const store = useCropsStore()
const saving = ref(false)

const categoryOptions: CropCategory[] = ['VEGETABLE', 'FRUIT', 'HERB', 'GRAIN', 'LEGUME', 'OTHER']
const difficultyOptions: CropDifficulty[] = ['EASY', 'MEDIUM', 'HARD']

const form = reactive({
  name: '',
  nameAr: '',
  nameDarija: '',
  scientificName: '',
  category: '' as CropCategory | '',
  description: '',
  descriptionAr: '',
  descriptionDarija: '',
  imageUrl: '',
  growingSeason: '',
  daysToHarvest: undefined as number | undefined,
  difficulty: '' as CropDifficulty | '',
})

function populateForm(crop?: Crop) {
  if (crop) {
    form.name = crop.name
    form.nameAr = crop.nameAr || ''
    form.nameDarija = crop.nameDarija || ''
    form.scientificName = crop.scientificName || ''
    form.category = crop.category
    form.description = crop.description || ''
    form.descriptionAr = crop.descriptionAr || ''
    form.descriptionDarija = crop.descriptionDarija || ''
    form.imageUrl = crop.imageUrl || ''
    form.growingSeason = crop.growingSeason || ''
    form.daysToHarvest = crop.daysToHarvest
    form.difficulty = crop.difficulty || ''
  } else {
    form.name = ''
    form.nameAr = ''
    form.nameDarija = ''
    form.scientificName = ''
    form.category = ''
    form.description = ''
    form.descriptionAr = ''
    form.descriptionDarija = ''
    form.imageUrl = ''
    form.growingSeason = ''
    form.daysToHarvest = undefined
    form.difficulty = ''
  }
}

watch(() => props.crop, (c) => populateForm(c), { immediate: true })

async function onSubmit() {
  if (!form.name || !form.category) return
  saving.value = true
  try {
    const payload = {
      name: form.name,
      nameAr: form.nameAr || undefined,
      nameDarija: form.nameDarija || undefined,
      scientificName: form.scientificName || undefined,
      category: form.category as CropCategory,
      description: form.description || undefined,
      descriptionAr: form.descriptionAr || undefined,
      descriptionDarija: form.descriptionDarija || undefined,
      imageUrl: form.imageUrl || undefined,
      growingSeason: form.growingSeason || undefined,
      daysToHarvest: form.daysToHarvest || undefined,
      difficulty: (form.difficulty || undefined) as CropDifficulty | undefined,
    }
    let result: Crop
    if (props.crop) {
      result = await store.update(props.crop.id, payload)
    } else {
      result = await store.create(payload)
    }
    emit('saved', result)
  } finally {
    saving.value = false
  }
}
</script>
