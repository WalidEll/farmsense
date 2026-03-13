<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <div>
        <h1 class="text-2xl font-extrabold text-gray-900 tracking-tight">{{ t('team_title') }}</h1>
        <p class="text-sm text-gray-400 mt-0.5 font-medium">
          {{ currentTeam ? teamMembers.length + ' ' + t('team_members_count') : '' }}
        </p>
      </div>
      <button
        v-if="!currentTeam"
        @click="showCreateModal = true"
        class="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 active:scale-[0.98] transition-all duration-200 font-semibold text-sm shadow-sm shadow-emerald-200"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
        </svg>
        {{ t('team_create_team') }}
      </button>
    </div>

    <!-- Pending Invitations -->
    <PendingInviteBanner :invites="pendingInvites" @accept="onAcceptInvite" />

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-16">
      <div class="animate-spin rounded-full h-10 w-10 border-[3px] border-gray-200 border-t-emerald-500"></div>
    </div>

    <!-- Team Exists -->
    <div v-else-if="currentTeam" class="space-y-6">
      <TeamCard :team="currentTeam" @invite="showInviteModal = true" />

      <!-- Members Section -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100/80 overflow-hidden hover:shadow-md transition-shadow duration-300">
        <div class="flex justify-between items-center px-6 py-5 border-b border-gray-50">
          <div class="flex items-center gap-3">
            <div class="p-2 bg-gray-50 rounded-lg">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 018.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-1.053M18 8.625a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zM15.75 9a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0z" />
              </svg>
            </div>
            <h3 class="text-base font-bold text-gray-800">{{ t('team_members') }}</h3>
          </div>
          <span class="text-xs font-bold text-gray-400 bg-gray-50 px-2.5 py-1 rounded-lg">
            {{ teamMembers.length }} {{ t('team_members_count') }}
          </span>
        </div>

        <!-- Members List -->
        <div class="divide-y divide-gray-50">
          <div
            v-for="member in teamMembers"
            :key="member.id"
            class="group flex items-center justify-between px-6 py-4 hover:bg-gray-50/50 transition-colors"
          >
            <div class="flex items-center gap-4 min-w-0">
              <div :class="[
                member.role === 'OWNER' ? 'bg-violet-100 text-violet-700' :
                member.role === 'MANAGER' ? 'bg-sky-100 text-sky-700' : 'bg-gray-100 text-gray-600',
                'h-10 w-10 rounded-xl flex items-center justify-center font-bold text-xs uppercase flex-shrink-0'
              ]">
                {{ member.userName.substring(0, 2) }}
              </div>
              <div class="min-w-0">
                <p class="text-sm font-semibold text-gray-900 truncate">{{ member.userName }}</p>
                <p class="text-[11px] text-gray-400 font-medium truncate">{{ member.userEmail }}</p>
              </div>
            </div>

            <div class="flex items-center gap-3">
              <!-- Role Badge -->
              <span :class="[
                member.role === 'OWNER' ? 'bg-violet-50 text-violet-700 border-violet-200' :
                member.role === 'MANAGER' ? 'bg-sky-50 text-sky-700 border-sky-200' : 'bg-gray-50 text-gray-600 border-gray-200',
                'px-2.5 py-1 rounded-lg text-[11px] font-bold uppercase tracking-wider border'
              ]">
                {{ t('team_role_' + member.role.toLowerCase()) }}
              </span>

              <!-- Status -->
              <span :class="[
                member.status === 'ACTIVE' ? 'text-emerald-600' : 'text-amber-500',
                'text-[11px] font-semibold'
              ]">
                {{ member.status === 'ACTIVE' ? t('team_status_active') : t('team_status_pending') }}
              </span>

              <!-- Remove button -->
              <button
                v-if="member.role !== 'OWNER'"
                @click="onRemoveMember(member.id)"
                class="p-1.5 text-gray-300 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors opacity-0 group-hover:opacity-100"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- Empty Members -->
        <div v-if="teamMembers.length === 0" class="py-12 flex flex-col items-center gap-3">
          <div class="w-12 h-12 rounded-xl bg-gray-50 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M18 18.72a9.094 9.094 0 003.741-.479 3 3 0 00-4.682-2.72m.94 3.198l.001.031c0 .225-.012.447-.037.666A11.944 11.944 0 0112 21c-2.17 0-4.207-.576-5.963-1.584A6.062 6.062 0 016 18.719m12 0a5.971 5.971 0 00-.941-3.197m0 0A5.995 5.995 0 0012 12.75a5.995 5.995 0 00-5.058 2.772m0 0a3 3 0 00-4.681 2.72 8.986 8.986 0 003.74.477m.94-3.197a5.971 5.971 0 00-.94 3.197M15 6.75a3 3 0 11-6 0 3 3 0 016 0zm6 3a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0zm-13.5 0a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z" />
            </svg>
          </div>
          <p class="text-sm text-gray-400 font-medium">{{ t('team_invite') }}</p>
        </div>
      </div>
    </div>

    <!-- No Team Empty State -->
    <div v-else class="py-16 flex flex-col items-center gap-4">
      <div class="w-20 h-20 rounded-2xl bg-gradient-to-br from-emerald-50 to-green-50 flex items-center justify-center border border-emerald-100/60">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-9 w-9 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M18 18.72a9.094 9.094 0 003.741-.479 3 3 0 00-4.682-2.72m.94 3.198l.001.031c0 .225-.012.447-.037.666A11.944 11.944 0 0112 21c-2.17 0-4.207-.576-5.963-1.584A6.062 6.062 0 016 18.719m12 0a5.971 5.971 0 00-.941-3.197m0 0A5.995 5.995 0 0012 12.75a5.995 5.995 0 00-5.058 2.772m0 0a3 3 0 00-4.681 2.72 8.986 8.986 0 003.74.477m.94-3.197a5.971 5.971 0 00-.94 3.197M15 6.75a3 3 0 11-6 0 3 3 0 016 0zm6 3a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0zm-13.5 0a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z" />
        </svg>
      </div>
      <div class="text-center max-w-sm">
        <h3 class="text-lg font-bold text-gray-900 mb-1">{{ t('team_no_team') }}</h3>
        <p class="text-sm text-gray-400 leading-relaxed">{{ t('team_no_team_desc') }}</p>
      </div>
      <button
        @click="showCreateModal = true"
        class="inline-flex items-center gap-2 px-6 py-3 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 active:scale-[0.98] transition-all duration-200 font-semibold text-sm shadow-sm shadow-emerald-200 mt-2"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
        </svg>
        {{ t('team_create_team') }}
      </button>
    </div>

    <!-- Create Team Modal -->
    <Teleport to="body">
      <div v-if="showCreateModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="fixed inset-0 bg-gray-900/40 backdrop-blur-sm" @click="showCreateModal = false"></div>
        <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
          <div class="p-6 space-y-5">
            <div class="flex items-center gap-3">
              <div class="p-2.5 bg-emerald-50 rounded-xl">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M18 18.72a9.094 9.094 0 003.741-.479 3 3 0 00-4.682-2.72m.94 3.198l.001.031c0 .225-.012.447-.037.666A11.944 11.944 0 0112 21c-2.17 0-4.207-.576-5.963-1.584A6.062 6.062 0 016 18.719m12 0a5.971 5.971 0 00-.941-3.197m0 0A5.995 5.995 0 0012 12.75a5.995 5.995 0 00-5.058 2.772m0 0a3 3 0 00-4.681 2.72 8.986 8.986 0 003.74.477m.94-3.197a5.971 5.971 0 00-.94 3.197M15 6.75a3 3 0 11-6 0 3 3 0 016 0zm6 3a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0zm-13.5 0a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z" />
                </svg>
              </div>
              <h3 class="text-lg font-bold text-gray-900">{{ t('team_create_team') }}</h3>
            </div>

            <div class="space-y-4">
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('team_name') }}</label>
                <input
                  type="text"
                  v-model="teamForm.name"
                  class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
                  :placeholder="t('team_name')"
                  autofocus
                >
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">{{ t('team_description') }}</label>
                <textarea
                  v-model="teamForm.description"
                  rows="3"
                  class="block w-full rounded-xl border-gray-200 shadow-sm focus:border-emerald-400 focus:ring-emerald-400 text-sm py-2.5"
                  :placeholder="t('team_description')"
                ></textarea>
              </div>
            </div>
          </div>

          <div class="flex gap-3 px-6 py-4 bg-gray-50/80 border-t border-gray-100">
            <button
              @click="onCreateTeam"
              class="flex-1 px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 transition-all font-semibold text-sm active:scale-[0.98]"
            >
              {{ t('team_create_team') }}
            </button>
            <button
              @click="showCreateModal = false"
              class="flex-1 px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 transition-colors font-semibold text-sm"
            >
              {{ t('cancel') }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <InviteMemberDialog
      :is-open="showInviteModal"
      @close="showInviteModal = false"
      @submit="onInviteMember"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from '@/i18n'
import { useTeamsStore } from '@/stores/teams.store'
import { storeToRefs } from 'pinia'
import TeamCard from '@/components/accounting/TeamCard.vue'
import InviteMemberDialog from '@/components/accounting/InviteMemberDialog.vue'
import PendingInviteBanner from '@/components/accounting/PendingInviteBanner.vue'
import type { InviteMemberRequest } from '@/types'

const { t } = useI18n()
const store = useTeamsStore()
const { currentTeam, teamMembers, pendingInvites, loading } = storeToRefs(store)

const showCreateModal = ref(false)
const showInviteModal = ref(false)
const teamForm = reactive({ name: '', description: '' })

onMounted(() => {
  store.fetchTeams()
  store.fetchPendingInvites()
})

const onCreateTeam = async () => {
  await store.createTeam(teamForm)
  showCreateModal.value = false
}

const onInviteMember = async (data: InviteMemberRequest) => {
  if (currentTeam.value) {
    await store.inviteMember(currentTeam.value.id, data)
    showInviteModal.value = false
  }
}

const onAcceptInvite = async (memberId: string) => {
  if (currentTeam.value) {
    await store.acceptInvite(currentTeam.value.id)
    store.fetchPendingInvites()
  }
}

const onRemoveMember = async (memberId: string) => {
  if (currentTeam.value && confirm(t('team_remove_member'))) {
    await store.removeMember(currentTeam.value.id, memberId)
  }
}
</script>
