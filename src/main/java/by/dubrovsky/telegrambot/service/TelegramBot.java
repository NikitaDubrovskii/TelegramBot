package by.dubrovsky.telegrambot.service;

import by.dubrovsky.telegrambot.config.BotConfig;
import by.dubrovsky.telegrambot.model.Ads;
import by.dubrovsky.telegrambot.model.User;
import by.dubrovsky.telegrambot.repository.AdsRepository;
import by.dubrovsky.telegrambot.repository.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final AdsRepository adsRepository;

    final BotConfig botConfig;

    static final String HELP_TEXT = """
            Этот бот сделан для демонстрации моих возможностей и для Максима. ЛТ.
                        
            /start - для начала\s
                        
            /data - для отображения своих данных\s
                        
            /deletedata - для удаления своих данных\s
                        
            /help - помощь\s
                        
            /settings - настройки
            """;

    static final String YES_BTN = "YES_BTN";
    static final String NO_BTN = "NO_BTN";
    public static final String ERROR_TEXT = "Ошибка: ";

    public TelegramBot(UserRepository userRepository, AdsRepository adsRepository, BotConfig config) {
        super(config.getToken());
        this.userRepository = userRepository;
        this.adsRepository = adsRepository;
        this.botConfig = config;
        List<BotCommand> botCommandsList = new ArrayList<>();
        botCommandsList.add(new BotCommand("/start", "начать"));
        botCommandsList.add(new BotCommand("/register", "регистрация"));
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

            if (messageText.contains("/send") && botConfig.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            } else {
                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        prepareAndSendMessage(chatId, HELP_TEXT);
                        break;
                    case "/register":
                        register(chatId);
                        break;
                    default:
                        prepareAndSendMessage(chatId, "Извините, команда не поддерживается");
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(YES_BTN)) {
                String text = "Вы нажали ДА";
                executeEditMessageText(messageId, chatId, text);
            } else if (callbackData.equals(NO_BTN)) {
                String text = "Вы нажали НЕТ";
                executeEditMessageText(messageId, chatId, text);
            }
        }
    }

    private void executeEditMessageText(long messageId, long chatId, String text) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void register(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Вы хотите зарегистрироваться?");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardButtonList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();

        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        yesButton.setCallbackData(YES_BTN);

        var noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        noButton.setCallbackData(NO_BTN);

        keyboardButtons.add(yesButton);
        keyboardButtons.add(noButton);

        keyboardButtonList.add(keyboardButtons);

        keyboardMarkup.setKeyboard(keyboardButtonList);
        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
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
        String answer = EmojiParser.parseToUnicode("Привет, " + name + " :blush:");
        log.info("Ответ пользователю " + name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Погода");
        row.add("Шутка");

        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Регистрация");
        row.add("Проверить свои данные");
        row.add("Удалить свои данные");

        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(replyKeyboardMarkup);

        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void prepareAndSendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message);
    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendAds() {
        var ads = adsRepository.findAll();
        var users= userRepository.findAll();

        for (Ads ad : ads) {
            for (User user : users) {
                prepareAndSendMessage(user.getChatId(), ad.getAd());
            }
        }
    }
}
