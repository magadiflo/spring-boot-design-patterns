package dev.magadiflo.app.behavioral.strategy.auxiliary;

import dev.magadiflo.app.behavioral.strategy.strategy.FileParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileParserFactory {

    /**
     * La función de inyección de dependencia de Spring Boot construirá este mapa para nosotros e incluirá
     * todas las implementaciones disponibles del FileParser en el mapa, teniendo como clave el nombre del bean.
     * Lógicamente, el mapa se verá más o menos como se muestra a continuación.
     * {"csv": CsvFileParser,
     * "json": JsonFileParser,
     * "xml": XmlFileParser}
     */
    private final Map<String, FileParser> fileParserStrategies;

    /**
     * Retorna la implementación de FileParser adecuado dado un tipo de archivo
     *
     * @param fileType, uno de los tipos de archivos definidos en el nombre
     *                  de las implementaciones del FileParser (csv, json o xml)
     * @return FileParser
     */
    public FileParser getFileParserStrategy(String fileType) {
        FileParser fileParserStrategy = this.fileParserStrategies.get(fileType);
        if (Objects.isNull(fileParserStrategy)) {
            throw new IllegalArgumentException("Tipo de archivo no soportado");
        }
        return fileParserStrategy;
    }
}
