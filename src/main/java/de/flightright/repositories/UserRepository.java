package de.flightright.repositories;

import de.flightright.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findUserByUsernameAndPasswordHash(String username, String passwordHash);
    User findUserByUsername(String username);
}
