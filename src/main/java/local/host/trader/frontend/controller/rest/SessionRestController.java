package local.host.trader.frontend.controller.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import local.host.trader.frontend.dto.SubscriptionDTO;
import local.host.trader.frontend.model.Exchange;
import local.host.trader.frontend.model.TraderUser;
import local.host.trader.frontend.model.Session;
import local.host.trader.frontend.model.Subscription;
import local.host.trader.frontend.model.User;
import local.host.trader.frontend.repository.ExchangeRepository;
import local.host.trader.frontend.repository.PublisherRepository;
import local.host.trader.frontend.service.CurrentUser;
import local.host.trader.frontend.service.SessionService;
import local.host.trader.frontend.service.UserService;

@RestController
@RequestMapping("/rest/sessions")
public class SessionRestController {

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private UserService userService;

	@Autowired
	private ExchangeRepository exchangeRepository;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Object> browse(@AuthenticationPrincipal Principal principal) {
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		return ResponseEntity.ok(sessionService.listAll(activeUser.getUser()));
	}

	@RequestMapping(value = "/active", method = RequestMethod.GET)
	public List<Session> publishedList(@AuthenticationPrincipal Principal principal) {
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		Optional<TraderUser> publisher = publisherRepository.findByUser(activeUser.getUser());
		return sessionService.publisherList(publisher.get());
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public void unPublish(@PathVariable("id") Long id, @AuthenticationPrincipal Principal principal) {
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		Optional<TraderUser> publisher = publisherRepository.findByUser(activeUser.getUser());
		sessionService.unPublish(publisher.get(), id);
	}

	@RequestMapping(value = "/assignments", method = RequestMethod.GET)
	public List<SubscriptionDTO> getUserSubscriptions(@AuthenticationPrincipal Principal principal) {
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		User persistedUser = userService.findById(activeUser.getId());
		List<Subscription> subscriptions = persistedUser.getSubscriptions();
		List<Exchange> exchanges = exchangeRepository.findAll();
		List<SubscriptionDTO> subscriptionDTOs = new ArrayList<>(exchanges.size());
		exchanges.stream().forEach(c -> {
			SubscriptionDTO subscr = new SubscriptionDTO(c);
			Optional<Subscription> subscription = subscriptions.stream().filter(s -> s.getExchange().getId().equals(c.getId())).findFirst();
			subscr.setActive(subscription.isPresent());
			subscriptionDTOs.add(subscr);
		});
		return subscriptionDTOs;
	}

	@RequestMapping(value = "/assign/{exchangeId}", method = RequestMethod.POST)
	public void subscribe(@PathVariable("exchangeId") Long exchangeId, @AuthenticationPrincipal Principal principal) {
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		User user = userService.findById(activeUser.getUser().getId());
		userService.subscribe(user, exchangeId);
	}
}
