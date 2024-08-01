package local.host.trader.frontend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import local.host.trader.frontend.model.Exchange;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
}
