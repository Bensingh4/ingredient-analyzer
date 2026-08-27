package com.affordmed.ingredientanalyzer.controller;

import com.affordmed.ingredientanalyzer.ai.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/test")
    public String testGemini(@RequestParam String question) {
        return aiService.askGemini(question);
    }
}