package by.dubrovsky.telegrambot.service;

import by.dubrovsky.telegrambot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class DefaultCityService {

    private final UserRepository userRepository;

    public DefaultCityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String setDefaultCity(Message message) {
        var user = userRepository.findById(message.getChatId());
        if (user.isPresent()) {
            var userWithCity = user.get();
            userWithCity.setDefaultCity(message.getText());
            userRepository.save(userWithCity);
            return "Город '" + message.getText() + "' установлен в качестве города по умолчанию";
        }
        return "Ошибка";
    }
}
