package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.service.DefaultCityService;
import by.dubrovsky.telegrambot.util.MenuKeyboard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class DefaultCityAction implements Action {

    private static final String ANSWER = "Введите название города, который Вы хотите установить по умолчанию";

    private final DefaultCityService defaultCityService;
    private final MenuKeyboard menuKeyboard;

    public DefaultCityAction(DefaultCityService defaultCityService, MenuKeyboard menuKeyboard) {
        this.defaultCityService = defaultCityService;
        this.menuKeyboard = menuKeyboard;
    }

    @Override
    public BotApiMethod<Message> handle(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        return new SendMessage(chatId, ANSWER);
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        var answer = defaultCityService.setDefaultCity(message);

        var replyKeyboardMarkup = menuKeyboard.getReplyKeyboardMarkup();
        var messageToSend = new SendMessage(chatId, answer);
        messageToSend.setReplyMarkup(replyKeyboardMarkup);

        log.info("Выставлен город по умолчанию - " + message.getText());

        return messageToSend;
    }
}
