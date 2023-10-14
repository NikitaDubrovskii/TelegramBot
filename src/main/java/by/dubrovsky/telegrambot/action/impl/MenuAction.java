package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.util.MenuKeyboard;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MenuAction implements Action {

    private final MenuKeyboard menuKeyboard;

    public MenuAction(MenuKeyboard menuKeyboard) {
        this.menuKeyboard = menuKeyboard;
    }

    @Override
    public BotApiMethod<Message> handle(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        var answer = "Вы перешли в главное меню";
        SendMessage messageToSend = new SendMessage(chatId, answer);

        var replyKeyboardMarkup = menuKeyboard.getReplyKeyboardMarkup();
        messageToSend.setReplyMarkup(replyKeyboardMarkup);

        return messageToSend;
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();
        return new SendMessage(chatId, "");
    }

}
