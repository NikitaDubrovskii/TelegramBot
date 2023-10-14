package by.dubrovsky.telegrambot.config;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.action.impl.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Data
public class BotView {
    private final StartAction startAction;
    private final WeatherAction weatherAction;
    private final HelpAction helpAction;
    private final SettingsAction settingsAction;
    private final DefaultCityAction defaultCityAction;

    private Map<String, Action> actions;

    private Map<String, String> actionsRuToEng = Map.of(
            "Погода", "/weather",
            "Помощь", "/help",
            "Настройки", "/settings"
    );

    public BotView(StartAction startAction, WeatherAction weatherAction, HelpAction helpAction, SettingsAction settingsAction, DefaultCityAction defaultCityAction) {
        this.startAction = startAction;
        this.weatherAction = weatherAction;
        this.helpAction = helpAction;
        this.settingsAction = settingsAction;
        this.defaultCityAction = defaultCityAction;
    }

    public Map<String, Action> getActions() {
        return actions = Map.of(
                "/start", startAction,
                "/help", new HelpAction(
                        List.of(
                                "/start - для начала",
                                "/weather - для отображения погоды в городе",
                                "/settings - настройки",
                                "/help - помощь"
                        )
                ),
                "/weather", weatherAction,
                "/settings", settingsAction,
                "Установить город по умолчанию", defaultCityAction
        );
    }

}
