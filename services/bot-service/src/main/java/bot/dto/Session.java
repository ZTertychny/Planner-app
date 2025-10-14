package bot.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Data
@Accessors(chain = true)
public class Session {
    private State state = State.ASK_DESTINATION;
    private Address destination_address;
    private Address original_address;
    private LocalTime arrived_at;
    private String tzOffset;   // "+03:00"

    @Data
    @Accessors(chain = true)
    public static class Address {
        private String raw_address;
        private String formalized_address;
        private String lat;
        private String lon;
    }
}
