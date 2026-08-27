package com.affordmed.ingredientanalyzer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ingredient_usages")
public class IngredientUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private String function;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "potential_concerns", columnDefinition = "TEXT")
    private String potentialConcerns;

    @Column(name = "suitable_for", columnDefinition = "TEXT")
    private String suitableFor;

    @Column(name = "caution_for", columnDefinition = "TEXT")
    private String cautionFor;

    @Column(name = "risk_level")
    private String riskLevel;

    private String source;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    public IngredientUsage() {
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}