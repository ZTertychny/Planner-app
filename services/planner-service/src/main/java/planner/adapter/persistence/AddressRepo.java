package planner.adapter.persistence;

import dto.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import planner.domain.Address;

import java.util.Optional;

public interface AddressRepo extends JpaRepository<Address, Long> {
    Optional<Address> findByUserIdAndType(Long userId, AddressType type);
}
