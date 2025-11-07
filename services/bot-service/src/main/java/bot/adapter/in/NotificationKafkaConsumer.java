package bot.adapter.in;

import dto.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaConsumer {
    private final TelegramLongPollingBot bot;

    @KafkaListener(
            topics = "${planner.kafka.topic.consume.notify}",
            groupId = "bot-service"
    )
    public void listen(final Notification notification) {
        log.info("Received notification: {}", notification);
        var sm = SendMessage.builder()
                .chatId(Long.toString(notification.getChatId()))
                .text(notification.getText())
                .build();
        try {
            bot.execute(sm);
        } catch (TelegramApiException e) {
            log.error("Telegram send failed", e);
        }
    }
}
