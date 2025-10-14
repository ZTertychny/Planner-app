package planner.adapter.in;

import dto.FormalizedAddress;
import dto.GeoDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import planner.app.GeoDataUseCase;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GeoDataController {
    private final GeoDataUseCase geoDataUseCase;

    @PostMapping("/geodata")
    ResponseEntity<Optional<FormalizedAddress>> formalizeGeoData(@RequestBody GeoDataDto data) {
        log.info("Got data to formalize: {}", data);
        var formalizedData = geoDataUseCase.formalizeAddress(data);
        return ResponseEntity.ok(formalizedData);
    }
}
