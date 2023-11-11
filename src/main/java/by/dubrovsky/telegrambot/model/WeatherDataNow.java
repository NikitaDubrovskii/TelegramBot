package by.dubrovsky.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDataNow {
    private List<Weather> weather;
    private Main main;
    private String name;
    private Wind wind;

    @Data
    public static class Weather {
        private String description;
    }

    @Data
    public static class Main {
        private double temp;
        @SerializedName("feels_like")
        private double feelsLike;
    }

    @Data
    public static class Wind {
        private double speed;
    }
}
