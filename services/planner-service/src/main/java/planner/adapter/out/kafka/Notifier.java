package planner.adapter.out.kafka;

import dto.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Notifier {
    private final KafkaTemplate<String, Notification> kafkaTemplate;

    @Value("${bot.kafka.topic.produce.notify}")
    private String topicProduceNotify;

    public void notify(long chatId, String text) {
        log.info("Notifying {} with text {}", chatId, text);
        kafkaTemplate.send(topicProduceNotify, new Notification(chatId, text));
    }
}
