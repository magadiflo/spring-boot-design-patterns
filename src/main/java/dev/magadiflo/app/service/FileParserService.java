package dev.magadiflo.app.service;

import dev.magadiflo.app.behavioral.strategy.auxiliary.FileParserFactory;
import dev.magadiflo.app.behavioral.strategy.context.FileParserContext;
import dev.magadiflo.app.behavioral.strategy.strategy.FileParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileParserService {

    private final FileParserFactory fileParserFactory;
    private final FileParserContext fileParserContext;

    public void processFile(MultipartFile file, String fileType) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío o no existe");
        }
        FileParser fileParserStrategy = this.fileParserFactory.getFileParserStrategy(fileType);
        this.fileParserContext.setFileParserStrategy(fileParserStrategy);
        this.fileParserContext.parserFile(file);
    }
}
