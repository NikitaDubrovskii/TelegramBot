package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.repository.UserRepository;
import by.dubrovsky.telegrambot.service.WeatherService;
import com.vdurmont.emoji.EmojiParser;
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

    private final WeatherService weatherService;
    private final UserRepository userRepository;

    public WeatherAction(WeatherService weatherService, UserRepository userRepository) {
        this.weatherService = weatherService;
        this.userRepository = userRepository;
    }

    @Override
    public BotApiMethod<Message> handle(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        var answer = EmojiParser.parseToUnicode("В каком городе Вас интересует погода" + " :thermometer: " + "?");

        return makeKeyboard(new SendMessage(chatId, answer));
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId().toString();

        var answer = weatherService.getWeather(message);

        return new SendMessage(chatId, answer);
    }

    private SendMessage makeKeyboard(SendMessage message) {
        var user = userRepository.findById(Long.valueOf(message.getChatId())).orElseGet(null);

        String defaultCity = user.getDefaultCity();

        if (defaultCity == null) {
            defaultCity = "Необходимо установить город по умолчанию в настройках";
        }

        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row = new KeyboardRow();
        row.add(defaultCity);
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);

        message.setReplyMarkup(replyKeyboardMarkup);

        return message;
    }
}