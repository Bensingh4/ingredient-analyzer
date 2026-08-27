package com.affordmed.ingredientanalyzer.service;

import com.affordmed.ingredientanalyzer.dto.IngredientAnalysisResult;
import com.affordmed.ingredientanalyzer.dto.IngredientKnowledgeResult;
import com.affordmed.ingredientanalyzer.entity.Ingredient;
import com.affordmed.ingredientanalyzer.entity.IngredientUsage;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IngredientAnalysisService {

    private final IngredientService ingredientService;
    private final IngredientUsageService ingredientUsageService;
    private final IngredientKnowledgeService ingredientKnowledgeService;

    public IngredientAnalysisService(
            IngredientService ingredientService,
            IngredientUsageService ingredientUsageService,
            IngredientKnowledgeService ingredientKnowledgeService) {

        this.ingredientService = ingredientService;
        this.ingredientUsageService = ingredientUsageService;
        this.ingredientKnowledgeService = ingredientKnowledgeService;
    }

    // =========================================================
    // SINGLE INGREDIENT DATABASE ANALYSIS
    // =========================================================

    public List<IngredientUsage> analyzeIngredient(
            String ingredientName,
            String category) {

        Optional<Ingredient> ingredient =
                ingredientService.findByName(ingredientName);

        if (ingredient.isEmpty()) {
            return List.of();
        }

        return ingredientUsageService
                .getByIngredientAndCategory(
                        ingredient.get().getId(),
                        category
                );
    }

    // =========================================================
    // COMPLETE INGREDIENT ANALYSIS
    // =========================================================

    public List<IngredientAnalysisResult> analyzeIngredients(
            List<String> ingredientNames,
            String category) {

        List<IngredientAnalysisResult> results =
                new ArrayList<>();

        List<String> unknownIngredients =
                new ArrayList<>();

        Map<String, IngredientAnalysisResult> knownResults =
                new HashMap<>();

        // =====================================================
        // STEP 1
        // CHECK POSTGRESQL KNOWLEDGE BASE
        // =====================================================

        for (String ingredientName : ingredientNames) {

            if (ingredientName == null ||
                    ingredientName.isBlank()) {

                continue;
            }

            Optional<Ingredient> ingredient =
                    ingredientService.findByName(
                            ingredientName
                    );

            if (ingredient.isEmpty()) {

                unknownIngredients.add(
                        ingredientName
                );

                continue;
            }

            List<IngredientUsage> usages =
                    ingredientUsageService
                            .getByIngredientAndCategory(
                                    ingredient.get().getId(),
                                    category
                            );

            if (usages.isEmpty()) {

                unknownIngredients.add(
                        ingredientName
                );

                continue;
            }

            IngredientUsage usage =
                    usages.get(0);

            IngredientAnalysisResult result =
                    new IngredientAnalysisResult();

            result.setIngredient(
                    ingredientName
            );

            result.setStatus(
                    "FOUND"
            );

            result.setFunction(
                    usage.getFunction()
            );

            result.setBenefits(
                    usage.getBenefits()
            );

            result.setPotentialConcerns(
                    usage.getPotentialConcerns()
            );

            result.setSuitableFor(
                    usage.getSuitableFor()
            );

            result.setCautionFor(
                    usage.getCautionFor()
            );

            result.setRiskLevel(
                    usage.getRiskLevel()
            );

            result.setSource(
                    usage.getSource()
            );

            // Apply deterministic allergen detection
            applyAllergenRules(
                    result,
                    ingredientName
            );

            knownResults.put(
                    normalize(ingredientName),
                    result
            );
        }

        // =====================================================
        // STEP 2
        // ANALYZE UNKNOWN INGREDIENTS WITH GROQ
        // =====================================================

        Map<String, IngredientKnowledgeResult> aiResults =
                new HashMap<>();

        if (!unknownIngredients.isEmpty()) {

            List<IngredientKnowledgeResult> knowledgeResults =
                    ingredientKnowledgeService
                            .analyzeIngredientsWithAI(
                                    unknownIngredients,
                                    category
                            );

            for (IngredientKnowledgeResult aiResult :
                    knowledgeResults) {

                if (aiResult == null) {
                    continue;
                }

                String normalizedName =
                        normalize(
                                aiResult.getIngredient()
                        );

                aiResults.put(
                        normalizedName,
                        aiResult
                );
            }
        }

        // =====================================================
        // STEP 3
        // REBUILD RESULTS IN ORIGINAL ORDER
        // =====================================================

        for (String ingredientName :
                ingredientNames) {

            if (ingredientName == null ||
                    ingredientName.isBlank()) {

                continue;
            }

            String normalizedName =
                    normalize(ingredientName);

            // -------------------------------------------------
            // PostgreSQL result
            // -------------------------------------------------

            if (knownResults.containsKey(
                    normalizedName)) {

                results.add(
                        knownResults.get(
                                normalizedName
                        )
                );

                continue;
            }

            // -------------------------------------------------
            // Groq result
            // -------------------------------------------------

            IngredientKnowledgeResult aiResult =
                    aiResults.get(
                            normalizedName
                    );

            if (aiResult == null) {

                IngredientAnalysisResult result =
                        new IngredientAnalysisResult();

                result.setIngredient(
                        ingredientName
                );

                result.setStatus(
                        "AI_ANALYSIS_FAILED"
                );

                result.setSource(
                        "GROQ"
                );

                // Still check deterministic allergens
                applyAllergenRules(
                        result,
                        ingredientName
                );

                results.add(result);

                continue;
            }

            IngredientAnalysisResult result =
                    new IngredientAnalysisResult();

            result.setIngredient(
                    ingredientName
            );

            result.setStatus(
                    aiResult.getStatus() != null
                            ? aiResult.getStatus()
                            : "AI_ANALYZED"
            );

            result.setFunction(
                    aiResult.getFunction()
            );

            result.setBenefits(
                    aiResult.getBenefits()
            );

            result.setPotentialConcerns(
                    aiResult.getPotentialConcerns()
            );

            result.setSuitableFor(
                    aiResult.getSuitableFor()
            );

            result.setCautionFor(
                    aiResult.getCautionFor()
            );

            result.setRiskLevel(
                    aiResult.getRiskLevel()
            );

            result.setSource(
                    "GROQ"
            );

            // Start with AI result
            result.setAllergen(
                    aiResult.isAllergen()
            );

            result.setAllergenType(
                    aiResult.getAllergenType()
            );

            // -------------------------------------------------
            // IMPORTANT:
            // Backend allergen rules override AI when a clear
            // allergen is found in the ingredient name.
            // -------------------------------------------------

            applyAllergenRules(
                    result,
                    ingredientName
            );

            results.add(result);
        }

        return results;
    }

    // =========================================================
    // DETERMINISTIC ALLERGEN DETECTION
    // =========================================================

    private void applyAllergenRules(
            IngredientAnalysisResult result,
            String ingredientName) {

        if (result == null ||
                ingredientName == null) {

            return;
        }

        String value =
                ingredientName
                        .toLowerCase(Locale.ROOT)
                        .trim();

        // -----------------------------------------------------
        // MILK
        // -----------------------------------------------------

        if (containsAny(
                value,
                "milk",
                "skim milk",
                "skimmed milk",
                "milk powder",
                "skim milk powder",
                "skimmed milk powder",
                "whey",
                "casein",
                "caseinate",
                "lactose",
                "butter",
                "cream"
        )) {

            setAllergen(
                    result,
                    "MILK"
            );

            return;
        }

        // -----------------------------------------------------
        // SOY
        // -----------------------------------------------------

        if (containsAny(
                value,
                "soy",
                "soya",
                "soybean",
                "soy lecithin",
                "soya lecithin"
        )) {

            setAllergen(
                    result,
                    "SOY"
            );

            return;
        }

        // -----------------------------------------------------
        // TREE NUTS
        // -----------------------------------------------------

        if (containsAny(
                value,
                "hazelnut",
                "hazelnuts",
                "almond",
                "almonds",
                "walnut",
                "walnuts",
                "cashew",
                "cashews",
                "pistachio",
                "pistachios",
                "pecan",
                "pecans",
                "macadamia",
                "brazil nut",
                "brazil nuts"
        )) {

            setAllergen(
                    result,
                    "TREE_NUT"
            );

            return;
        }

        // -----------------------------------------------------
        // PEANUT
        // -----------------------------------------------------

        if (containsAny(
                value,
                "peanut",
                "peanuts",
                "groundnut",
                "groundnuts"
        )) {

            setAllergen(
                    result,
                    "PEANUT"
            );

            return;
        }

        // -----------------------------------------------------
        // EGG
        // -----------------------------------------------------

        if (containsAny(
                value,
                "egg",
                "eggs",
                "egg powder",
                "egg white",
                "egg yolk",
                "albumen",
                "albumin"
        )) {

            setAllergen(
                    result,
                    "EGG"
            );

            return;
        }

        // -----------------------------------------------------
        // WHEAT
        // -----------------------------------------------------

        if (containsAny(
                value,
                "wheat",
                "wheat flour",
                "wheat starch"
        )) {

            setAllergen(
                    result,
                    "WHEAT"
            );

            return;
        }

        // -----------------------------------------------------
        // SESAME
        // -----------------------------------------------------

        if (containsAny(
                value,
                "sesame",
                "sesame seed",
                "sesame seeds",
                "tahini"
        )) {

            setAllergen(
                    result,
                    "SESAME"
            );

            return;
        }

        // -----------------------------------------------------
        // FISH
        // -----------------------------------------------------

        if (containsAny(
                value,
                "fish",
                "anchovy",
                "anchovies",
                "cod",
                "salmon",
                "tuna",
                "sardine",
                "sardines"
        )) {

            setAllergen(
                    result,
                    "FISH"
            );

            return;
        }

        // -----------------------------------------------------
        // SHELLFISH
        // -----------------------------------------------------

        if (containsAny(
                value,
                "shellfish",
                "shrimp",
                "prawn",
                "prawns",
                "crab",
                "crabs",
                "lobster",
                "lobsters",
                "clam",
                "clams",
                "mussel",
                "mussels",
                "oyster",
                "oysters"
        )) {

            setAllergen(
                    result,
                    "SHELLFISH"
            );

            return;
        }

        // -----------------------------------------------------
        // If no known allergen was detected
        // -----------------------------------------------------

        if (!result.isAllergen()) {

            result.setAllergen(
                    false
            );

            if (result.getAllergenType() == null ||
                    result.getAllergenType().isBlank()) {

                result.setAllergenType(
                        "NONE"
                );
            }
        }
    }

    // =========================================================
    // SET ALLERGEN
    // =========================================================

    private void setAllergen(
            IngredientAnalysisResult result,
            String allergenType) {

        result.setAllergen(
                true
        );

        result.setAllergenType(
                allergenType
        );
    }

    // =========================================================
    // CHECK MULTIPLE KEYWORDS
    // =========================================================

    private boolean containsAny(
            String value,
            String... keywords) {

        for (String keyword : keywords) {

            if (value.contains(
                    keyword.toLowerCase(
                            Locale.ROOT
                    )
            )) {

                return true;
            }
        }

        return false;
    }

    // =========================================================
    // NORMALIZE INGREDIENT NAME
    // =========================================================

    private String normalize(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll(
                        "\\([^)]*%\\)",
                        ""
                )
                .replaceAll(
                        "[^a-z0-9 ]",
                        " "
                )
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }
}