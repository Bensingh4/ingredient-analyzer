package com.affordmed.ingredientanalyzer.service;

import com.affordmed.ingredientanalyzer.dto.IngredientAnalysisResult;
import com.affordmed.ingredientanalyzer.dto.ProductAnalysisResult;
import com.affordmed.ingredientanalyzer.dto.ProductScanResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductAnalysisService {

    private final IngredientAnalysisService ingredientAnalysisService;

    public ProductAnalysisService(
            IngredientAnalysisService ingredientAnalysisService) {

        this.ingredientAnalysisService =
                ingredientAnalysisService;
    }

    public ProductAnalysisResult analyzeProduct(
            ProductScanResult scanResult) {

        ProductAnalysisResult result =
                new ProductAnalysisResult();

        result.setProductName(
                scanResult.getProductName()
        );

        result.setCategory(
                scanResult.getCategory()
        );

        // Analyze every ingredient
        List<IngredientAnalysisResult> ingredientResults =
                ingredientAnalysisService.analyzeIngredients(
                        scanResult.getIngredients(),
                        scanResult.getCategory()
                );

        result.setIngredients(ingredientResults);

        calculateOverallAssessment(
                result,
                ingredientResults
        );

        return result;
    }

    private void calculateOverallAssessment(
            ProductAnalysisResult result,
            List<IngredientAnalysisResult> ingredients) {

        List<String> concerns = new ArrayList<>();
        List<String> allergens = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        int highRiskCount = 0;
        int moderateRiskCount = 0;
        int reviewCount = 0;

        // ---------------------------------------------------------
        // Analyze every ingredient
        // ---------------------------------------------------------

        for (IngredientAnalysisResult ingredient : ingredients) {

            String ingredientName =
                    ingredient.getIngredient();

            String status =
                    ingredient.getStatus();

            String risk =
                    ingredient.getRiskLevel();

            // -----------------------------------------------------
            // 1. ALLERGEN INFORMATION
            // -----------------------------------------------------

            if (ingredient.isAllergen()) {

                String allergenType =
                        ingredient.getAllergenType();

                if (allergenType != null
                        && !allergenType.equalsIgnoreCase("NONE")
                        && !allergenType.equalsIgnoreCase("UNKNOWN")) {

                    allergens.add(
                            ingredientName
                                    + " ("
                                    + allergenType
                                    + ")"
                    );

                    concerns.add(
                            ingredientName
                                    + " may be an allergen."
                    );
                }
            }

            // -----------------------------------------------------
            // 2. INGREDIENT REQUIRES REVIEW
            // -----------------------------------------------------

            if ("NOT_FOUND".equals(status)
                    || "NO_CATEGORY_DATA".equals(status)) {

                reviewCount++;

                concerns.add(
                        ingredientName
                                + " requires further verification."
                );
            }

            // -----------------------------------------------------
            // 3. RISK LEVEL
            //
            // IMPORTANT:
            // We only inspect the START of the risk description.
            //
            // Example:
            //
            // "Low for general population. High for allergies."
            //
            // should NOT be classified as HIGH.
            // -----------------------------------------------------

            if (risk != null && !risk.isBlank()) {

                String normalizedRisk =
                        risk.trim().toUpperCase();

                if (startsWithHighRisk(normalizedRisk)) {

                    highRiskCount++;

                } else if (startsWithModerateRisk(normalizedRisk)) {

                    moderateRiskCount++;
                }
            }
        }

        // ---------------------------------------------------------
        // Remove duplicate allergens
        // ---------------------------------------------------------

        allergens =
                new ArrayList<>(
                        allergens.stream()
                                .distinct()
                                .toList()
                );

        // ---------------------------------------------------------
        // Determine overall risk
        // ---------------------------------------------------------

        if (highRiskCount > 0) {

            result.setOverallRiskLevel("HIGH");

        } else if (moderateRiskCount > 0) {

            result.setOverallRiskLevel("MODERATE");

        } else if (reviewCount > 0) {

            result.setOverallRiskLevel("REVIEW");

        } else {

            result.setOverallRiskLevel("LOW");
        }

        // ---------------------------------------------------------
        // Overall assessment
        // ---------------------------------------------------------

        switch (result.getOverallRiskLevel()) {

            case "HIGH":

                result.setOverallAssessment(
                        "One or more ingredients have a high "
                                + "general-population risk classification "
                                + "and require careful review."
                );

                recommendations.add(
                        "Review the high-risk ingredients before "
                                + "regular use or consumption."
                );

                break;

            case "MODERATE":

                result.setOverallAssessment(
                        "Some ingredients may require attention "
                                + "depending on the amount consumed, "
                                + "frequency of use, and individual "
                                + "circumstances."
                );

                recommendations.add(
                        "Consider moderate use and review the "
                                + "highlighted ingredients."
                );

                break;

            case "REVIEW":

                result.setOverallAssessment(
                        "Some ingredient information could not be "
                                + "fully verified. Additional information "
                                + "is recommended before making a final "
                                + "decision."
                );

                recommendations.add(
                        "Verify the ingredients marked for review."
                );

                break;

            default:

                result.setOverallAssessment(
                        "No significant general-population concerns "
                                + "were identified from the currently "
                                + "available ingredient information."
                );

                recommendations.add(
                        "Use the product according to its intended "
                                + "purpose and label instructions."
                );

                break;
        }

        // ---------------------------------------------------------
        // Allergen recommendation
        // ---------------------------------------------------------

        if (!allergens.isEmpty()) {

            recommendations.add(
                    "People with relevant allergies should check "
                            + "the identified allergens before using "
                            + "or consuming this product."
            );
        }

        // ---------------------------------------------------------
        // Review recommendation
        // ---------------------------------------------------------

        if (reviewCount > 0) {

            recommendations.add(
                    "Some ingredient information is incomplete or "
                            + "category-specific data is unavailable."
            );
        }

        result.setKeyConcerns(concerns);
        result.setAllergens(allergens);
        result.setRecommendations(recommendations);
    }

    // -------------------------------------------------------------
    // Check whether the GENERAL risk starts with HIGH
    // -------------------------------------------------------------

    private boolean startsWithHighRisk(String risk) {

        return risk.startsWith("HIGH")
                || risk.startsWith("HIGH RISK");
    }

    // -------------------------------------------------------------
    // Check whether the GENERAL risk starts with MODERATE
    // -------------------------------------------------------------

    private boolean startsWithModerateRisk(String risk) {

        return risk.startsWith("MODERATE")
                || risk.startsWith("MODERATE RISK");
    }
}