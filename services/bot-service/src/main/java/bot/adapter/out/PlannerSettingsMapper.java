package bot.adapter.out;

import bot.dto.Session;
import dto.PlannerSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlannerSettingsMapper {

    public PlannerSettings map(Session session, String tgUserId, String tgChatId) {
        log.info("Mapping PlannerSettings");
        var originalAddress = new PlannerSettings.AddressPayload()
                .setNormalized(session.getOriginal_address().getFormalized_address())
                .setLon(session.getOriginal_address().getLon())
                .setLat(session.getOriginal_address().getLat())
                .setRaw(session.getOriginal_address().getRaw_address());
        var destinationAddress = new PlannerSettings.AddressPayload()
                .setNormalized(session.getDestination_address().getFormalized_address())
                .setLon(session.getDestination_address().getLon())
                .setLat(session.getDestination_address().getLat())
                .setRaw(session.getDestination_address().getRaw_address());
        return new PlannerSettings()
                .setArrivalTimeLocal(session.getArrived_at())
                .setTzOffset(session.getTzOffset())
                .setTgChatId(tgChatId)
                .setTgUserId(tgUserId)
                .setOriginal(originalAddress)
                .setDestination(destinationAddress);

    }
}
