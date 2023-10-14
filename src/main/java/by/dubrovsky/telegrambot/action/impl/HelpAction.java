package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class HelpAction implements Action {

    static final String DISCRIPTION_TEXT = "Этот бот сделан для демонстрации.";
    private final List<String> actions;

    public HelpAction(List<String> actions) {
        this.actions = actions;
    }

    @Override
    public BotApiMethod<Message> handle(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        var answer = new StringBuilder();
        answer.append(DISCRIPTION_TEXT).append("\n\n");
        for (String action : actions) {
            answer.append(action).append("\n\n");
        }

        return new SendMessage(chatId, answer.toString());
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        return handle(update);
    }
}
