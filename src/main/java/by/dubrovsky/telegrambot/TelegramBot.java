package by.dubrovsky.telegrambot;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.config.BotConfig;
import by.dubrovsky.telegrambot.config.BotView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    public final BotView botView;

    private static final String ERROR_TEXT = "Ошибка: ";

    private final Map<Long, String> bindingBy = new ConcurrentHashMap<>();
    private final Map<String, Action> actions;
    private final Map<String, String> actionsRuToEng;

    public TelegramBot(BotConfig config, BotView botView) {
        super(config.getToken());

        this.botConfig = config;
        this.botView = botView;

        this.actions = botView.getActions();
        this.actionsRuToEng = botView.getActionsRuToEng();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var messageKey = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();

            messageKey = replacementMessage(messageKey);

            if (actions.containsKey(messageKey)) {
                var message = actions.get(messageKey).handle(update);
                bindingBy.put(chatId, messageKey);
                executeMessage(message);
            } else if (bindingBy.containsKey(chatId)) {
                var message = actions.get(bindingBy.get(chatId)).callback(update);
                bindingBy.remove(chatId);
                executeMessage(message);
            }
        }
    }

    private String replacementMessage(String actionRuOrEng) {
        return actionsRuToEng.getOrDefault(actionRuOrEng, actionRuOrEng);
    }

    private void executeMessage(BotApiMethod<Message> message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

}