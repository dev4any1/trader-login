package local.host.trader.frontend.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import local.host.trader.frontend.controller.PublisherController;
import local.host.trader.frontend.model.Exchange;
import local.host.trader.frontend.model.Publisher;
import local.host.trader.frontend.model.Session;
import local.host.trader.frontend.model.Subscription;
import local.host.trader.frontend.model.User;
import local.host.trader.frontend.repository.ExchangeRepository;
import local.host.trader.frontend.repository.SessionRepository;
import local.host.trader.frontend.repository.UserRepository;

@Service
public class SessionServiceImpl implements SessionService {

	private final static Logger log = Logger.getLogger(SessionServiceImpl.class);

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ExchangeRepository exchangeRepository;

	@Autowired
	private MailService mailService;

	@Transactional(readOnly = true)
	@Override
	public List<Session> listAll(User user) {
		User persistentUser = userRepository.findOne(user.getId());
		List<Subscription> subscriptions = persistentUser.getSubscriptions();
		if (subscriptions != null) {
			List<Long> ids = new ArrayList<>(subscriptions.size());
			subscriptions.stream().forEach(s -> ids.add(s.getExchange().getId()));
			return sessionRepository.findByExchangeIdIn(ids);
		} else {
			return Collections.emptyList();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Session> publisherList(Publisher publisher) {
		Iterable<Session> sessions = sessionRepository.findByPublisher(publisher);
		return StreamSupport.stream(sessions.spliterator(), false).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Session publish(Publisher publisher, Session session, Long exchangeId) throws ServiceException {
		Exchange exchange = exchangeRepository.findOne(exchangeId);
		if (exchange == null) {
			throw new ServiceException("Exchange not found");
		}
		session.setPublisher(publisher);
		session.setExchange(exchange);
		try {
			Session result = sessionRepository.save(session);
			mailService.notifySubscribers(exchangeId, session);
			return result;
		} catch (DataIntegrityViolationException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public void unPublish(Publisher publisher, Long id) throws ServiceException {
		Session session = sessionRepository.findOne(id);
		if (session == null) {
			throw new ServiceException("Session doesn't exist");
		}
		String filePath = PublisherController.getFileName(publisher.getId(), session.getUuid());
		File file = new File(filePath);
		if (file.exists()) {
			boolean deleted = file.delete();
			if (!deleted) {
				log.error("File " + filePath + " cannot be deleted");
			}
		}
		if (!session.getPublisher().getId().equals(publisher.getId())) {
			throw new ServiceException("Session cannot be removed");
		}
		sessionRepository.delete(session);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Session> getNewByExchange(Long exchangeId) {
		LocalDateTime now = LocalDateTime.now();
		Date newPeriodEnd = toDate(now);
		Date newPeriodStart = toDate(now.minusDays(1L));
		List<Session> result = sessionRepository.findByExchangeIdAndPublishDateBetween(exchangeId, newPeriodStart,
				newPeriodEnd);
		return result;
	}

	private Date toDate(LocalDateTime localDate) {
		return Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
	}
}
