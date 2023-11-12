package by.dubrovsky.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData5Days {
    private ArrayList<List> list;

    @Data
    public static class List {
        private Main main;
        private ArrayList<Weather> weather;
        private Wind wind;
        @SerializedName("dt_txt")
        private String dtTxt;

        @Data
        public static class Main {
            private double temp;
        }

        @Data
        public static class Weather {
            private String description;
        }

        @Data
        public static class Wind {
            private double speed;
        }
    }
}
