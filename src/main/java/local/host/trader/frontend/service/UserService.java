package local.host.trader.frontend.service;

import java.util.Optional;

import local.host.trader.frontend.model.User;

public interface UserService {

    Optional<User> getUserByLoginName(String loginName);

    void subscribe(User user, Long exchangeId);

    User findById(Long id);

}