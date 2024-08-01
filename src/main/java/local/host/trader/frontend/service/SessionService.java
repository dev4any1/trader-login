package local.host.trader.frontend.service;

import java.util.List;

import local.host.trader.frontend.model.Publisher;
import local.host.trader.frontend.model.Session;
import local.host.trader.frontend.model.User;

public interface SessionService {

	List<Session> listAll(User user);

	List<Session> publisherList(Publisher publisher);

	Session publish(Publisher publisher, Session session, Long exchangeId);

	void unPublish(Publisher publisher, Long sessionId);
	
	List<Session> getNewByExchange(Long exchangeId);
}
