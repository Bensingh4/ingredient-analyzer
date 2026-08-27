package com.affordmed.ingredientanalyzer.scan;

import com.affordmed.ingredientanalyzer.dto.ProductAnalysisResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/scans")
@CrossOrigin(origins = "http://localhost:5173")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping
    public ProductAnalysisResult uploadImage(
            @RequestParam("image") MultipartFile image)
            throws Exception {

        return scanService.processImage(image);
    }
}