package by.dubrovsky.telegrambot.service;


import by.dubrovsky.telegrambot.config.BotConfig;
import by.dubrovsky.telegrambot.config.RestTemplateConfig;
import by.dubrovsky.telegrambot.model.WeatherData;
import com.google.gson.Gson;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final RestTemplateConfig restTemplateConfig;

    private final BotConfig botConfig;

    public WeatherService(RestTemplateConfig restTemplateConfig, BotConfig botConfig) {
        this.restTemplateConfig = restTemplateConfig;
        this.botConfig = botConfig;
    }

    public String getWeather(String city) {
        String url = botConfig.getWeatherUrl()
                .replace("{city}", city)
                .replace("{key}", botConfig.getWeatherToken());
        String response = restTemplateConfig.restTemplate(new RestTemplateBuilder())
                .getForEntity(url, String.class).getBody();

        Gson gson = new Gson();
        WeatherData weatherData = gson.fromJson(response, WeatherData.class);

        String temp = String.valueOf(weatherData.getMain().getTemp());
        String feelsLike = String.valueOf(weatherData.getMain().getFeelsLike());
        String discription = weatherData.getWeather().get(0).getDescription();
        String name = weatherData.getName();

        return STR. "Город: \{ name } \nТемпература: \{ temp } \n" +
                STR. "Ощущается: \{ feelsLike } \nПогода: \{ discription }" ;
    }

}
