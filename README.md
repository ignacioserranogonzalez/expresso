# Expresso - Sprint Inicial
Curso: EIF400-II-2025, Grupo: 02-1PM  
Universidad Nacional de Costa Rica, Escuela de Informática  
Paradigmas de Programación

## Autores
- Kendall Miso Chinchilla Araya - 119310542  
- Ignacio Serrano Gonzalez - 402600631  
- Minor Brenes Aguilar - 116730106  
- Pablo Chavarria Alvarez - 117810573  

## Descripción
CLI "expressor" para el minilenguaje Expresso, implementado en Java 23+. Simula la transpilación, compilación y ejecución de archivos `.expresso` a Java, generando archivos `.java` y `.class` en el directorio especificado. Usa Picocli para manejar argumentos, Gradle para la construcción, y `jpackage` para generar una imagen ejecutable en consola pura (Windows/cmd). El diseño sigue principios de OOP (clases por subcomando, encapsulación), modularidad (paquete `org.example.cli`), y DRY (métodos comunes `transpileCommon`, `buildCommon`) con manejo robusto de errores, alineado con el principio de Knuth para simplicidad.


## Compilación
Ejecute los siguientes comandos desde un terminal Windows (cmd) sin IDE:
```bash
./gradlew build
./gradlew jpackageImage

