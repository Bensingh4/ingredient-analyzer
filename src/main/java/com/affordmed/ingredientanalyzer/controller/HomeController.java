package com.affordmed.ingredientanalyzer.controller;

import com.affordmed.ingredientanalyzer.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/")
    public String home() {
        return homeService.getWelcomeMessage();
    }
}