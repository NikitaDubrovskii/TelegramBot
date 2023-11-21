package by.dubrovsky.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramBotApplication {

/*	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
	}*/

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(TelegramBotApplication.class, args);

		/*ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(TelegramBotApplication::stopApplication, 10, TimeUnit.SECONDS);*/
	}

	@EventListener
	public void onApplicationClosed(ContextClosedEvent event) {
		System.out.println("Приложение закрывается...");
		//stopApplication();
	}

	public void stopApplication() {
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

			if (context != null) {
				SpringApplication.exit(context);
			}
			System.exit(0);
        });
		thread.start();

	}
}
