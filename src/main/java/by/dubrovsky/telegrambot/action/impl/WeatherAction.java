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
public class WeatherAction implements Action {

    public static final String ANSWER = "Выберите вариант отображения погоды";

    @Override
    public BotApiMethod<Message> handle(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        var messageToSend = new SendMessage(chatId, ANSWER);

        return makeKeyboard(messageToSend);
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        return null;
    }

    private SendMessage makeKeyboard(SendMessage message) {
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row = new KeyboardRow();
        row.add("Погода сейчас");
        row.add("Погода на 12 часов");
        row.add("Погода на 3 дня");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Меню");
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);

        message.setReplyMarkup(replyKeyboardMarkup);

        return message;
    }
}
