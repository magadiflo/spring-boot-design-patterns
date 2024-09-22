package dev.magadiflo.app.controller;

import dev.magadiflo.app.service.FileParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/strategy/files")
public class FileParserController {

    private final FileParserService fileParserService;

    @PostMapping(path = "/{fileType}")
    public ResponseEntity<Void> analyzeFile(@PathVariable String fileType, @RequestParam MultipartFile file) {
        this.fileParserService.processFile(file, fileType);
        return ResponseEntity.noContent().build();
    }
}
