package bot.adapter.out;

import dto.PlannerSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSettingsKafkaAdapter {
    private final KafkaTemplate<String, PlannerSettings> kafkaTemplate;

    @Value("${planner.kafka.topic.produce.settings}")
    private String topic;

    public void sendSettings(PlannerSettings message) {
        log.info("Sending settings to {}", topic);
        kafkaTemplate.send(topic, message);
    }
}
