package com.badgersoft.satpredict.controller;

/**
 * Created by g4dpz on 19/06/2016.
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String index() {
        return "index";
    }
}
