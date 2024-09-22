# Implementando Patrones de Diseño en Spring Boot

En este repositorio agregaré diversas funcionalidades, cada una responderá al uso de algún patrón de diseño,
precisamente en eso se basa este repositorio, en la implementación de patrones de diseño utilizando Spring Boot.

**Referencias**

- [java-design-patterns](https://github.com/magadiflo/java-design-patterns)
- [Medium | Implementing the Strategy Design pattern in Spring Boot](https://medium.com/codex/implementing-the-strategy-design-pattern-in-spring-boot-df3adb9ceb4a)

---

## Strategy Design pattern

El Patrón de Diseño de Estrategia es un patrón de comportamiento que nos permite seleccionar el comportamiento de un
algoritmo en tiempo de ejecución. Este patrón nos permite definir un conjunto de algoritmos, colocarlos en diferentes
clases y hacerlos intercambiables.

Esto es solo una definición, pero entendamos mejor conociendo el problema que estamos tratando de resolver.

### El problema

Digamos que estás trabajando en una función llamada `Analizador de archivos` (File Parser). Necesitas escribir una API
donde puedas cargar un archivo y nuestro sistema debería poder extraer los datos de este y guardarlos en la base de
datos. Actualmente, se nos pide que admitamos archivos `CSV`, `JSON` y `XML`. Nuestra solución inmediata se vería así:

````java

@Service
public class FileParserService {

    public void parse(File file, String fileType) {
        if (Objects.equals(fileType, "CSV")) {
            // TODO : a huge implementation to parse CSV file and persist data in db
        } else if (Objects.equals(fileType, "JSON")) {
            // TODO : a huge implementation to parse JSON file and persist data in db
        } else if (Objects.equals(fileType, "XML")) {
            // TODO : a huge implementation to parse XML file and persist data in db
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

}
````

Ahora todo parece ir bien desde la perspectiva empresarial, pero las cosas se pondrán peor cuando queramos admitir más
tipos de archivos en el futuro. Comenzaremos a agregar varios bloques else if y el tamaño de la clase crecerá
rápidamente, lo que eventualmente se volverá demasiado difícil de mantener. Cualquier cambio en una de las
implementaciones del analizador de archivos afectará a toda la clase, lo que aumentará la posibilidad de introducir un
error en una funcionalidad que ya está funcionando.

No solo eso, sino que hay otro problema. Digamos que ahora necesitamos admitir además los tipos de archivos `sqlite` y
`parquet`. Dos desarrolladores intervendrán y comenzarán a trabajar en la misma clase enorme. Es muy probable que surjan
conflictos de fusión, lo que no solo es irritante para cualquier desarrollador, sino que también requiere mucho tiempo
para resolverlos. Lo más importante es que, incluso después de la resolución del conflicto, habría una menor confianza
en términos de que la característica funcione en su conjunto.

### La solución

Aquí es donde el patrón de diseño de estrategia entra en nuestro rescate. Moveremos todas las implementaciones del
analizador de archivos a clases separadas llamadas estrategias. En la clase actual, buscaremos dinámicamente la
implementación apropiada según el tipo de archivo y ejecutaremos la estrategia.

A continuación, se muestra un diagrama UML para proporcionar una descripción general de alto nivel del patrón de diseño
que estamos a punto de implementar.

Ahora, profundicemos en el código.

Creemos una interfaz para nuestro Analizador de Archivos (File Parser).

````java
public interface FileParser {
    void parse(MultipartFile file);
}
````

Ahora que hemos creado una interfaz, vamos a crear diferentes implementaciones para diferentes tipos de archivos, es
decir, estrategias.

````java

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
````

````java

@Slf4j
@Component("xml")
public class XmlFileParser implements FileParser {
    @Override
    public void parse(MultipartFile file) {
        log.debug(file.getName());
        log.debug(file.getOriginalFilename());
        log.debug(file.getContentType());
    }
}
````

````java

@Slf4j
@Component("csv")
public class CsvFileParser implements FileParser {
    @Override
    public void parse(MultipartFile file) {
        log.debug(file.getName());
        log.debug(file.getOriginalFilename());
        log.debug(file.getContentType());
    }
}
````

Tenga en cuenta que hemos dado nombres personalizados para los beans anteriores que nos ayudarán a inyectar estos
tres beans en nuestra clase requerida.

Ahora necesitamos encontrar una manera de elegir una de las implementaciones anteriores en función del tipo de archivo
durante el tiempo de ejecución.

Vamos a crear una clase `FileParserFactory`. Esta clase es responsable de decidir qué implementación elegir dado un tipo
de archivo. Aprovecharemos la increíble función de `inyección de dependencias` de `spring boot` para obtener la
estrategia adecuada durante el tiempo de ejecución. **(Consulte los comentarios en el bloque de código a continuación
para obtener más detalles).**

````java

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
````

Vamos a crear nuestra clase de contexto, que según el patrón de diseño `Strategy`, la clase `Contexto` mantiene
una referencia a una de las estrategias concretas y se comunica con este objeto únicamente a través de la interfaz
estrategia.

````java

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
````

Ahora, vamos a hacer cambios en nuestro `FileParserService`, quien será el cliente según el patrón `Strategy`. Usaremos
nuestro `FileParserFactory` para obtener la estrategia `FileParser` apropiada en función del `fileType`. Luego, usando
el `FileParserContext` le enviaremos la estrategia seleccionada y el archivo a procesar.

````java

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
````

Eso es todo, la implementación del `Patrón Strategy` se ha realizado correctamente, ahora solo necesitamos crear
nuestra clase de controlador para poder interactuar con la funcionalidad implementada.

````java

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
````

### Probando implementación de patrón Strategy

Procesando archivo `xml`:

````bash
$ curl -v -X POST -H "Content-Type: multipart/form-data" -F "file=@C:\Users\USUARIO\Downloads\reporte-de-empleados.xml" http://localhost:8080/api/v1/strategy/files/xml | jq
>
< HTTP/1.1 204
<
````

Información del archivo `xml` en la consola del IDE:

````bash
d.m.a.b.strategy.strategy.XmlFileParser  : file
d.m.a.b.strategy.strategy.XmlFileParser  : reporte-de-empleados.xml
d.m.a.b.strategy.strategy.XmlFileParser  : application/xml
````

Procesando archivo `json`:

````bash
$ curl -v -X POST -H "Content-Type: multipart/form-data" -F "file=@C:\Users\USUARIO\Downloads\reporte de personal.json" http://localhost:8080/api/v1/strategy/files/json | jq
>
< HTTP/1.1 204
<
````

Información del archivo `json` en la consola del IDE:

````bash
d.m.a.b.s.strategy.JsonFileParser        : file
d.m.a.b.s.strategy.JsonFileParser        : reporte de personal.json
d.m.a.b.s.strategy.JsonFileParser        : application/octet-stream
````

Lo mismo ocurriría con el archivo `csv`.