package com.affordmed.ingredientanalyzer.ai;

import com.affordmed.ingredientanalyzer.dto.IngredientKnowledgeResult;
import com.affordmed.ingredientanalyzer.dto.ProductScanResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String groqModel;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AiService(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.restClient =
                RestClient.builder().build();
    }


    // =========================================================
    // SIMPLE TEST
    // =========================================================

    public String askGemini(String question) {

        if (question == null ||
                question.isBlank()) {

            throw new IllegalArgumentException(
                    "Question cannot be empty"
            );
        }

        Map<String, Object> message =
                new HashMap<>();

        message.put(
                "role",
                "user"
        );

        message.put(
                "content",
                question
        );


        Map<String, Object> request =
                new HashMap<>();

        request.put(
                "model",
                groqModel
        );

        request.put(
                "messages",
                List.of(message)
        );

        request.put(
                "temperature",
                0
        );

        request.put(
                "max_completion_tokens",
                300
        );

        request.put(
                "reasoning_effort",
                "none"
        );


        return callGroqWithRetry(
                request,
                "TEST"
        );
    }


    // =========================================================
    // PRODUCT IMAGE ANALYSIS
    // =========================================================

    public ProductScanResult analyzeProductImage(
            MultipartFile image) throws Exception {

        if (image == null ||
                image.isEmpty()) {

            throw new IllegalArgumentException(
                    "Image is empty"
            );
        }


        String base64Image =
                Base64.getEncoder()
                        .encodeToString(
                                image.getBytes()
                        );


        String mimeType =
                image.getContentType();


        if (mimeType == null ||
                mimeType.isBlank()) {

            mimeType = "image/jpeg";
        }


        // -----------------------------------------------------
        // VERY SHORT PROMPT
        // -----------------------------------------------------

        String prompt = """
                Read the product label in this image.

                Return ONLY valid JSON.
                Do not use <think>.
                Do not use markdown.
                Do not explain.

                Required JSON:

                {
                  "productName": "string",
                  "category": "FOOD",
                  "ingredients": ["string"]
                }

                Extract every visible ingredient.

                Category must be one of:
                FOOD,
                BEVERAGE,
                COSMETIC,
                PERSONAL_CARE,
                HOUSEHOLD_CLEANING,
                LAUNDRY,
                ORAL_CARE,
                BABY_CARE,
                GROOMING

                Do not invent ingredients.
                """;


        // -----------------------------------------------------
        // IMAGE CONTENT
        // -----------------------------------------------------

        Map<String, Object> imageUrl =
                new HashMap<>();

        imageUrl.put(
                "url",
                "data:"
                        + mimeType
                        + ";base64,"
                        + base64Image
        );


        Map<String, Object> imageContent =
                new HashMap<>();

        imageContent.put(
                "type",
                "image_url"
        );

        imageContent.put(
                "image_url",
                imageUrl
        );


        Map<String, Object> textContent =
                new HashMap<>();

        textContent.put(
                "type",
                "text"
        );

        textContent.put(
                "text",
                prompt
        );


        List<Map<String, Object>> content =
                List.of(
                        textContent,
                        imageContent
                );


        Map<String, Object> message =
                new HashMap<>();

        message.put(
                "role",
                "user"
        );

        message.put(
                "content",
                content
        );


        // -----------------------------------------------------
        // REQUEST
        // -----------------------------------------------------

        Map<String, Object> request =
                new HashMap<>();

        request.put(
                "model",
                groqModel
        );

        request.put(
                "messages",
                List.of(message)
        );

        request.put(
                "temperature",
                0
        );

        request.put(
                "reasoning_effort",
                "none"
        );

        request.put(
                "max_completion_tokens",
                800
        );


        // -----------------------------------------------------
        // JSON MODE
        // -----------------------------------------------------

        request.put(
                "response_format",
                Map.of(
                        "type",
                        "json_object"
                )
        );


        // -----------------------------------------------------
        // CALL GROQ
        // -----------------------------------------------------

        String response =
                callGroqWithRetry(
                        request,
                        "PRODUCT_IMAGE"
                );


        try {

            String json =
                    extractJson(
                            response
                    );


            ProductScanResult result =
                    objectMapper.readValue(
                            json,
                            ProductScanResult.class
                    );


            return result;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse product analysis from Groq. "
                            + "Response: "
                            + response,
                    e
            );
        }
    }


    // =========================================================
    // SINGLE INGREDIENT
    // =========================================================

    public IngredientKnowledgeResult
    analyzeIngredientKnowledge(
            String ingredient,
            String category) {

        if (ingredient == null ||
                ingredient.isBlank()) {

            throw new IllegalArgumentException(
                    "Ingredient cannot be empty"
            );
        }


        String prompt = """
                Analyze this ingredient.

                Ingredient: %s
                Category: %s

                Return ONLY valid JSON.
                No explanation.
                No markdown.
                No <think>.

                {
                  "ingredient": "%s",
                  "function": "short",
                  "benefits": "short",
                  "potentialConcerns": "short",
                  "suitableFor": "short",
                  "cautionFor": "short",
                  "riskLevel": "LOW",
                  "allergen": false,
                  "allergenType": "NONE"
                }

                riskLevel:
                LOW, MODERATE, HIGH, UNKNOWN

                allergenType:
                NONE, MILK, SOY, TREE_NUT,
                PEANUT, GLUTEN, EGG, FISH,
                SHELLFISH, SESAME, UNKNOWN

                Keep answers short.
                """.formatted(
                ingredient,
                category,
                escapeJson(ingredient)
        );


        Map<String, Object> message =
                new HashMap<>();

        message.put(
                "role",
                "user"
        );

        message.put(
                "content",
                prompt
        );


        Map<String, Object> request =
                new HashMap<>();

        request.put(
                "model",
                groqModel
        );

        request.put(
                "messages",
                List.of(message)
        );

        request.put(
                "temperature",
                0
        );

        request.put(
                "reasoning_effort",
                "none"
        );

        request.put(
                "max_completion_tokens",
                350
        );

        request.put(
                "response_format",
                Map.of(
                        "type",
                        "json_object"
                )
        );


        String response =
                callGroqWithRetry(
                        request,
                        ingredient
                );


        try {

            String json =
                    extractJson(
                            response
                    );


            IngredientKnowledgeResult result =
                    objectMapper.readValue(
                            json,
                            IngredientKnowledgeResult.class
                    );


            result.setIngredient(
                    ingredient
            );

            result.setSource(
                    "GROQ"
            );

            result.setStatus(
                    "AI_ANALYZED"
            );


            return result;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse ingredient analysis for "
                            + ingredient
                            + ". Response: "
                            + response,
                    e
            );
        }
    }


    // =========================================================
    // BATCH INGREDIENT ANALYSIS
    //
    // ONE REQUEST FOR ALL INGREDIENTS
    // =========================================================

    public List<IngredientKnowledgeResult>
    analyzeIngredientsKnowledge(
            List<String> ingredients,
            String category) {

        List<IngredientKnowledgeResult> finalResults =
                new ArrayList<>();


        if (ingredients == null ||
                ingredients.isEmpty()) {

            return finalResults;
        }


        // -----------------------------------------------------
        // BUILD INGREDIENT LIST
        // -----------------------------------------------------

        StringBuilder ingredientList =
                new StringBuilder();


        for (String ingredient :
                ingredients) {

            if (ingredient == null ||
                    ingredient.isBlank()) {

                continue;
            }

            ingredientList
                    .append(ingredient)
                    .append("\n");
        }


        // -----------------------------------------------------
        // SHORT BATCH PROMPT
        // -----------------------------------------------------

        String prompt = """
                Analyze all ingredients below.

                Category: %s

                Ingredients:
                %s

                Return ONLY valid JSON.
                No explanation.
                No markdown.
                No <think>.

                Return:

                {
                  "results": [
                    {
                      "ingredient": "name",
                      "function": "short",
                      "benefits": "short",
                      "potentialConcerns": "short",
                      "suitableFor": "short",
                      "cautionFor": "short",
                      "riskLevel": "LOW",
                      "allergen": false,
                      "allergenType": "NONE"
                    }
                  ]
                }

                Use these risk levels only:
                LOW, MODERATE, HIGH, UNKNOWN

                Use these allergen types only:
                NONE, MILK, SOY, TREE_NUT,
                PEANUT, GLUTEN, EGG, FISH,
                SHELLFISH, SESAME, UNKNOWN

                Return exactly one result for every ingredient.
                Keep descriptions very short.
                """.formatted(
                category,
                ingredientList
        );


        // -----------------------------------------------------
        // MESSAGE
        // -----------------------------------------------------

        Map<String, Object> message =
                new HashMap<>();

        message.put(
                "role",
                "user"
        );

        message.put(
                "content",
                prompt
        );


        // -----------------------------------------------------
        // REQUEST
        // -----------------------------------------------------

        Map<String, Object> request =
                new HashMap<>();

        request.put(
                "model",
                groqModel
        );

        request.put(
                "messages",
                List.of(message)
        );

        request.put(
                "temperature",
                0
        );

        request.put(
                "reasoning_effort",
                "none"
        );


        /*
         * Seven ingredients need more output,
         * but descriptions are intentionally short.
         */

        request.put(
                "max_completion_tokens",
                1800
        );


        // -----------------------------------------------------
        // JSON MODE
        // -----------------------------------------------------

        request.put(
                "response_format",
                Map.of(
                        "type",
                        "json_object"
                )
        );


        // -----------------------------------------------------
        // ONE GROQ REQUEST
        // -----------------------------------------------------

        String response =
                callGroqWithRetry(
                        request,
                        "INGREDIENT_BATCH"
                );


        try {

            String json =
                    extractJson(
                            response
                    );


            JsonNode root =
                    objectMapper.readTree(
                            json
                    );


            JsonNode resultsNode =
                    root.get(
                            "results"
                    );


            if (resultsNode == null ||
                    !resultsNode.isArray()) {

                throw new RuntimeException(
                        "Groq response does not contain "
                                + "a results array. Response: "
                                + response
                );
            }


            // -------------------------------------------------
            // READ RESULTS
            // -------------------------------------------------

            Map<String, IngredientKnowledgeResult>
                    resultMap =
                    new HashMap<>();


            for (JsonNode node :
                    resultsNode) {

                IngredientKnowledgeResult result =
                        objectMapper.treeToValue(
                                node,
                                IngredientKnowledgeResult.class
                        );


                if (result == null) {
                    continue;
                }


                result.setSource(
                        "GROQ"
                );

                result.setStatus(
                        "AI_ANALYZED"
                );


                resultMap.put(
                        normalize(
                                result.getIngredient()
                        ),
                        result
                );
            }


            // -------------------------------------------------
            // RESTORE ORIGINAL ORDER
            // -------------------------------------------------

            for (String ingredient :
                    ingredients) {

                if (ingredient == null ||
                        ingredient.isBlank()) {

                    continue;
                }


                IngredientKnowledgeResult result =
                        resultMap.get(
                                normalize(
                                        ingredient
                                )
                        );


                if (result == null) {

                    result =
                            createFailedResult(
                                    ingredient
                            );

                } else {

                    /*
                     * Always use the exact ingredient
                     * name received from OCR.
                     */

                    result.setIngredient(
                            ingredient
                    );
                }


                finalResults.add(
                        result
                );
            }


            return finalResults;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse ingredient analysis "
                            + "from Groq. Response: "
                            + response,
                    e
            );
        }
    }


    // =========================================================
    // GROQ REQUEST WITH LIMITED RETRY
    // =========================================================

    private String callGroqWithRetry(
            Map<String, Object> request,
            String requestName) {

        int maxAttempts = 2;


        for (int attempt = 1;
             attempt <= maxAttempts;
             attempt++) {

            try {

                return callGroq(
                        request
                );

            } catch (RuntimeException e) {

                String message =
                        e.getMessage();


                boolean rateLimit =
                        message != null &&
                                (
                                        message.contains(
                                                "429"
                                        )
                                                ||
                                                message.contains(
                                                        "rate_limit_exceeded"
                                                )
                                );


                /*
                 * Only retry 429.
                 *
                 * Never retry 400 JSON errors.
                 */

                if (!rateLimit ||
                        attempt == maxAttempts) {

                    throw e;
                }


                long wait =
                        10000L;


                System.out.println(
                        "Groq rate limit reached for "
                                + requestName
                                + ". Waiting "
                                + wait
                                + " ms."
                );


                sleep(
                        wait
                );
            }
        }


        throw new RuntimeException(
                "Groq request failed: "
                        + requestName
        );
    }


    // =========================================================
    // GROQ HTTP CALL
    // =========================================================

    private String callGroq(
            Map<String, Object> request) {

        try {

            String response =
                    restClient
                            .post()
                            .uri(
                                    groqApiUrl
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .header(
                                    "Authorization",
                                    "Bearer "
                                            + groqApiKey
                            )
                            .body(
                                    request
                            )
                            .retrieve()
                            .body(
                                    String.class
                            );


            if (response == null ||
                    response.isBlank()) {

                throw new RuntimeException(
                        "Groq returned empty response"
                );
            }


            JsonNode root =
                    objectMapper.readTree(
                            response
                    );


            JsonNode choices =
                    root.get(
                            "choices"
                    );


            if (choices == null ||
                    !choices.isArray() ||
                    choices.isEmpty()) {

                throw new RuntimeException(
                        "Groq returned no choices. "
                                + response
                );
            }


            JsonNode firstChoice =
                    choices.get(
                            0
                    );


            JsonNode message =
                    firstChoice.get(
                            "message"
                    );


            if (message == null ||
                    message.isNull()) {

                throw new RuntimeException(
                        "Groq response has no message"
                );
            }


            JsonNode content =
                    message.get(
                            "content"
                    );


            if (content == null ||
                    content.isNull()) {

                throw new RuntimeException(
                        "Groq response has no content"
                );
            }


            String result =
                    content.asText();


            if (result == null ||
                    result.isBlank()) {

                throw new RuntimeException(
                        "Groq returned empty content"
                );
            }


            return result.trim();


        } catch (
                HttpClientErrorException e) {

            throw new RuntimeException(
                    "Groq API request failed: "
                            + e.getStatusCode()
                            + " "
                            + e.getResponseBodyAsString(),
                    e
            );


        } catch (Exception e) {

            throw new RuntimeException(
                    "Groq API request failed: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // =========================================================
    // JSON EXTRACTION
    // =========================================================

    private String extractJson(
            String response) {

        if (response == null ||
                response.isBlank()) {

            throw new RuntimeException(
                    "Empty Groq response"
            );
        }


        String text =
                response.trim();


        // -----------------------------------------------------
        // REMOVE THINK BLOCKS
        // -----------------------------------------------------

        while (true) {

            int start =
                    text.indexOf(
                            "<think>"
                    );


            if (start == -1) {
                break;
            }


            int end =
                    text.indexOf(
                            "</think>",
                            start
                    );


            if (end == -1) {

                /*
                 * If reasoning is incomplete,
                 * discard it.
                 */

                text =
                        text.substring(
                                0,
                                start
                        );

                break;
            }


            text =
                    text.substring(
                            0,
                            start
                    )
                            +
                            text.substring(
                                    end + "</think>".length()
                            );
        }


        text =
                text.trim();


        // -----------------------------------------------------
        // REMOVE MARKDOWN
        // -----------------------------------------------------

        text =
                text.replace(
                        "```json",
                        ""
                );


        text =
                text.replace(
                        "```",
                        ""
                );


        text =
                text.trim();


        // -----------------------------------------------------
        // FIND JSON OBJECT
        // -----------------------------------------------------

        int start =
                text.indexOf(
                        "{"
                );


        if (start == -1) {

            throw new RuntimeException(
                    "Groq response does not contain JSON. "
                            + "Response: "
                            + text
            );
        }


        int depth = 0;

        boolean insideString =
                false;

        boolean escaped =
                false;


        for (int i = start;
             i < text.length();
             i++) {

            char c =
                    text.charAt(i);


            if (escaped) {

                escaped = false;

                continue;
            }


            if (c == '\\' &&
                    insideString) {

                escaped = true;

                continue;
            }


            if (c == '"') {

                insideString =
                        !insideString;

                continue;
            }


            if (insideString) {
                continue;
            }


            if (c == '{') {

                depth++;
            }


            if (c == '}') {

                depth--;


                if (depth == 0) {

                    return text.substring(
                            start,
                            i + 1
                    ).trim();
                }
            }
        }


        throw new RuntimeException(
                "Groq returned incomplete JSON. "
                        + "Response: "
                        + text
        );
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


    // =========================================================
    // NORMALIZE
    // =========================================================

    private String normalize(
            String value) {

        if (value == null) {

            return "";
        }


        return value
                .trim()
                .toLowerCase()
                .replaceAll(
                        "[^a-z0-9 ]",
                        ""
                )
                .replaceAll(
                        "\\s+",
                        " "
                );
    }


    // =========================================================
    // ESCAPE JSON
    // =========================================================

    private String escapeJson(
            String value) {

        if (value == null) {

            return "";
        }


        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                );
    }


    // =========================================================
    // SLEEP
    // =========================================================

    private void sleep(
            long milliseconds) {

        try {

            Thread.sleep(
                    milliseconds
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();


            throw new RuntimeException(
                    "Groq request interrupted",
                    e
            );
        }
    }
}