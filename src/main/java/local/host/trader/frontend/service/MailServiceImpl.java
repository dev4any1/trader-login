package local.host.trader.frontend.service;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import local.host.trader.frontend.model.Exchange;
import local.host.trader.frontend.model.Session;
import local.host.trader.frontend.model.Subscription;
import local.host.trader.frontend.repository.ExchangeRepository;
import local.host.trader.frontend.repository.SubscribtionRepository;


@Service
public class MailServiceImpl implements MailService{
	private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
	
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;
    
    @Value("${spring.mail.username}")
    private String username;
    
    @Value("${spring.mail.password}")
    private String password;
    
    @Autowired
    private JavaMailSender sender;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private ExchangeRepository exchangeRepository;
    
    @Autowired
    private SubscribtionRepository subscribtionRepository;   
    
    @Bean
    public javax.mail.Session getSession() {
        log.info("getSession TLS enabled");
        Properties p = new Properties();
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.starttls.enable", "true");
        p.setProperty("mail.smtp.host", host);
        p.setProperty("mail.smtp.port", port);
        return javax.mail.Session.getInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
	   
	protected void send(String message, String address) {
		SimpleMailMessage smm = new SimpleMailMessage();
		smm.setText(message);
		smm.setTo(address);
       try {
    	   sender.send(smm);
        } catch (MailException ex) {
            // silencing about the SMTP transport exceptions
        	log.debug("Error sending mail message: " + smm.toString(), ex);
        }
	}

	@Override
	public void notifySubscribers(Long exchangeId, Session session) {
		log.info("notifySubscribers exchangeId " + exchangeId + " sessionId " + session.getId());
		Exchange exchange = exchangeRepository.findOne(exchangeId);
		List<Subscription> subs = subscribtionRepository.findUserDistinctByExchange(exchange);
		subs.stream().forEach(s -> {
			doProcessEmail(s, Arrays.asList(session));
		});
	}

	@Override
	@Scheduled(fixedDelay = 86400000, initialDelay = 10000)
	public void scheduleDigestDelivery() {
		log.info("scheduleDigestDelivery");
		List<Exchange> cats = exchangeRepository.findAll();
		cats.stream().forEach(c -> {
			List<Session> digest = sessionService.getNewByExchange(c.getId());
			List<Subscription> subs = subscribtionRepository.findUserDistinctByExchange(c);
			subs.stream().forEach(s -> {
				doProcessEmail(s, digest);
			});
		});
	}
	
	private void doProcessEmail(Subscription sub, List<Session> digest){
		StringBuilder message = new StringBuilder();
		digest.forEach(d -> {
			message.append(String.format("New Session %s was earned by %s at %s. \n", d.getName(), 
					d.getTraderUser().getName(), d.getPublishDate().toString()));
		});
		if (!digest.isEmpty()){
			send(message.toString(), sub.getUser().getLoginName());
		} else {
			log.debug("doProcessEmail: nothing to email, seems no digest for today");
		}
	}
}
