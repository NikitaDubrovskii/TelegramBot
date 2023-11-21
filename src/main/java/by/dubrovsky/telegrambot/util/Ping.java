package by.dubrovsky.telegrambot.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class Ping {
    private final RestTemplate restTemplate;

    public Ping(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 300000)
    public void keepBotAlive() {
        restTemplate.getForObject("https://tgbot-haad.onrender.com", String.class);
    }

}
