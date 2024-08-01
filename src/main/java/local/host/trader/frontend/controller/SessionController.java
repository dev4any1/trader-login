package local.host.trader.frontend.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import local.host.trader.frontend.model.Exchange;
import local.host.trader.frontend.model.Session;
import local.host.trader.frontend.model.Subscription;
import local.host.trader.frontend.model.User;
import local.host.trader.frontend.repository.SessionRepository;
import local.host.trader.frontend.repository.UserRepository;
import local.host.trader.frontend.service.CurrentUser;
import local.host.trader.frontend.service.ServiceException;

@Controller
public class SessionController {

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private UserRepository userRepository;

	@ResponseBody
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET, produces = "application/pdf")
	public ResponseEntity<byte[]> renderDocument(@AuthenticationPrincipal Principal principal, @PathVariable("id") Long id) throws IOException{
		Session session = sessionRepository.findOne(id);
		Exchange exchange = session.getExchange();
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		User user = userRepository.findOne(activeUser.getUser().getId());
		List<Subscription> subscriptions = user.getSubscriptions();
		Optional<Subscription> subscription = subscriptions.stream()
				.filter(s -> s.getExchange().getId().equals(exchange.getId())).findFirst();
		if (subscription.isPresent() || session.getTraderUser().getId().equals(user.getId())) {
			File file = new File(PublisherController.getFileName(session.getTraderUser().getId(), session.getUuid()));
			InputStream in = new FileInputStream(file);
			return ResponseEntity.ok(IOUtils.toByteArray(in));
		} else {
			throw new ServiceException("unsubscribed access, or user isn't a trader");
		}

	}
}
