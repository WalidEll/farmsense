import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/services/api'
import type { Team, CreateTeamRequest, UpdateTeamRequest, TeamMember, InviteMemberRequest } from '@/types'

export const useTeamsStore = defineStore('teams', () => {
  const ownedTeams = ref<Team[]>([])
  const joinedTeams = ref<Team[]>([])
  const invitations = ref<Team[]>([])
  const members = ref<TeamMember[]>([])
  const loading = ref(false)

  // Computed properties used by TeamManagementView
  const currentTeam = computed(() => ownedTeams.value[0] || joinedTeams.value[0] || null)
  const teamMembers = computed(() => members.value)
  const pendingInvites = computed(() => invitations.value)

  async function fetchTeams() {
    loading.value = true
    try {
      ownedTeams.value = await api.get('/teams')
      joinedTeams.value = await api.get('/teams/joined')
      invitations.value = await api.get('/teams/invited')
      // Auto-fetch members for current team
      if (currentTeam.value) {
        await fetchMembers(currentTeam.value.id)
      }
    } finally {
      loading.value = false
    }
  }

  async function fetchPendingInvites() {
    invitations.value = await api.get('/teams/invited')
  }

  async function acceptInvite(teamId: string) {
    await api.post(`/teams/${teamId}/accept?accept=true`, {})
    await fetchTeams()
  }

  async function createTeam(req: CreateTeamRequest): Promise<Team> {
    const data = await api.post('/teams', req)
    ownedTeams.value.unshift(data)
    return data
  }

  async function updateTeam(id: string, req: UpdateTeamRequest): Promise<Team> {
    const data = await api.put(`/teams/${id}`, req)
    const idx = ownedTeams.value.findIndex(t => t.id === id)
    if (idx !== -1) ownedTeams.value[idx] = data
    return data
  }

  async function deleteTeam(id: string) {
    await api.delete(`/teams/${id}`)
    ownedTeams.value = ownedTeams.value.filter(t => t.id !== id)
  }

  async function fetchMembers(teamId: string) {
    members.value = await api.get(`/teams/${teamId}/members`)
  }

  async function inviteMember(teamId: string, req: InviteMemberRequest): Promise<TeamMember> {
    const data = await api.post(`/teams/${teamId}/invite`, req)
    members.value.push(data)
    return data
  }

  async function respondToInvite(teamId: string, accept: boolean) {
    await api.post(`/teams/${teamId}/accept?accept=${accept}`, {})
    await fetchTeams()
  }

  async function removeMember(teamId: string, memberId: string) {
    await api.delete(`/teams/${teamId}/members/${memberId}`)
    members.value = members.value.filter(m => m.id !== memberId)
  }

  return {
    ownedTeams, joinedTeams, invitations, members, loading,
    currentTeam, teamMembers, pendingInvites,
    fetchTeams, fetchPendingInvites, acceptInvite,
    createTeam, updateTeam, deleteTeam,
    fetchMembers, inviteMember, respondToInvite, removeMember
  }
})
