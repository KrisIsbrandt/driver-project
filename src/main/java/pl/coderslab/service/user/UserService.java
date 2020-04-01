package pl.coderslab.service.user;

import pl.coderslab.model.User;

public interface UserService {
    User findByUserName(String name);

    void save(User user);
}
