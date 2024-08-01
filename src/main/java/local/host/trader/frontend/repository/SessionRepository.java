package local.host.trader.frontend.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import local.host.trader.frontend.model.TraderUser;
import local.host.trader.frontend.model.Session;

public interface SessionRepository extends CrudRepository<Session, Long> {

    Collection<Session> findByTraderUser(TraderUser traderUser);

    List<Session> findByExchangeIdIn(List<Long> ids);
    
    List<Session> findByExchangeIdAndPublishDateBetween(Long catId, Date endDate, Date startDate);
}
