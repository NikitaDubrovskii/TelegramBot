package by.dubrovsky.telegrambot.repository;

import by.dubrovsky.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
