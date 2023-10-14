package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class SettingsAction implements Action {

    @Override
    public BotApiMethod<Message> handle(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        var answer = "Выберите, что хотите настроить";

        var messageToSend = new SendMessage(chatId, answer);

        return makeKeyboard(messageToSend);
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();
        var messageToSend = new SendMessage();
        if (message.getText().contains("Назад")) {
            messageToSend.setChatId(chatId);
            messageToSend.setText("Назад");
        }
        return messageToSend;
    }

    private SendMessage makeKeyboard(SendMessage message) {
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row = new KeyboardRow();
        row.add("Установить город по умолчанию");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад");
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);

        message.setReplyMarkup(replyKeyboardMarkup);

        return message;
    }
}
