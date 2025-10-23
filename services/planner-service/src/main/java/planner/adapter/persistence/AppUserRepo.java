package planner.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import planner.domain.AppUser;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTgUserId(Long tgUserId);
}
