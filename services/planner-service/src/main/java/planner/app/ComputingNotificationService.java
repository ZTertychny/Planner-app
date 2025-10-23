package planner.app;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import planner.adapter.out.twogis.TwoGisAdapter;
import planner.domain.Route;

import java.time.*;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComputingNotificationService {
    private final TwoGisAdapter twoGis;

    @Value("${planner.notify.lead-minutes:30}")
    private int leadMinutes;

    @Value("${planner.notify.cutoff-before-arrival:5}") // когда уже слишком поздно
    private int cutOffBeforeArrival;

    public Optional<ZonedDateTime> computeInitialSendAt(Route route) {
        ZoneId zone = ZoneId.ofOffset("UTC", ZoneOffset.of(route.getTzId())); // "+03:00" -> ZoneId
        ZonedDateTime now = ZonedDateTime.now(zone);

        // ближайший будний (включая сегодня)
        LocalDate targetDate = nearestBusiness(now.toLocalDate());

        // «прибытие в targetDate» в локальной TZ
        ZonedDateTime arrival = targetDate.atTime(route.getArrivedByLocal()).atZone(zone);

        // если уже поздно для сегодняшнего прибытия — переносим targetDate на следующий будний
        if (!now.isBefore(arrival.minusMinutes(cutOffBeforeArrival))) {
            targetDate = nearestBusiness(targetDate.plusDays(1));
            arrival = targetDate.atTime(route.getArrivedByLocal()).atZone(zone);
        }

        Duration eta = twoGis.getDuration(route, targetDate)
                .orElse(Duration.ofMinutes(30));

        // рассчитываем sendAt
        ZonedDateTime sendAt = arrival.minus(eta).minusMinutes(leadMinutes);
        return Optional.of(sendAt);
    }

    public ZonedDateTime computeNextBusinessSendAtOffline(Route route) {
        ZoneId zone = ZoneId.ofOffset("UTC", ZoneOffset.of(route.getTzId()));
        LocalDate next = nearestBusiness(ZonedDateTime.now(zone).toLocalDate().plusDays(1));
        Duration eta = Duration.ofMinutes(30);
        return arrival(route, next, zone).minus(eta).minusMinutes(leadMinutes);
    }

    private ZonedDateTime arrival(Route route, LocalDate date, ZoneId zoneId) {
        return date.atTime(route.getArrivedByLocal()).atZone(zoneId);
    }

    private LocalDate nearestBusiness(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case SATURDAY -> date.plusDays(2);
            case SUNDAY -> date.plusDays(1);
            default -> date;
        };
    }
}