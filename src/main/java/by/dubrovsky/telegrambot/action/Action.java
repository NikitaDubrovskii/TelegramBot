package by.dubrovsky.telegrambot.action;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public interface Action {
    BotApiMethod<Message> handle(Update update);

    BotApiMethod<Message> callback(Update update);

}
