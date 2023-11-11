package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.service.DefaultCityService;
import by.dubrovsky.telegrambot.util.MenuKeyboard;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DefaultCityAction implements Action {

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
        String answer = "Введите название города, который вы хотите установить по умолчанию";

        return new SendMessage(chatId, answer);
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        var answer = defaultCityService.setDefaultCity(message);

        var replyKeyboardMarkup = menuKeyboard.getReplyKeyboardMarkup();
        var messageToSend = new SendMessage(chatId, answer);
        messageToSend.setReplyMarkup(replyKeyboardMarkup);

        return messageToSend;
    }
}