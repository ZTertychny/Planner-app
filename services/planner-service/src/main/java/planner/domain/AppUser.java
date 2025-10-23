package planner.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_app_user")
    @SequenceGenerator(name = "sq_app_user", allocationSize = 1)
    private Long id;
    @Column(name = "tg_user_id", nullable = false, unique = true)
    private Long tgUserId;
    @Column(name = "tg_chat_id", nullable = false)
    private Long tgChatId;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
