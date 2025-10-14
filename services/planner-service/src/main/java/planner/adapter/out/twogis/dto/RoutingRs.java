package planner.adapter.out.twogis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class RoutingRs {
    private String type;
    private Query query;
    private List<Route> result;

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Query {
        private String locale;
        private String transport;
        private String route_mode;
        private String traffic_mode;
        private List<RoutingRq.Point> points;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Accessors(chain = true)
    public static class Route {
        private String id;
        private Integer total_duration;     // СЕКУНДЫ
        private Integer total_distance;     // метры
    }
}