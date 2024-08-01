package local.host.trader.frontend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import local.host.trader.frontend.model.Publisher;
import local.host.trader.frontend.model.User;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    Optional<Publisher> findByUser(User user);

}
