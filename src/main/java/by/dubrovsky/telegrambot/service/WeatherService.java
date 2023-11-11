package by.dubrovsky.telegrambot.service;


import by.dubrovsky.telegrambot.config.BotConfig;
import by.dubrovsky.telegrambot.config.RestTemplateConfig;
import by.dubrovsky.telegrambot.model.WeatherData5Days;
import by.dubrovsky.telegrambot.model.WeatherDataNow;
import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Service
public class WeatherService {

    private final RestTemplateConfig restTemplateConfig;

    private final BotConfig botConfig;

    private final SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat simpleDateFormatFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM", new Locale("ru"));


    public WeatherService(RestTemplateConfig restTemplateConfig, BotConfig botConfig) {
        this.restTemplateConfig = restTemplateConfig;
        this.botConfig = botConfig;
    }

    public String getWeatherNow(String city) {
        var url = botConfig.getWeatherUrlNow()
                .replace("{city}", city)
                .replace("{key}", botConfig.getWeatherToken());
        var response = restTemplateConfig.restTemplate(new RestTemplateBuilder())
                .getForEntity(url, String.class).getBody();

        var gson = new Gson();
        var weatherDataNow = gson.fromJson(response, WeatherDataNow.class);

        var temp = String.valueOf(Math.round(weatherDataNow.getMain().getTemp()));
        var feelsLike = String.valueOf(Math.round(weatherDataNow.getMain().getFeelsLike()));
        var discription = weatherDataNow.getWeather().get(0).getDescription();
        var speed = String.valueOf(weatherDataNow.getWind().getSpeed());

        return STR. "Прогноз погоды в городе \{ city } \n\n" + EmojiParser.parseToUnicode(STR.":thermometer: " + STR. "\{ temp } °C" ) +
                EmojiParser.parseToUnicode(STR. ", ощущается на \{ feelsLike } °C" ) +
                STR. ", \{ discription }" +
                STR. ", ветер: \{ speed } м/с" ;
    }

    public String getWeather3Days(String city) {
        var url = botConfig.getWeatherUrl5Days()
                .replace("{city}", city)
                .replace("{key}", botConfig.getWeatherToken());
        var respone = restTemplateConfig.restTemplate(new RestTemplateBuilder())
                .getForEntity(url, String.class).getBody();

        var gson = new Gson();
        var weatherData5Days = gson.fromJson(respone, WeatherData5Days.class);

        var day = simpleDateFormatDay.format(new Date());
        var now = LocalDate.now();

        var result = new StringBuilder(STR. "Прогноз погоды на 3 дня в городе \{city} \n\n");
        int i = 0;
        int days = 1;

        for (WeatherData5Days.List list : weatherData5Days.getList()) {

            if (!list.getDtTxt().contains(day)) {
                if ((list.getDtTxt().contains("03:00:00") || list.getDtTxt().contains("15:00:00")) && i < 6) {
                    var temp = String.valueOf(Math.round(list.getMain().getTemp()));
                    var discription = list.getWeather().get(0).getDescription();
                    var speed = String.valueOf(list.getWind().getSpeed());


                    if ((list.getDtTxt().contains("03:00:00"))) {
                        var localDate = now.plusDays(days);
                        var finalDate = localDate.format(formatter);

                        result.append(STR. "\{finalDate} \n");
                        result.append(EmojiParser.parseToUnicode(STR. ":crescent_moon: " + STR. "\{temp} °C"));
                        result.append(STR. ", \{ discription }" );
                        result.append(STR. ", \{ speed } м/с \n" );
                    } else {

                        result.append(EmojiParser.parseToUnicode(STR. ":sunny: " + STR. "\{temp} °C"));
                        result.append(STR. ", \{ discription }" );
                        result.append(STR. ", \{ speed } м/с \n\n" );

                        days++;
                    }
                    i++;
                }
            }
        }


        return result.toString();
    }

}
