package planner.adapter.out.twogis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RoutingRq {
    private List<Point> points;
    private String locale;
    private String transport;
    private String route_mode;
    private String traffic_mode;
    private long utc;

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Point {
        private String type;
        private double lon;
        private double lat;
    }
}
