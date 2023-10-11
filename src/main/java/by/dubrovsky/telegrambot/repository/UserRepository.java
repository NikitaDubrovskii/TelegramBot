package by.dubrovsky.telegrambot.repository;

import by.dubrovsky.telegrambot.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

}
