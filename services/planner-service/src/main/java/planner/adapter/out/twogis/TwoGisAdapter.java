package planner.adapter.out.twogis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import planner.adapter.out.twogis.dto.RoutingRq;
import planner.adapter.out.twogis.dto.RoutingRs;
import planner.domain.Route;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TwoGisAdapter {
    private final RestTemplate restTemplate;
    private final RoutingMapper routingMapper;

    @Value("${twogis.api-key}")
    String apiKey;
    @Value("${twogis.url}")
    String url;

    public Optional<Duration> getDuration(Route route, LocalDate targetDate) {
        var rq = routingMapper.mapToRq(route, targetDate);
        log.info("Got mapped rq = {} from route = {} and targetDate = {}", rq, route, targetDate);
        var uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("key", apiKey)
                .build(true)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<RoutingRq> req = new HttpEntity<>(rq, headers);

        log.info("2GIS POST to {} with rq {}", uri, req);
        ResponseEntity<RoutingRs> resp = restTemplate.exchange(uri, HttpMethod.POST, req, RoutingRs.class);

        if (resp.getStatusCode().is2xxSuccessful()
                && resp.getBody() != null
                && resp.getBody().getResult() != null) {
            var seconds = resp.getBody().getResult().getFirst().getTotal_duration();
            log.info("2GIS GET RESULT {}", seconds);
            return Optional.of(Duration.ofSeconds(Math.max(60, seconds)));
        }
        log.info("2GIS GET FAILED");
        return Optional.empty();
    }


}
