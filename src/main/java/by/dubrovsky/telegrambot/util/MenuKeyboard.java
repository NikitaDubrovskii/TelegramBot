package by.dubrovsky.telegrambot.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class MenuKeyboard {

    public ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        return makeKeyboard();
    }

    private ReplyKeyboardMarkup makeKeyboard() {
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row = new KeyboardRow();
        row.add("Погода");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Помощь");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Настройки");
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }
}
