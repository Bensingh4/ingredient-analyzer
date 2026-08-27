package com.affordmed.ingredientanalyzer.dto;

public class IngredientKnowledgeResult {

    private String ingredient;
    private String function;
    private String benefits;
    private String potentialConcerns;
    private String suitableFor;
    private String cautionFor;
    private String riskLevel;
    private String source;
    private String status;
    private Boolean allergen;
    private String allergenType;

    public IngredientKnowledgeResult() {
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public String getIngredient() {
        return ingredient;
    }

    public String getFunction() {
        return function;
    }

    public String getBenefits() {
        return benefits;
    }

    public String getPotentialConcerns() {
        return potentialConcerns;
    }

    public String getSuitableFor() {
        return suitableFor;
    }

    public String getCautionFor() {
        return cautionFor;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getSource() {
        return source;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getAllergen() {
        return allergen;
    }

    // =========================================================
    // IMPORTANT
    // Existing code uses aiResult.isAllergen()
    // =========================================================

    public boolean isAllergen() {
        return Boolean.TRUE.equals(allergen);
    }

    public String getAllergenType() {
        return allergenType;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public void setPotentialConcerns(String potentialConcerns) {
        this.potentialConcerns = potentialConcerns;
    }

    public void setSuitableFor(String suitableFor) {
        this.suitableFor = suitableFor;
    }

    public void setCautionFor(String cautionFor) {
        this.cautionFor = cautionFor;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAllergen(Boolean allergen) {
        this.allergen = allergen;
    }

    public void setAllergenType(String allergenType) {
        this.allergenType = allergenType;
    }
}