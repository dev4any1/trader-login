package local.host.trader.frontend.service;

import java.util.List;

import local.host.trader.frontend.model.TraderUser;
import local.host.trader.frontend.model.Session;
import local.host.trader.frontend.model.User;

public interface SessionService {

	List<Session> listAll(User user);

	List<Session> publisherList(TraderUser publisher);

	Session publish(TraderUser publisher, Session session, Long exchangeId);

	void unPublish(TraderUser publisher, Long sessionId);
	
	List<Session> getNewByExchange(Long exchangeId);
}
