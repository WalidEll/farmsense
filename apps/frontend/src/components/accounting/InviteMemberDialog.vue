<template>
  <Teleport to="body">
    <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="fixed inset-0 bg-gray-900/40 backdrop-blur-sm" @click="$emit('close')"></div>
      <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
        <form @submit.prevent="submit">
          <div class="p-6 space-y-5">
            <div class="flex items-center gap-3">
              <div class="p-2.5 bg-blue-50 rounded-xl">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600" viewBox="0 0 20 20" fill="currentColor">
                  <path d="M8 9a3 3 0 100-6 3 3 0 000 6zM8 11a6 6 0 016 6H2a6 6 0 016-6zM16 7a1 1 0 10-2 0v1h-1a1 1 0 100 2h1v1a1 1 0 102 0v-1h1a1 1 0 100-2h-1V7z" />
                </svg>
              </div>
              <h3 class="text-lg font-bold text-gray-900">{{ t('team_invite') }}</h3>
            </div>

            <div class="space-y-4">
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('auth_email') }}</label>
                <input
                  type="email"
                  v-model="form.email"
                  required
                  class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-blue-400 focus:ring-blue-400 text-sm py-2.5"
                  placeholder="colleague@example.com"
                  autofocus
                >
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">Role</label>
                <div class="grid grid-cols-2 gap-2">
                  <button
                    type="button"
                    @click="form.role = 'VIEWER'"
                    :class="[
                      form.role === 'VIEWER'
                        ? 'bg-gray-900 text-white border-gray-900'
                        : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50',
                      'px-4 py-2.5 rounded-xl border text-sm font-semibold transition-all'
                    ]"
                  >
                    {{ t('team_role_viewer') }}
                  </button>
                  <button
                    type="button"
                    @click="form.role = 'MANAGER'"
                    :class="[
                      form.role === 'MANAGER'
                        ? 'bg-sky-600 text-white border-sky-600'
                        : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50',
                      'px-4 py-2.5 rounded-xl border text-sm font-semibold transition-all'
                    ]"
                  >
                    {{ t('team_role_manager') }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="flex gap-3 px-6 py-4 bg-gray-50/80 border-t border-gray-100">
            <button
              type="submit"
              class="flex-1 px-5 py-2.5 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-all font-semibold text-sm active:scale-[0.98]"
            >
              {{ t('team_invite') }}
            </button>
            <button
              type="button"
              @click="$emit('close')"
              class="flex-1 px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 transition-colors font-semibold text-sm"
            >
              {{ t('cancel') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useI18n } from '@/i18n'
import type { InviteMemberRequest } from '@/types'

defineProps<{
  isOpen: boolean
}>()

const { t } = useI18n()

const form = reactive<InviteMemberRequest>({
  email: '',
  role: 'VIEWER'
})

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', data: InviteMemberRequest): void
}>()

const submit = () => {
  emit('submit', { ...form })
  form.email = ''
}
</script>
