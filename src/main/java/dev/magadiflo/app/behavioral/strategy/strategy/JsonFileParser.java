package dev.magadiflo.app.behavioral.strategy.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Component("json")
public class JsonFileParser implements FileParser {
    @Override
    public void parse(MultipartFile file) {
        log.debug(file.getName());
        log.debug(file.getOriginalFilename());
        log.debug(file.getContentType());
    }
}
