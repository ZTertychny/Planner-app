package planner.adapter.in;

import dto.PlannerSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import planner.app.SaveSettingsUseCase;

@Slf4j
@RequiredArgsConstructor
@RestController
@Deprecated
public class SettingsController {
    private final SaveSettingsUseCase saveSettingsUseCase;

    @PostMapping("/settings")
    public void saveUserSettings(@RequestBody PlannerSettings settings) {
        log.info("Saving settings for {}", settings);
        saveSettingsUseCase.saveSettings(settings);
    }
}
