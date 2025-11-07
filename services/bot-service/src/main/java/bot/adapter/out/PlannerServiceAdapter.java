package bot.adapter.out;

import dto.FormalizedAddress;
import dto.GeoDataDto;
import dto.PlannerSettings;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(
        name = "plannerServiceClient",
        url = "${planner.feign.service.url}"
)
public interface PlannerServiceAdapter {

    @PostMapping("/geodata")
    Optional<FormalizedAddress> formalizeGeoData(@RequestBody GeoDataDto data);

    @PostMapping("/settings")
    @Deprecated
    String saveSettings(@RequestBody PlannerSettings plannerSettings);
}
