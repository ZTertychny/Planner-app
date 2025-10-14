package bot.adapter.in;

import bot.adapter.out.PlannerServiceAdapter;
import bot.adapter.out.PlannerSettingsMapper;
import bot.dto.Session;
import bot.dto.State;
import dto.FormalizedAddress;
import dto.GeoDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static bot.dto.State.ASK_DESTINATION;
import static bot.util.TelegramBotPhrases.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotHandler extends TelegramLongPollingBot {
    private final PlannerServiceAdapter plannerServiceAdapter;
    private final PlannerSettingsMapper plannerSettingsMapper;

    @Value("${telegram.botToken}")
    private String botToken;
    @Value("${telegram.botUserName}")
    private String botUserName;

    private final Map<Long, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        long userId = msg.getFrom().getId();
        long chatId = msg.getChatId();
        String text = msg.getText().trim();

        Session session = sessions.computeIfAbsent(userId, id -> new Session());
        log.info("Start session" + session + " for user " + userId + " and chat " + chatId);

        if (text.equalsIgnoreCase("/start")) {
            log.info("Session " + userId + " has started");
            session.setState(ASK_DESTINATION);
            sessions.put(userId, session);
            sendText(chatId, START);
            return;
        }

        try {
            switch (session.getState()) {
                case ASK_DESTINATION -> {
                    log.info("Session " + session + " on AskDestination");
                    Optional<FormalizedAddress> normalized = plannerServiceAdapter.formalizeGeoData(new GeoDataDto(text));
                    if (normalized.isPresent()) {
                        fillSession(normalized.get(), session, State.ASK_ORIGINAL);
                        sendText(chatId, ASK_ORIGINAL);
                        return;
                    } else {
                        sendText(chatId, "Что-то пошлое не так");
                    }
                }
                case ASK_ORIGINAL -> {
                    log.info("Session " + session + " on AskOriginal");
                    Optional<FormalizedAddress> normalized = plannerServiceAdapter.formalizeGeoData(new GeoDataDto(text));
                    if (normalized.isPresent()) {
                        fillSession(normalized.get(), session, State.ASK_ARRIVED_AT);
                        sendText(chatId, ASK_TIME);
                        return;
                    } else {
                        sendText(chatId, "Что-то пошлое не так");
                    }
                }
                case ASK_ARRIVED_AT -> {
                    log.info("Session " + session + " on AskArrivedAt");
                    LocalTime time;
                    try {
                        time = LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm"));
                        session.setArrived_at(time);
                        session.setState(State.DONE);
                        sendText(chatId, DONE.formatted(session.getDestination_address().getFormalized_address(),
                                session.getOriginal_address().getFormalized_address(),
                                time));
                    } catch (DateTimeParseException e) {
                        sendText(chatId, "Не понял введенное время. Попробуй заново. Формат 09:30");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            log.info("WE HAVE TROUBLE!" + e.getMessage());
        }

        log.info("End session" + session + " for user " + userId + " and chat " + chatId);
        sendText(chatId, EXIT);
        var settings = plannerSettingsMapper.map(session, String.valueOf(userId), String.valueOf(chatId));
        sessions.remove(userId);
        plannerServiceAdapter.saveSettings(settings);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    private void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    private void fillSession(FormalizedAddress formalized_address, Session session, State state) {
        if (state.name().equals(State.ASK_ORIGINAL.name())) {
            log.info("Session " + session + " on AskOriginal");
            session.setDestination_address(new Session.Address()
                    .setRaw_address(formalized_address.getRaw_address())
                    .setFormalized_address(formalized_address.getFormalized_address())
                    .setLon(formalized_address.getGeo_lon())
                    .setLat(formalized_address.getGeo_lat()));
        } else {
            log.info("Session " + session + " on" + state);
            session.setOriginal_address(new Session.Address()
                    .setRaw_address(formalized_address.getRaw_address())
                    .setFormalized_address(formalized_address.getFormalized_address())
                    .setLon(formalized_address.getGeo_lon())
                    .setLat(formalized_address.getGeo_lat()));
        }
        session.setTzOffset(formalized_address.getTz());
        session.setState(state);

    }

}
