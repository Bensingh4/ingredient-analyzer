package com.affordmed.ingredientanalyzer.service;

import com.affordmed.ingredientanalyzer.ai.AiService;
import com.affordmed.ingredientanalyzer.dto.IngredientKnowledgeResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IngredientKnowledgeService {

    private final AiService aiService;

    public IngredientKnowledgeService(AiService aiService) {
        this.aiService = aiService;
    }

    // =========================================================
    // SINGLE INGREDIENT
    // =========================================================

    public IngredientKnowledgeResult analyzeIngredientWithAI(
            String ingredient,
            String category) {

        try {

            IngredientKnowledgeResult result =
                    aiService.analyzeIngredientKnowledge(
                            ingredient,
                            category
                    );

            if (result == null) {
                return createFailedResult(ingredient);
            }

            return result;

        } catch (Exception e) {

            System.out.println(
                    "AI analysis failed for ingredient: "
                            + ingredient
            );

            System.out.println(
                    "Reason: "
                            + e.getMessage()
            );

            return createFailedResult(ingredient);
        }
    }

    // =========================================================
    // MULTIPLE INGREDIENTS
    //
    // ONE Groq request for ALL ingredients
    // =========================================================

    public List<IngredientKnowledgeResult>
    analyzeIngredientsWithAI(
            List<String> ingredients,
            String category) {

        List<IngredientKnowledgeResult> results =
                new ArrayList<>();

        if (ingredients == null ||
                ingredients.isEmpty()) {

            return results;
        }

        /*
         * IMPORTANT:
         *
         * Do NOT loop through the ingredients
         * and call Groq separately.
         *
         * Send the complete list in ONE request.
         */

        try {

            results =
                    aiService.analyzeIngredientsKnowledge(
                            ingredients,
                            category
                    );

            if (results == null) {

                results = new ArrayList<>();
            }

            /*
             * Make sure every result has the correct
             * source and status.
             */

            for (IngredientKnowledgeResult result :
                    results) {

                if (result == null) {
                    continue;
                }

                result.setSource("GROQ");

                if (result.getStatus() == null ||
                        result.getStatus().isBlank()) {

                    result.setStatus("AI_ANALYZED");
                }
            }

            return results;

        } catch (Exception e) {

            System.out.println(
                    "Groq batch ingredient analysis failed."
            );

            System.out.println(
                    "Reason: "
                            + e.getMessage()
            );

            /*
             * Do NOT throw the exception.
             *
             * Return failed results so the complete
             * product scan can still finish.
             */

            for (String ingredient :
                    ingredients) {

                if (ingredient == null ||
                        ingredient.isBlank()) {

                    continue;
                }

                results.add(
                        createFailedResult(
                                ingredient
                        )
                );
            }

            return results;
        }
    }

    // =========================================================
    // FAILED RESULT
    // =========================================================

    private IngredientKnowledgeResult
    createFailedResult(
            String ingredient) {

        IngredientKnowledgeResult result =
                new IngredientKnowledgeResult();

        result.setIngredient(
                ingredient
        );

        result.setSource(
                "GROQ"
        );

        result.setStatus(
                "AI_ANALYSIS_FAILED"
        );

        result.setAllergen(
                false
        );

        result.setAllergenType(
                "UNKNOWN"
        );

        return result;
    }
}