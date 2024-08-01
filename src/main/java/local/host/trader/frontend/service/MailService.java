package local.host.trader.frontend.service;

import local.host.trader.frontend.model.Session;

public interface MailService {
	
	void notifySubscribers(Long exchangeId, Session session);
	
	void scheduleDigestDelivery();
}
