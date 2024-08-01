package local.host.trader.frontend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import local.host.trader.frontend.model.Exchange;
import local.host.trader.frontend.model.Subscription;

public interface SubscribtionRepository extends JpaRepository<Subscription, Long> {
	List<Subscription> findUserDistinctByExchange(Exchange exchange);
}
