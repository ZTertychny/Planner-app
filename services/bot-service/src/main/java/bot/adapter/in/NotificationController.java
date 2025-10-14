package bot.adapter.in;

import dto.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NotificationController {
    private final TelegramLongPollingBot bot;

    @PostMapping("/notify")
    public ResponseEntity<String> notifyUser(@RequestBody Notification notification) {
        var sm = SendMessage.builder()
                .chatId(Long.toString(notification.getChatId()))
                .text(notification.getText())
                .build();

        try {
            bot.execute(sm);
            return ResponseEntity.noContent().build();
        } catch (TelegramApiException e) {
            log.error("Telegram send failed", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
}
