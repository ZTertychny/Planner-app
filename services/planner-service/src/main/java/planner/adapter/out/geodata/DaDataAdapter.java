package planner.adapter.out.geodata;

import dto.FormalizedAddress;
import dto.GeoDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import planner.adapter.out.geodata.dto.DaDataCleanDto;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DaDataAdapter {
    private final RestTemplate restTemplate;
    private final DaDataCleanMapper daDataCleanMapper;
    @Value("${dadata.clean.url}")
    private String cleanUrl;
    @Value("${dadata.client.secret}")
    private String clientSecret;
    @Value("${dadata.client.token}")
    private String token;

    public Optional<FormalizedAddress> getCleanAddress(GeoDataDto dataDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Secret", clientSecret);
        headers.set("Authorization", "Token " + token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<List<String>> req = new HttpEntity<>(List.of(dataDto.getAddress()), headers);

        log.info("CT={}", req.getHeaders().getContentType());
        log.info("DaData cleanUrl = '{}'", cleanUrl);
        // 2) Ответ — массив объектов → используем ParameterizedTypeReference
        var resp = restTemplate.exchange(
                cleanUrl,
                HttpMethod.POST,
                req,
                new ParameterizedTypeReference<List<DaDataCleanDto>>() {
                }
        );
        DaDataCleanDto result;
        if (resp.getBody() != null && !resp.getBody().isEmpty()) {
            result = resp.getBody().getFirst();
            log.info("Got response from dadata: {}", result);
            return Optional.of(daDataCleanMapper.map(Pair.of(result, dataDto.getAddress())));

        }
        log.info("Got empty response from dadata");

        return Optional.empty();

    }
}
