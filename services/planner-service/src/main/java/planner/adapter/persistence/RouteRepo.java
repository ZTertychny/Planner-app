package planner.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import planner.domain.Route;

import java.util.Optional;

public interface RouteRepo extends JpaRepository<Route, Long> {
    Optional<Route> findByUserId(Long userId);
}
