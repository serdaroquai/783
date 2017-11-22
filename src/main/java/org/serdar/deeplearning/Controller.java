package org.serdar.deeplearning;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
@EnableAutoConfiguration
public class Controller {

    @RequestMapping("/")
    public String index(HttpServletRequest request, Principal principal) {
    	request.setAttribute("principal", principal);
        return "index";
    }  
}