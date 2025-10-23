package planner.app;

import dto.FormalizedAddress;
import dto.GeoDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import planner.adapter.out.geodata.DaDataAdapter;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeoDataUseCase {
    private final DaDataAdapter daDataAdapter;

    public Optional<FormalizedAddress> formalizeAddress(GeoDataDto data) {
        return daDataAdapter.getCleanAddress(data);
    }
}
