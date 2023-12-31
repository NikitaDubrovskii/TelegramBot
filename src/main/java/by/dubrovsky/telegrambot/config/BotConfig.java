package by.dubrovsky.telegrambot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Data
@PropertySource("application.properties")
public class BotConfig {

    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String token;

    @Value("${openweather.token}")
    String weatherToken;

    @Value("${openweather.url.now}")
    String weatherUrlNow;

    @Value("${openweather.url.5days}")
    String weatherUrl5Days;

}
