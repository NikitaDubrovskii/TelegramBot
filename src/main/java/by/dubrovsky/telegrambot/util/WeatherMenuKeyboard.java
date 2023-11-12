package by.dubrovsky.telegrambot.util;

import by.dubrovsky.telegrambot.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeatherMenuKeyboard {
    public ReplyKeyboardMarkup getReplyKeyboardMarkup(UserRepository userRepository, Long messageChatId) {
        return makeKeyboard(userRepository, messageChatId);
    }

    private ReplyKeyboardMarkup makeKeyboard(UserRepository userRepository, Long messageChatId) {
        var user = userRepository.findById(messageChatId).orElseGet(null);

        String defaultCity = user.getDefaultCity();

        if (defaultCity == null) {
            defaultCity = "Необходимо установить город по умолчанию в настройках";
        }

        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row = new KeyboardRow();
        row.add(defaultCity);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Меню");
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }
}
