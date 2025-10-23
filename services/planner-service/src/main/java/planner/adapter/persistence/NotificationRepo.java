package planner.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import planner.domain.Notification;
import planner.domain.NotificationStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    Optional<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);

    List<Notification> findTop200ByStatusAndFireAtLessThanEqualOrderByFireAtAsc(
            NotificationStatus status, Instant threshold);
}
