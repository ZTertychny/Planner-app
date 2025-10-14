package planner.adapter.out.twogis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import planner.adapter.out.twogis.dto.RoutingRq;
import planner.domain.Route;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoutingMapper {

    public RoutingRq mapToRq(Route route, LocalDate targetDate) {
        // TZ пользователя хранится как "+03:00"
        ZoneId zone = ZoneId.ofOffset("UTC", ZoneOffset.of(route.getTzId()));

        // «Прибыть к» в локальной TZ пользователя в указанный день
        ZonedDateTime arrivalLocal = targetDate
                .atTime(route.getArrivedByLocal())
                .atZone(zone);

        // unix time в секундах для Routing API
        var utc = arrivalLocal.toEpochSecond();

        RoutingRq rq = new RoutingRq()
                .setLocale("ru")
                .setTransport("driving")
                .setRoute_mode("fastest")
                .setTraffic_mode("statistics")
                .setUtc(utc);

        rq.setPoints(List.of(
                new RoutingRq.Point("stop", route.getOriginal().getLon(), route.getOriginal().getLat()),
                new RoutingRq.Point("stop", route.getDestination().getLon(), route.getDestination().getLat())
        ));
        return rq;
    }
}

