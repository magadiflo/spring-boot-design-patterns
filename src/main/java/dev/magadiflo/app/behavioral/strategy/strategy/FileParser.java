package dev.magadiflo.app.behavioral.strategy.strategy;

import org.springframework.web.multipart.MultipartFile;

public interface FileParser {
    void parse(MultipartFile file);
}
