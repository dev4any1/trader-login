package local.host.trader.frontend.controller.rest;

import local.host.trader.frontend.model.Exchange;
import local.host.trader.frontend.repository.ExchangeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/rest/exchange")
public class ExchangeRestController {

    @Autowired
    private ExchangeRepository repository;


    @RequestMapping(value = "")
    public List<Exchange> getCategories() {
        return repository.findAll();
    }

}
