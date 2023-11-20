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
    private final WeatherNowAction weatherNowAction;
    private final Weather12HoursAction weather12HoursAction;
    private final Weather3DaysAction weather3DaysAction;
    private final HelpAction helpAction;
    private final SettingsAction settingsAction;
    private final DefaultCityAction defaultCityAction;
    private final MenuAction menuAction;
    private final ExitAction exitAction;

    private Map<String, Action> actions;

    private Map<String, String> actionsRuToEng = Map.of(
            "Погода", "/weather",
            "Погода сейчас", "/weatherNow",
            "Погода на 12 часов", "/weather12Hours",
            "Погода на 3 дня", "/weather3Days",
            "Помощь", "/help",
            "Настройки", "/settings",
            "Выход", "/exit"
    );

    public BotView(StartAction startAction, WeatherNowAction weatherNowAction, WeatherAction weatherAction,
                   Weather12HoursAction weather12HoursAction, Weather3DaysAction weather3DaysAction,
                   HelpAction helpAction, SettingsAction settingsAction, DefaultCityAction defaultCityAction,
                   MenuAction menuAction, ExitAction exitAction) {
        this.startAction = startAction;
        this.weatherNowAction = weatherNowAction;
        this.weatherAction = weatherAction;
        this.weather12HoursAction = weather12HoursAction;
        this.weather3DaysAction = weather3DaysAction;
        this.helpAction = helpAction;
        this.settingsAction = settingsAction;
        this.defaultCityAction = defaultCityAction;
        this.menuAction = menuAction;
        this.exitAction = exitAction;
    }

    public Map<String, Action> getActions() {
        return actions = Map.of(
                "/start", startAction,
                "/help", new HelpAction(
                        List.of(
                                "/start - для начала",
                                "/weatherNow - для отображения погоды в городе в данный момент",
                                "/weather12Hours - для отображения погоды в городе на 12 часов",
                                "/weather3Days - для отображения погоды в городе на следующие 3 дня",
                                "/settings - настройки",
                                "/help - помощь"
                        )
                ),
                "/weather", weatherAction,
                "/weatherNow", weatherNowAction,
                "/weather12Hours", weather12HoursAction,
                "/weather3Days", weather3DaysAction,
                "/settings", settingsAction,
                "Установить город по умолчанию", defaultCityAction,
                "Меню", menuAction,
                "/exit", exitAction
        );
    }

}
