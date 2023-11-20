package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.TelegramBotApplication;
import by.dubrovsky.telegrambot.action.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ExitAction implements Action {

    static final String ANSWER = "Приложение закрывается...";
    private final TelegramBotApplication tg;

    public ExitAction(TelegramBotApplication tg) {
        this.tg = tg;
    }

    @Override
    public BotApiMethod<Message> handle(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        tg.stopApplication();

        return new SendMessage(chatId, ANSWER);
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        return handle(update);
    }
}
