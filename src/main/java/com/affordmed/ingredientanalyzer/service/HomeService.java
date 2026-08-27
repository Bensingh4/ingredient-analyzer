package com.affordmed.ingredientanalyzer.service;

import org.springframework.stereotype.Service;

@Service
public class HomeService {

    public String getWelcomeMessage() {
        return "Ingredient Analyzer API is running!";
    }
}