package planner.adapter.out.bot;

import dto.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class TelegramBotAdapter {
    private final RestTemplate restTemplate;
    @Value("${bot.service.notification.url}")
    String url;

    public void notify(long chatId, String text) {
        log.info("Notifying bot");
        var uri = UriComponentsBuilder.fromUriString(url)
                .path("/notify")
                .build(true)
                .toUri();

        log.info("Formed uri: {}", uri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var req = new HttpEntity<>(new Notification(chatId, text), headers);
        restTemplate.postForEntity(uri, req, String.class);
    }
}
