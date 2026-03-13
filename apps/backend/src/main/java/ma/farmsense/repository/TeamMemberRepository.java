package ma.farmsense.repository;

import ma.farmsense.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    List<TeamMember> findByTeam(Team team);
    List<TeamMember> findByTeamOrderByRoleAsc(Team team);
    List<TeamMember> findByUserAndStatus(User user, MemberStatus status);
    Optional<TeamMember> findByTeamAndUser(Team team, User user);
    void deleteByTeam(Team team);
}
