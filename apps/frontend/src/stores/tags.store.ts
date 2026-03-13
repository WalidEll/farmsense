import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/services/api'
import type { Tag, CreateTagRequest, UpdateTagRequest } from '@/types'

export const useTagsStore = defineStore('tags', () => {
  const tags = ref<Tag[]>([])
  const loading = ref(false)

  async function fetchTags() {
    loading.value = true
    try {
      tags.value = await api.get('/tags')
    } finally {
      loading.value = false
    }
  }

  async function createTag(req: CreateTagRequest): Promise<Tag> {
    const data = await api.post('/tags', req)
    tags.value.push(data)
    tags.value.sort((a, b) => a.name.localeCompare(b.name))
    return data
  }

  async function updateTag(id: string, req: UpdateTagRequest): Promise<Tag> {
    const data = await api.put(`/tags/${id}`, req)
    const idx = tags.value.findIndex(t => t.id === id)
    if (idx !== -1) tags.value[idx] = data
    tags.value.sort((a, b) => a.name.localeCompare(b.name))
    return data
  }

  async function deleteTag(id: string) {
    await api.delete(`/tags/${id}`)
    tags.value = tags.value.filter(t => t.id !== id)
  }

  return { tags, loading, fetchTags, createTag, updateTag, deleteTag }
})
