package planner.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "route")
@Accessors(chain = true)
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_route")
    @SequenceGenerator(name = "sq_route", allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_location_id", nullable = false)
    private Address original;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Address destination;
    @Column(name = "mode", length = 32)
    private String mode;
    @Column(name = "arrived_by_local", nullable = false)
    private LocalTime arrivedByLocal;
    @Column(name = "tz_id", nullable = false)
    private String tzId;                                          // "+HH:MM"
}
