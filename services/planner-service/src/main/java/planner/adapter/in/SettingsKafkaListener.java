package planner.adapter.in;

import dto.PlannerSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import planner.app.SaveSettingsUseCase;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsKafkaListener {
    private final SaveSettingsUseCase saveSettingsUseCase;


    @KafkaListener(
            topics = "${bot.kafka.topic.consume.settings}",
            groupId = "planner-service"
    )
    public void handle(PlannerSettings settings) {
        log.info("Received settings {}", settings);
        saveSettingsUseCase.saveSettings(settings);
    }
}
