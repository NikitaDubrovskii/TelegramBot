package by.dubrovsky.telegrambot.config;

import by.dubrovsky.telegrambot.action.Action;
import by.dubrovsky.telegrambot.action.impl.HelpAction;
import by.dubrovsky.telegrambot.action.impl.StartAction;
import by.dubrovsky.telegrambot.action.impl.WeatherAction;
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

    private Map<String, Action> actions;

    private Map<String, String> actionsRuToEng = Map.of(
            "Погода", "/weather",
            "Помощь", "/help"
    );

    public BotView(StartAction startAction, WeatherAction weatherAction, HelpAction helpAction) {
        this.startAction = startAction;
        this.weatherAction = weatherAction;
        this.helpAction = helpAction;
    }

    public Map<String, Action> getActions() {
        return actions = Map.of(
                "/start", startAction,
                "/help", new HelpAction(
                        List.of(
                                "/start - для начала",
                                "/weather - для отображения погоды в городе",
                                "/help - помощь"
                        )
                ),
                "/weather", weatherAction
        );
    }

}
