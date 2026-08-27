package com.affordmed.ingredientanalyzer.dto;

import java.util.List;

public class ProductAnalysisResult {

    private String productName;
    private String category;

    private List<IngredientAnalysisResult> ingredients;

    private String overallRiskLevel;
    private String overallAssessment;

    private List<String> keyConcerns;
    private List<String> allergens;
    private List<String> recommendations;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<IngredientAnalysisResult> getIngredients() {
        return ingredients;
    }

    public void setIngredients(
            List<IngredientAnalysisResult> ingredients) {
        this.ingredients = ingredients;
    }

    public String getOverallRiskLevel() {
        return overallRiskLevel;
    }

    public void setOverallRiskLevel(String overallRiskLevel) {
        this.overallRiskLevel = overallRiskLevel;
    }

    public String getOverallAssessment() {
        return overallAssessment;
    }

    public void setOverallAssessment(String overallAssessment) {
        this.overallAssessment = overallAssessment;
    }

    public List<String> getKeyConcerns() {
        return keyConcerns;
    }

    public void setKeyConcerns(
            List<String> keyConcerns) {
        this.keyConcerns = keyConcerns;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(
            List<String> allergens) {
        this.allergens = allergens;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(
            List<String> recommendations) {
        this.recommendations = recommendations;
    }
}