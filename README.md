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
```

## Prompt Grok(AI)
-	Vamos a hacer un proyecto Java dividido en sprint llamado “Expresso” que es un mini lenguaje funcional que se transpila a Java. Vamos a utilizar Java 23.0.2, picocli y gradle. Te voy a pasar una serie de documentos para que los analices y entiendas mejor el contexto del Sprint1. (Nota: le pase los tres documentos que hablaban sobre el proyecto).
-	Los compañeros ya avanzaron con el proyecto, analiza el código adjunto y dime en que puedo intervenir (Aquí la IA me dijo varias cosas como que faltada la clase RunCommand, el READMI, cumplimiento de DRY, etc )
-	Voy a empezar con la implementación del subcomando run en una nueva rama llamada comando-run. Es importante tener en cuenta esta asignación que me dio el coordinador: "Este mock equivale a la secuencia de los dos anteriores más la ejecución de la clase Helloworld. Importante: usar el mismo proceso de ejecución del cli para la ejecución el .expresso/.java"
-	Antes de agregar esto a mi proyecto, me gustaria entender paso a paso que hace esta parte de ejecución del método run()
-	Que es ProcessBuilder y para que sirve
-	Que deberíamos agregar al README del proyecto
