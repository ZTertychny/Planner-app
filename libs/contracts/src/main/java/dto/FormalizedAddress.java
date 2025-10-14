package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FormalizedAddress {
    private String formalized_address;
    private String raw_address;
    private String geo_lat;
    private String geo_lon;
    private String tz;
}
