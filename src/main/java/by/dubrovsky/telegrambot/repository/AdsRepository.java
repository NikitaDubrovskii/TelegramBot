package by.dubrovsky.telegrambot.repository;

import by.dubrovsky.telegrambot.model.Ads;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdsRepository extends JpaRepository<Ads, Long> {
}
