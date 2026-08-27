package com.affordmed.ingredientanalyzer.scan;

import com.affordmed.ingredientanalyzer.ai.AiService;
import com.affordmed.ingredientanalyzer.dto.ProductAnalysisResult;
import com.affordmed.ingredientanalyzer.dto.ProductScanResult;
import com.affordmed.ingredientanalyzer.service.ProductAnalysisService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ScanService {

    private final AiService aiService;
    private final ProductAnalysisService productAnalysisService;

    public ScanService(
            AiService aiService,
            ProductAnalysisService productAnalysisService) {

        this.aiService = aiService;
        this.productAnalysisService = productAnalysisService;
    }

    public ProductAnalysisResult processImage(
            MultipartFile image) throws Exception {

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException(
                    "No image was uploaded"
            );
        }

        // Step 1: Extract product information using Gemini
        ProductScanResult scanResult =
                aiService.analyzeProductImage(image);

        // Step 2: Analyze all extracted ingredients
        return productAnalysisService.analyzeProduct(
                scanResult
        );
    }
}