package de.flightright.repositories;

import de.flightright.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findUserByUsernameAndPassword(String username, String password);
    User findUserByUsername(String username);
}
