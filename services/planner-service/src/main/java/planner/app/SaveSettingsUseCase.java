package planner.app;

import dto.AddressType;
import dto.PlannerSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planner.adapter.persistence.AddressRepo;
import planner.adapter.persistence.AppUserRepo;
import planner.adapter.persistence.NotificationRepo;
import planner.adapter.persistence.RouteRepo;
import planner.domain.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SaveSettingsUseCase {
    private final AddressRepo addressRepo;
    private final AppUserRepo appUserRepo;
    private final RouteRepo routeRepo;
    private final NotificationRepo notificationRepo;
    private final ComputingNotificationService computingNotificationService;

    @Transactional
    public void saveSettings(PlannerSettings settings) {
        log.info("Start save settings");
        var user = appUserRepo.findByTgUserId(Long.valueOf(settings.getTgUserId()))
                .orElseGet(() -> appUserRepo.save(new AppUser()
                        .setTgUserId(Long.valueOf(settings.getTgUserId()))
                        .setTgChatId(Long.valueOf(settings.getTgChatId()))
                        .setCreatedAt(Instant.now())));

        var orig = upsertAddress(user, settings.getOriginal(), AddressType.ORIGINAL, settings.getTzOffset());
        var dest = upsertAddress(user, settings.getDestination(), AddressType.DESTINATION, settings.getTzOffset());
        log.info("Form addresses");

        var route = routeRepo.findByUserId(user.getId()).orElseGet(Route::new);
        route.setOriginal(orig)
                .setUser(user)
                .setOriginal(orig)
                .setDestination(dest)
                .setMode("driving")
                .setArrivedByLocal(settings.getArrivalTimeLocal())
                .setTzId(settings.getTzOffset());

        log.info("Saving");
        routeRepo.save(route);
        createNotification(route);
    }

    private Address upsertAddress(AppUser user, PlannerSettings.AddressPayload p,
                                  AddressType type, String tzOffset) {
        var address = addressRepo.findByUserIdAndType(user.getId(), type).orElse(new Address());
        address.setUser(user)
                .setType(type)
                .setRawText(p.getRaw())
                .setNormalizedText(p.getNormalized())
                .setLat(Double.valueOf(p.getLat()))
                .setLon(Double.valueOf(p.getLon()))
                .setTzId(tzOffset);
        return addressRepo.save(address);
    }

    private void createNotification(Route route) {
        Optional<ZonedDateTime> sendAtOpt = computingNotificationService.computeInitialSendAt(route);
        if (sendAtOpt.isEmpty()) {
            log.warn("Initial sendAt is empty for user={}", route.getUser().getId());
            return;
        }
        Instant fireAt = sendAtOpt.get().toInstant();

        upsertPending(route.getUser(), fireAt);

        log.info("Planned initial PENDING user={} fireAt(UTC)={}", route.getUser().getId(), fireAt);
    }

    private void upsertPending(AppUser user, Instant fireAt) {
        notificationRepo.findByUserIdAndStatus(user.getId(), NotificationStatus.PENDING)
                .ifPresentOrElse(
                        n -> n.setFireAt(fireAt), // просто сдвигаем время
                        () -> notificationRepo.save(new Notification()
                                .setUser(user)
                                .setFireAt(fireAt)
                                .setStatus(NotificationStatus.PENDING))
                );
    }
}
