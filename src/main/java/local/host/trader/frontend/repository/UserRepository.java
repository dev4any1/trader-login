package local.host.trader.frontend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import local.host.trader.frontend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLoginName(String loginName);
    User findByEmail(String email);
}
