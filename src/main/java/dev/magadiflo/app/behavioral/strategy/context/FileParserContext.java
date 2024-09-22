package dev.magadiflo.app.behavioral.strategy.context;

import dev.magadiflo.app.behavioral.strategy.strategy.FileParser;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileParserContext {

    private FileParser fileParser;

    public void setFileParserStrategy(FileParser fileParser) {
        this.fileParser = fileParser;
    }

    public void parserFile(MultipartFile file) {
        if (this.fileParser == null) {
            throw new IllegalStateException("Estrategia no establecida");
        }
        this.fileParser.parse(file);
    }
}
