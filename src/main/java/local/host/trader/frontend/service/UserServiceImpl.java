package local.host.trader.frontend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import local.host.trader.frontend.model.Exchange;
import local.host.trader.frontend.model.Subscription;
import local.host.trader.frontend.model.User;
import local.host.trader.frontend.repository.ExchangeRepository;
import local.host.trader.frontend.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ExchangeRepository exchangeRepository;

	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Optional<User> getUserByLoginName(String loginName) {
		return Optional.ofNullable(userRepository.findByLoginName(loginName));
	}

	@Override
	public void subscribe(User user, Long exchangeId) {
		List<Subscription> subscriptions;
		subscriptions = user.getSubscriptions();
		if (subscriptions == null) {
			subscriptions = new ArrayList<>();
		}
		Optional<Subscription> subscr = subscriptions.stream()
				.filter(s -> s.getExchange().getId().equals(exchangeId)).findFirst();
		if (!subscr.isPresent()) {
			Subscription s = new Subscription();
			s.setUser(user);
			Exchange exchange = exchangeRepository.findOne(exchangeId);
			if(exchange == null) {
				throw new ServiceException("Exchange not found");
			}
			s.setExchange(exchange);
			subscriptions.add(s);
			userRepository.save(user);
		}
	}

	@Override
	public User findById(Long id) {
		return userRepository.findOne(id);
	}

}
