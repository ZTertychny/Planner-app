package dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Data
@Accessors(chain = true)
public class PlannerSettings {
    private String tgUserId;
    private String tgChatId;
    private LocalTime arrivalTimeLocal; // "HH:mm"
    private String tzOffset;         // "+03:00"
    private AddressPayload original;
    private AddressPayload destination;

    @Data
    @Accessors(chain = true)
    public static class AddressPayload {
        private String raw;
        private String normalized;
        private String lat;
        private String lon;
        private AddressType type; // "ORIGINAL" | "DESTINATION"
    }
}
