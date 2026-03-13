<template>
  <div>
    <label class="block text-sm font-medium text-gray-700 mb-1">{{ t('nav_tags') }}</label>
    <div class="flex flex-wrap gap-2 mb-2">
      <TagChip
        v-for="tagId in modelValue"
        :key="tagId"
        :tag="getTag(tagId)"
        removable
        @remove="removeTag(tagId)"
      />
    </div>
    <select
      @change="addTag(($event.target as HTMLSelectElement).value)"
      class="block w-full rounded-lg border-gray-300 shadow-sm focus:border-green-500 focus:ring-green-500 sm:text-sm"
    >
      <option value="">{{ t('common_select') }}</option>
      <option
        v-for="tag in availableTags"
        :key="tag.id"
        :value="tag.id"
      >
        {{ tag.name }}
      </option>
    </select>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from '@/i18n'
import type { Tag } from '@/types'
import TagChip from './TagChip.vue'

const props = defineProps<{
  modelValue: string[]
  allTags: Tag[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string[]): void
}>()

const { t } = useI18n()

const availableTags = computed(() => 
  props.allTags.filter(t => !props.modelValue.includes(t.id))
)

const getTag = (id: string) => props.allTags.find(t => t.id === id) || { id, name: 'Unknown', color: '#6B7280' }

const addTag = (id: string) => {
  if (id && !props.modelValue.includes(id)) {
    emit('update:modelValue', [...props.modelValue, id])
  }
}

const removeTag = (id: string) => {
  emit('update:modelValue', props.modelValue.filter(tid => tid !== id))
}
</script>
