package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.team.*;
import ma.farmsense.entity.*;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.TeamMemberRepository;
import ma.farmsense.repository.TeamRepository;
import ma.farmsense.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public List<TeamResponse> findAllOwned(User user) {
        return teamRepository.findByOwnerOrderByCreatedAtDesc(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TeamResponse> findAllJoined(User user) {
        return teamMemberRepository.findByUserAndStatus(user, MemberStatus.ACTIVE).stream()
                .map(tm -> mapToResponse(tm.getTeam()))
                .toList();
    }

    public List<TeamResponse> findAllInvited(User user) {
        return teamMemberRepository.findByUserAndStatus(user, MemberStatus.PENDING).stream()
                .map(tm -> mapToResponse(tm.getTeam()))
                .toList();
    }

    public TeamResponse findById(User user, UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Team not found"));
        
        // Check if owner or member
        boolean isMember = teamMemberRepository.findByTeamAndUser(team, user)
                .map(tm -> tm.getStatus() == MemberStatus.ACTIVE)
                .orElse(false);
        
        if (!team.getOwner().getId().equals(user.getId()) && !isMember) {
            throw AppException.notFound("Team not found");
        }
        
        return mapToResponse(team);
    }

    @Transactional
    public TeamResponse create(User user, CreateTeamRequest req) {
        Team team = Team.builder()
                .owner(user)
                .name(req.name())
                .description(req.description())
                .build();
        
        Team saved = teamRepository.save(team);
        
        // Add owner as admin member
        TeamMember ownerMember = TeamMember.builder()
                .team(saved)
                .user(user)
                .role(TeamRole.OWNER)
                .status(MemberStatus.ACTIVE)
                .joinedAt(Instant.now())
                .build();
        teamMemberRepository.save(ownerMember);
        
        return mapToResponse(saved);
    }

    @Transactional
    public TeamResponse update(User user, UUID id, UpdateTeamRequest req) {
        Team team = getOwnedTeam(user, id);
        
        if (req.name() != null) team.setName(req.name());
        if (req.description() != null) team.setDescription(req.description());
        
        return mapToResponse(teamRepository.save(team));
    }

    @Transactional
    public void delete(User user, UUID id) {
        Team team = getOwnedTeam(user, id);
        // Cascading delete might be handled by JPA or DB
        teamMemberRepository.deleteByTeam(team);
        teamRepository.delete(team);
    }

    // ── Membership ────────────────────────────────────────────────

    public List<TeamMemberResponse> getMembers(User user, UUID teamId) {
        Team team = getTeamIfMember(user, teamId);
        return teamMemberRepository.findByTeamOrderByRoleAsc(team).stream()
                .map(this::mapToMemberResponse)
                .toList();
    }

    @Transactional
    public TeamMemberResponse inviteMember(User user, UUID teamId, InviteMemberRequest req) {
        Team team = getOwnedTeam(user, teamId);
        
        User invitee = userRepository.findByEmail(req.email())
                .orElseThrow(() -> AppException.notFound("User with email " + req.email() + " not found"));
        
        if (teamMemberRepository.findByTeamAndUser(team, invitee).isPresent()) {
            throw AppException.conflict("User is already a member or has a pending invitation");
        }
        
        TeamMember member = TeamMember.builder()
                .team(team)
                .user(invitee)
                .role(req.role())
                .status(MemberStatus.PENDING)
                .invitedBy(user)
                .invitedAt(Instant.now())
                .build();
        
        return mapToMemberResponse(teamMemberRepository.save(member));
    }

    @Transactional
    public void respondToInvitation(User user, UUID teamId, boolean accept) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> AppException.notFound("Team not found"));
        
        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> AppException.notFound("Invitation not found"));
        
        if (member.getStatus() != MemberStatus.PENDING) {
            throw AppException.conflict("Invitation already processed");
        }
        
        if (accept) {
            member.setStatus(MemberStatus.ACTIVE);
            member.setJoinedAt(Instant.now());
            teamMemberRepository.save(member);
        } else {
            teamMemberRepository.delete(member);
        }
    }

    @Transactional
    public void removeMember(User user, UUID teamId, UUID memberId) {
        Team team = getOwnedTeam(user, teamId);
        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> AppException.notFound("Member not found"));
        
        if (!member.getTeam().getId().equals(team.getId())) {
            throw AppException.notFound("Member not found in this team");
        }
        
        if (member.getUser().getId().equals(user.getId())) {
            throw AppException.conflict("Owner cannot remove themselves. Delete team instead.");
        }
        
        teamMemberRepository.delete(member);
    }

    // ── Helpers ───────────────────────────────────────────────────

    private Team getOwnedTeam(User user, UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Team not found"));
        if (!team.getOwner().getId().equals(user.getId())) {
            throw AppException.notFound("Team not found or you are not the owner");
        }
        return team;
    }

    private Team getTeamIfMember(User user, UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Team not found"));
        
        boolean isMember = teamMemberRepository.findByTeamAndUser(team, user).isPresent();
        if (!isMember && !team.getOwner().getId().equals(user.getId())) {
            throw AppException.notFound("Team not found");
        }
        return team;
    }

    private TeamResponse mapToResponse(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getOwner().getId(),
                team.getOwner().getFullName(),
                team.getName(),
                team.getDescription(),
                team.getCreatedAt(),
                team.getUpdatedAt()
        );
    }

    private TeamMemberResponse mapToMemberResponse(TeamMember tm) {
        return new TeamMemberResponse(
                tm.getId(),
                tm.getUser().getId(),
                tm.getUser().getFullName(),
                tm.getUser().getEmail(),
                tm.getRole(),
                tm.getStatus(),
                null,
                null,
                tm.getJoinedAt()
        );
    }
}
