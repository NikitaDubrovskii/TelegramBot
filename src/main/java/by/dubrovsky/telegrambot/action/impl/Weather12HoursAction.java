package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.repository.UserRepository;
import by.dubrovsky.telegrambot.service.WeatherService;
import by.dubrovsky.telegrambot.util.MenuKeyboard;
import by.dubrovsky.telegrambot.util.WeatherMenuKeyboard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class Weather12HoursAction implements Action {

    public static final String ANSWER = "В каком городе Вас интересует погода?";

    private final WeatherService weatherService;
    private final UserRepository userRepository;
    private final MenuKeyboard menuKeyboard;
    private final WeatherMenuKeyboard weatherMenuKeyboard;

    public Weather12HoursAction(WeatherService weatherService, UserRepository userRepository, MenuKeyboard menuKeyboard, WeatherMenuKeyboard weatherMenuKeyboard) {
        this.weatherService = weatherService;
        this.userRepository = userRepository;
        this.menuKeyboard = menuKeyboard;
        this.weatherMenuKeyboard = weatherMenuKeyboard;
    }

    @Override
    public BotApiMethod<Message> handle(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        var messageToSend = new SendMessage(chatId, ANSWER);

        var replyKeyboardMarkup = weatherMenuKeyboard.getReplyKeyboardMarkup(userRepository, message.getChatId());
        messageToSend.setReplyMarkup(replyKeyboardMarkup);

        return messageToSend;
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId().toString();

        var answer = weatherService.getWeather12Hours(message);
        var replyKeyboardMarkup = menuKeyboard.getReplyKeyboardMarkup();
        var messageToSend = new SendMessage(chatId, answer);
        messageToSend.setReplyMarkup(replyKeyboardMarkup);

        log.info("Отображена погода на 12 часов в городе - " + message);

        return messageToSend;
    }

}
