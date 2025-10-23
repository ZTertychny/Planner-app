package planner.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import planner.adapter.out.bot.TelegramBotAdapter;
import planner.adapter.persistence.NotificationRepo;
import planner.adapter.persistence.RouteRepo;
import planner.domain.Notification;
import planner.domain.NotificationStatus;
import planner.domain.Route;

import java.time.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationScheduler {
    private final RouteRepo routeRepo;
    private final NotificationRepo notifications;
    private final ComputingNotificationService compute;
    private final TelegramBotAdapter bot;

    private static final long CUTOFF_MIN_BEFORE_ARRIVAL = 5;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void tick() {
        Instant nowUtc = Instant.now();
        var due = notifications.findTop200ByStatusAndFireAtLessThanEqualOrderByFireAtAsc(
                NotificationStatus.PENDING, nowUtc);
        log.info("TICK due={} nowUtc={}", due.size(), nowUtc);

        for (var notification : due) {
            var route = routeRepo.findByUserId(notification.getUser().getId()).orElse(null);
            if (route == null) { notification.setStatus(NotificationStatus.ERROR); continue; }

            ZoneId zone = ZoneId.ofOffset("UTC", ZoneOffset.of(route.getTzId()));
            ZonedDateTime now   = nowUtc.atZone(zone);
            ZonedDateTime arrivalToday = now.toLocalDate().atTime(route.getArrivedByLocal()).atZone(zone);

            // если уже поздно (меньше 5 мин) — просто перекидываем на след. будний (оффлайн)
            if (!now.isBefore(arrivalToday.minusMinutes(CUTOFF_MIN_BEFORE_ARRIVAL))) {
                ZonedDateTime next = compute.computeNextBusinessSendAtOffline(route);
                notification.setFireAt(next.toInstant());
                log.info("Rescheduled late user={} nextFireAt(UTC)={}", route.getUser().getId(), notification.getFireAt());
                continue;
            }

            // пора стрелять
            try {
                bot.notify(route.getUser().getTgChatId(), buildMsg(route));
                notification.setStatus(NotificationStatus.SENT);
                log.info("FIRE user={} chat={}", route.getUser().getId(), route.getUser().getTgChatId());
            } catch (Exception ex) {
                log.warn("Notify failed user={} : {}", route.getUser().getId(), ex.toString());
                // оставляем PENDING — ретраим на следующем тике
                continue;
            }

            // планируем следующую PENDING на ближайший будний (оффлайн)
            ZonedDateTime next = compute.computeNextBusinessSendAtOffline(route);
            notifications.save(new Notification()
                    .setUser(route.getUser())
                    .setFireAt(next.toInstant())
                    .setStatus(NotificationStatus.PENDING));
        }
    }

    private String buildMsg(Route route) {
        return "Через 30 минут пора выезжать к " + route.getDestination().getNormalizedText() + ".";
    }
}
