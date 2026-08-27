package com.affordmed.ingredientanalyzer.dto;

public class IngredientAnalysisResult {

    private String ingredient;
    private String status;
    private String function;
    private String benefits;
    private String potentialConcerns;
    private String suitableFor;
    private String cautionFor;
    private String riskLevel;
    private String source;

    private boolean allergen;
    private String allergenType;

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getPotentialConcerns() {
        return potentialConcerns;
    }

    public void setPotentialConcerns(String potentialConcerns) {
        this.potentialConcerns = potentialConcerns;
    }

    public String getSuitableFor() {
        return suitableFor;
    }

    public void setSuitableFor(String suitableFor) {
        this.suitableFor = suitableFor;
    }

    public String getCautionFor() {
        return cautionFor;
    }

    public void setCautionFor(String cautionFor) {
        this.cautionFor = cautionFor;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isAllergen() {
        return allergen;
    }

    public void setAllergen(boolean allergen) {
        this.allergen = allergen;
    }

    public String getAllergenType() {
        return allergenType;
    }

    public void setAllergenType(String allergenType) {
        this.allergenType = allergenType;
    }
}