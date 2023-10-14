package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.service.RegisterService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class StartAction implements Action {

    private final RegisterService registerService;

    public StartAction(RegisterService registerService) {
        this.registerService = registerService;
    }


    @Override
    public BotApiMethod<Message> handle(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();

        registerService.registerUser(message);

        var name = message.getChat().getFirstName();
        var answer = EmojiParser.parseToUnicode("Привет, " + name + " :blush:");
        log.info("Ответ пользователю " + name);

        var messageToSend = new SendMessage(chatId, answer);

        return makeKeyboard(messageToSend);
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        return handle(update);
    }

    private SendMessage makeKeyboard(SendMessage message) {
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

        message.setReplyMarkup(replyKeyboardMarkup);

        return message;
    }
}
