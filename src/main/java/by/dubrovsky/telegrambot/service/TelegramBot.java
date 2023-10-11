package by.dubrovsky.telegrambot.service;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.config.BotConfig;
import by.dubrovsky.telegrambot.config.BotView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig botConfig;
    public final BotView botView;

    public static final String ERROR_TEXT = "Ошибка: ";

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

    // редактирование ответа от бота
    private void executeEditMessageText(long messageId, long chatId, String text) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

/*    private void register(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Вы хотите зарегистрироваться?");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardButtonList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();

        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        yesButton.setCallbackData(YES_BTN);

        var noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        noButton.setCallbackData(NO_BTN);

        keyboardButtons.add(yesButton);
        keyboardButtons.add(noButton);

        keyboardButtonList.add(keyboardButtons);

        keyboardMarkup.setKeyboard(keyboardButtonList);
        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }*/

    private void executeMessage(BotApiMethod<Message> message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

/*    @Scheduled(cron = "${cron.scheduler}")
    private void sendAds() {
        var ads = adsRepository.findAll();
        var users= userRepository.findAll();

        for (Ads ad : ads) {
            for (User user : users) {
                prepareAndSendMessage(user.getChatId(), ad.getAd());
            }
        }
    }*/

}
