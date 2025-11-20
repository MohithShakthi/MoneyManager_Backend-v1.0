package com.mohith.moneymanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping({"/health","/status"})
    public String healthCheck(){
        return "Application is running!";
    }
}
