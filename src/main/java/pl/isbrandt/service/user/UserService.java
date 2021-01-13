package pl.isbrandt.service.user;

import pl.isbrandt.model.User;

public interface UserService {
    User findByUserName(String name);

    void save(User user);
}
