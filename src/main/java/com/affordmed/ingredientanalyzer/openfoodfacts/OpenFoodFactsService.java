package com.affordmed.ingredientanalyzer.openfoodfacts;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.affordmed.ingredientanalyzer.dto.OpenFoodFactsResponse;
@Service
public class OpenFoodFactsService {

    private final RestClient restClient;

    public OpenFoodFactsService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://world.openfoodfacts.org")
                .defaultHeader(
                        "User-Agent",
                        "IngredientAnalyzer/1.0 (student-project)"
                )
                .build();
    }

    public OpenFoodFactsResponse getProductByBarcode(String barcode) {

        return restClient
                .get()
                .uri("/api/v3/product/{code}?product_type=food", barcode)
                .retrieve()
                .body(OpenFoodFactsResponse.class);
    }
}