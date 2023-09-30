package by.dubrovsky.telegrambot.service;

import by.dubrovsky.telegrambot.config.BotConfig;
import by.dubrovsky.telegrambot.model.User;
import by.dubrovsky.telegrambot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private UserRepository userRepository;

    final BotConfig botConfig;

    static final String HELP_TEXT = """
            Этот бот сделан для демонстрации моих возможностей и для Максима. ЛТ.
            
            /start - для начала\s
            
            /data - для отображения своих данных\s
            
            /deletedata - для удаления своих данных\s
            
            /help - помощь\s
            
            /settings - настройки
            """;

    public TelegramBot(UserRepository userRepository, BotConfig config) {
        super(config.getToken());
        this.userRepository = userRepository;
        this.botConfig = config;
        List<BotCommand> botCommandsList = new ArrayList<>();
        botCommandsList.add(new BotCommand("/start", "начать"));
        botCommandsList.add(new BotCommand("/mydata", "мои данные"));
        botCommandsList.add(new BotCommand("/deletedata", "удалить данные"));
        botCommandsList.add(new BotCommand("/help", "помощь"));
        botCommandsList.add(new BotCommand("/settings", "настройки"));
        try {
            execute(new SetMyCommands(botCommandsList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Ошибка в добавлении команд: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:
                    sendMessage(chatId, "Извините, команда не поддерживается");
            }
        }
    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);

            log.info("Пользователь сохранен: " + user);
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет, " + name;
        log.info("Ответ пользователю " + name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка: " + e.getMessage());
        }
    }
}
