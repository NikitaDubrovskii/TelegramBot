package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.service.DefaultCityService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DefaultCityAction implements Action {

    private final DefaultCityService defaultCityService;

    public DefaultCityAction(DefaultCityService defaultCityService) {
        this.defaultCityService = defaultCityService;
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

        return new SendMessage(chatId, answer);
    }
}
