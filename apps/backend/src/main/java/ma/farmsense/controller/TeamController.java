package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.team.*;
import ma.farmsense.entity.User;
import ma.farmsense.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getOwnedTeams(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(teamService.findAllOwned(user));
    }

    @GetMapping("/joined")
    public ResponseEntity<List<TeamResponse>> getJoinedTeams(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(teamService.findAllJoined(user));
    }

    @GetMapping("/invited")
    public ResponseEntity<List<TeamResponse>> getInvitations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(teamService.findAllInvited(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(teamService.findById(user, id));
    }

    @PostMapping
    public ResponseEntity<TeamResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateTeamRequest req) {
        return ResponseEntity.status(201).body(teamService.create(user, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeamRequest req) {
        return ResponseEntity.ok(teamService.update(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        teamService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    // ── Membership ────────────────────────────────────────────────

    @GetMapping("/{id}/members")
    public ResponseEntity<List<TeamMemberResponse>> getMembers(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(teamService.getMembers(user, id));
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<TeamMemberResponse> invite(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody InviteMemberRequest req) {
        return ResponseEntity.status(201).body(teamService.inviteMember(user, id, req));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        teamService.respondToInvitation(user, id, true);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<Void> declineInvitation(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        teamService.respondToInvitation(user, id, false);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @PathVariable UUID memberId) {
        teamService.removeMember(user, id, memberId);
        return ResponseEntity.noContent().build();
    }
}
