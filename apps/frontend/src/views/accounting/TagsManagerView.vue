<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <div>
        <h1 class="text-2xl font-extrabold text-gray-900 tracking-tight">{{ t('tags_title') }}</h1>
        <p class="text-sm text-gray-400 mt-0.5 font-medium">{{ tags.length }} {{ t('tags_count') || 'tags' }}</p>
      </div>
      <button
        v-if="!showForm"
        @click="showForm = true"
        class="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 active:scale-[0.98] transition-all duration-200 font-semibold text-sm shadow-sm shadow-emerald-200"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
        </svg>
        {{ t('tags_create') }}
      </button>
    </div>

    <!-- Create/Edit Tag Form -->
    <div v-if="showForm" class="bg-white p-5 rounded-2xl shadow-sm border border-gray-100/80">
      <div class="flex flex-wrap gap-4 items-end">
        <div class="flex-1 min-w-[200px]">
          <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('tags_name') }}</label>
          <input
            type="text"
            v-model="form.name"
            required
            class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
            :placeholder="t('tags_name')"
            autofocus
          >
        </div>
        <div>
          <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('tags_color') }}</label>
          <div class="flex items-center gap-3 bg-gray-50 rounded-xl px-3 py-1.5">
            <input type="color" v-model="form.color" class="h-8 w-8 rounded-lg border-0 p-0 cursor-pointer bg-transparent">
            <span class="text-[11px] font-mono text-gray-400 uppercase tracking-wider">{{ form.color }}</span>
          </div>
        </div>
        <!-- Preview -->
        <div class="flex items-end pb-1">
          <span
            v-if="form.name"
            class="inline-flex items-center px-3 py-1.5 rounded-full text-xs font-semibold border"
            :style="{ backgroundColor: form.color + '18', borderColor: form.color + '4D', color: form.color }"
          >{{ form.name }}</span>
        </div>
        <div class="flex gap-2 ml-auto">
          <button @click="save" class="px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 transition-all font-semibold text-sm active:scale-[0.98]">
            {{ editingId ? t('common_save') : t('common_new') }}
          </button>
          <button @click="cancel" class="px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 transition-colors font-semibold text-sm">
            {{ t('cancel') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-16">
      <div class="animate-spin rounded-full h-10 w-10 border-[3px] border-gray-200 border-t-emerald-500"></div>
    </div>

    <!-- Tag Grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
      <div
        v-for="tag in tags"
        :key="tag.id"
        class="group bg-white p-4 rounded-xl shadow-sm border border-gray-100/80 hover:shadow-md hover:border-gray-200 transition-all duration-200 flex items-center justify-between"
      >
        <div class="flex items-center gap-3 min-w-0">
          <div
            :style="{ backgroundColor: tag.color }"
            class="w-3.5 h-3.5 rounded-full flex-shrink-0 ring-2 ring-offset-2"
            :class="'ring-' + tag.color.replace('#', '') + '/20'"
            style="--tw-ring-color: currentColor; --tw-ring-opacity: 0.15;"
          ></div>
          <span class="font-semibold text-gray-800 text-sm truncate">{{ tag.name }}</span>
        </div>
        <div class="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
          <button @click="edit(tag)" class="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931z" />
            </svg>
          </button>
          <button @click="onDelete(tag.id)" class="p-1.5 text-gray-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Empty state -->
      <div v-if="tags.length === 0" class="sm:col-span-2 lg:col-span-3 py-16 flex flex-col items-center gap-3">
        <div class="w-16 h-16 rounded-2xl bg-gray-50 flex items-center justify-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-7 w-7 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9.568 3H5.25A2.25 2.25 0 003 5.25v4.318c0 .597.237 1.17.659 1.591l9.581 9.581c.699.699 1.78.872 2.607.33a18.095 18.095 0 005.223-5.223c.542-.827.369-1.908-.33-2.607L11.16 3.66A2.25 2.25 0 009.568 3z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 6h.008v.008H6V6z" />
          </svg>
        </div>
        <p class="text-sm text-gray-400 font-medium">{{ t('tags_empty') }}</p>
        <button @click="showForm = true" class="text-sm text-emerald-600 font-semibold hover:text-emerald-700">
          {{ t('tags_create') }} &rarr;
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from '@/i18n'
import { useTagsStore } from '@/stores/tags.store'
import { storeToRefs } from 'pinia'
import type { Tag } from '@/types'

const { t } = useI18n()
const store = useTagsStore()
const { tags, loading } = storeToRefs(store)

const showForm = ref(false)
const editingId = ref<string | null>(null)

const form = reactive({
  name: '',
  color: '#6B7280'
})

onMounted(() => {
  store.fetchTags()
})

const save = async () => {
  if (editingId.value) {
    await store.updateTag(editingId.value, form)
  } else {
    await store.createTag(form)
  }
  cancel()
}

const edit = (tag: Tag) => {
  editingId.value = tag.id
  form.name = tag.name
  form.color = tag.color
  showForm.value = true
}

const cancel = () => {
  showForm.value = false
  editingId.value = null
  form.name = ''
  form.color = '#6B7280'
}

const onDelete = async (id: string) => {
  if (confirm(t('plant_delete_confirm'))) {
    await store.deleteTag(id)
  }
}
</script>
