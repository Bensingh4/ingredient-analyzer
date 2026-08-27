package com.affordmed.ingredientanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {

    @JsonProperty("product_name")
    private String productName;

    private String brands;

    @JsonProperty("ingredients_text_en")
    private String ingredientsText;

    private String allergens;

    @JsonProperty("nutriscore_grade")
    private String nutriscoreGrade;

    @JsonProperty("nutriscore_score")
    private Integer nutriscoreScore;

    private Nutriments nutriments;
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getIngredientsText() {
        return ingredientsText;
    }

    public void setIngredientsText(String ingredientsText) {
        this.ingredientsText = ingredientsText;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public String getNutriscoreGrade() {
        return nutriscoreGrade;
    }

    public void setNutriscoreGrade(String nutriscoreGrade) {
        this.nutriscoreGrade = nutriscoreGrade;
    }

    public Integer getNutriscoreScore() {
        return nutriscoreScore;
    }
    public Nutriments getNutriments() {
        return nutriments;
    }

    public void setNutriments(Nutriments nutriments) {
        this.nutriments = nutriments;
    }

    public void setNutriscoreScore(Integer nutriscoreScore) {
        this.nutriscoreScore = nutriscoreScore;
    }
}