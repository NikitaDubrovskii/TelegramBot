package by.dubrovsky.telegrambot.action.impl;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.service.RegisterService;
import by.dubrovsky.telegrambot.util.MenuKeyboard;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class StartAction implements Action {

    private final RegisterService registerService;
    private final MenuKeyboard menuKeyboard;

    public StartAction(RegisterService registerService, MenuKeyboard menuKeyboard) {
        this.registerService = registerService;
        this.menuKeyboard = menuKeyboard;
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
        var replyKeyboardMarkup = menuKeyboard.getReplyKeyboardMarkup();
        messageToSend.setReplyMarkup(replyKeyboardMarkup);

        return messageToSend;
    }

    @Override
    public BotApiMethod<Message> callback(Update update) {
        return handle(update);
    }

}
