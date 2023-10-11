package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.service.WeatherService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class WeatherAction implements Action {

    private final WeatherService weatherService;

    public WeatherAction(WeatherService weatherService) {
        this.weatherService = weatherService;
    }


    @Override
    public BotApiMethod<Message> handle(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        var answer = EmojiParser.parseToUnicode("В каком городе Вас интересует погода" + " :thermometer: " + "?");

        return new SendMessage(chatId, answer);
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId().toString();

        var answer = weatherService.getWeather(message);

        return new SendMessage(chatId, answer);
    }
}
