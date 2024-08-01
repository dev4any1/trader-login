package local.host.trader.frontend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import local.host.trader.frontend.model.TraderUser;
import local.host.trader.frontend.model.User;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<TraderUser, Long> {

    Optional<TraderUser> findByUser(User user);

}
