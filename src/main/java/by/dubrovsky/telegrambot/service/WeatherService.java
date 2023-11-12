package by.dubrovsky.telegrambot.service;


import by.dubrovsky.telegrambot.config.BotConfig;
import by.dubrovsky.telegrambot.config.RestTemplateConfig;
import by.dubrovsky.telegrambot.model.WeatherData5Days;
import by.dubrovsky.telegrambot.model.WeatherDataNow;
import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
    private final SimpleDateFormat simpleDateFormatHours = new SimpleDateFormat("HH:mm");
    private final DateTimeFormatter formatterForDays = DateTimeFormatter.ofPattern("d MMMM", new Locale.Builder().setLanguage("ru").build());


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

        return STR. "Погода в городе \{ city } \n\n" + EmojiParser.parseToUnicode(STR.":thermometer: " + STR. "\{ temp } °C" ) +
                EmojiParser.parseToUnicode(STR. ", ощущается на \{ feelsLike } °C" ) +
                STR. ", \{ discription }, " +
                EmojiParser.parseToUnicode(STR.":dash: " + STR. "\{speed} м/с\n") ;
    }

    public String getWeather12Hours(String city) {
        var url = botConfig.getWeatherUrl5Days()
                .replace("{city}", city)
                .replace("{key}", botConfig.getWeatherToken());
        var respone = restTemplateConfig.restTemplate(new RestTemplateBuilder())
                .getForEntity(url, String.class).getBody();

        var gson = new Gson();
        var weatherData5Days = gson.fromJson(respone, WeatherData5Days.class);

        var result = new StringBuilder(STR. "Погода на 12 часов в городе \{city} \n\n");
        int i = 0;

        SimpleDateFormat simpleDateFormatHoursInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (WeatherData5Days.List list : weatherData5Days.getList()) {
            if (i < 5) {
                var time = list.getDtTxt();
                Date date;
                String outputTime;
                try {
                    date = simpleDateFormatHoursInput.parse(time);
                    outputTime = simpleDateFormatHours.format(date);
                    result.append(STR. "\{outputTime}: ");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                var temp = String.valueOf(Math.round(list.getMain().getTemp()));
                var discription = list.getWeather().get(0).getDescription();
                var speed = String.valueOf(list.getWind().getSpeed());
                result.append(EmojiParser.parseToUnicode(STR.":thermometer: " + STR. "\{ temp } °C, "));
                result.append(STR. "\{discription}, ");
                result.append(EmojiParser.parseToUnicode(STR.":dash: " + STR. "\{speed} м/с\n"));

                i++;
            } else {
                break;
            }
        }

        return result.toString();
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

        var result = new StringBuilder(STR. "Погода на 3 дня в городе \{city} \n\n");
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
                        var finalDate = localDate.format(formatterForDays);

                        result.append(STR. "\{finalDate} \n");
                        result.append(EmojiParser.parseToUnicode(STR. ":crescent_moon: " + STR. "\{temp} °C"));
                        result.append(STR. ", \{ discription }, " );
                        result.append(EmojiParser.parseToUnicode(STR.":dash: " + STR. "\{speed} м/с\n"));
                    } else {
                        result.append(EmojiParser.parseToUnicode(STR. ":sunny: " + STR. "\{temp} °C"));
                        result.append(STR. ", \{ discription }, " );
                        result.append(EmojiParser.parseToUnicode(STR.":dash: " + STR. "\{speed} м/с\n"));

                        days++;
                    }
                    i++;
                }
            }
        }

        return result.toString();
    }

}
