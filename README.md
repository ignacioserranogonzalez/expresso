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

## Versiones de las diferentes herramientas utilizadas
1. JDK 23.0.2
2. Gradle 9.0.0
   
## Compilación
Ejecute los siguientes comandos desde un terminal Windows (cmd) para poder utilizar expressor:
```bash
.\gradlew build
.\gradlew jpackageImage
```
## Manual de uso de los comandos soportados por expressor
1. *Transpile*: Lee de disco el archivo HelloWorld.expresso que no está vacío, salva textualmente HelloWorld.java en la carpeta seleccionado, si no se selecciona alguna en particular, salva en la misma carpeta donde se ejecuta el comando expressor. Este se puede ejecutar como el usuario desee, con o sin argumentos opcionales.
   
    -out: define la ruta de carpeta en la que se guarda el HelloWorld.java, si esa carpeta no existiera la crea.
   
    -verbose: nos permite observar los pasos que se estan realizando al momento de la ejecución del comando.
   
 ```bash
expressor transpile --verbose --out output HelloWorld.expresso
expressor transpile HelloWorld.expresso
```  
2. *Build*: Realiza el proceso de transpile de ser necesario y además permite compilar el archivo .java generado en la transpilación y generar el .class de este. Se puede ejecutar como el usuario desee, con o sin argumentos opcionales.
   
    -out: define la ruta de carpeta en la que se guarda el HelloWorld.java, si esa carpeta no existiera la crea.
   
    -verbose: nos permite observar los pasos que se estan realizando al momento de la ejecución del comando.

 ```bash
expressor build --verbose --out output HelloWorld.expresso
expressor build HelloWorld.expresso
```
3. *Run*: Realiza el proceso de build si este no se realizo anteriormente y además permite ejecutar el archivo .class generado en la compilación (build) y mostrar el contenido de este. Se puede ejecutar como el usuario desee, con o sin argumentos opcionales.
   
    -out: define la ruta de carpeta en la que se guarda el HelloWorld.java, si esa carpeta no existiera la crea.
   
    -verbose: nos permite observar los pasos que se estan realizando al momento de la ejecución del comando
 ```bash
expressor run --verbose --out output HelloWorld.expresso
expressor run HelloWorld.expresso
```
## Aspectos importantes del proyecto
Este proyecto utiliza la ruta relativa `expresso/resources/template` para acceder al archivo `HelloWorld.java` durante la transpilación, empleando una propiedad personalizada (`PROJECT_ROOT`) al empaquetar con `jpackage`, conforme a las instrucciones del Sprint Inicial. Esta elección asegura compatibilidad con los requisitos especificados. Sin embargo, en un entorno real, se recomienda utilizar el directorio `resources/` generado automáticamente por Gradle, accediendo al template a través del `classpath` en `TranspileCommand`. Esto evitaría el uso de rutas relativas complejas.


## Prompts de IA Íntegros

Es importante aclarar que los modelos de inteligencia artificial consultados fueron **Grok**, **DeepSeek**, **Gemini** y **ChatGPT**.

1. Vamos a hacer un proyecto Java dividido en sprint llamado **“Expresso”**, que es un mini lenguaje funcional que se transpila a Java.  
   Vamos a utilizar **Java 23.0.2**, **picocli** y **Gradle**.  
   Te voy a pasar una serie de documentos para que los analices y entiendas mejor el contexto del Sprint1.  
   *(Nota: le pasé los tres documentos que hablaban sobre el proyecto).*

2. Los compañeros ya avanzaron con el proyecto, analiza el código adjunto y dime en qué puedo intervenir.  
   *(Aquí la IA me dijo varias cosas como que faltaba la clase `RunCommand`, el README, cumplimiento de DRY, etc.)*

3. Voy a empezar con la implementación del subcomando **run** en una nueva rama llamada `comando-run`.  
   Es importante tener en cuenta esta asignación que me dio el coordinador:  
   *"Este mock equivale a la secuencia de los dos anteriores más la ejecución de la clase HelloWorld. Importante: usar el mismo proceso de ejecución del CLI para la ejecución del `.expresso`/`.java`".*

4. Antes de agregar esto a mi proyecto, me gustaría entender paso a paso qué hace la parte de ejecución del método `run()`.

5. ¿Qué es **ProcessBuilder** y para qué sirve?

6. ¿Qué deberíamos agregar al **README** del proyecto?

7. Siguiendo como referencia la estructura del comando `transpile`, explícame de qué manera puedo implementar un comando **build** usando la librería **picocli** y **javax.tools.JavaCompiler**.  
   Es decir, explícame cómo los elementos de estas librerías pueden ayudarme a construir el comando `build`, el cual debe realizar el proceso que ya hace `transpile` y además, ser capaz de pasar un archivo `.java` a uno `.class`. el comando debe ejecutarse con:
```bash
expressor build HelloWorld.expresso
expressor build --verbose --out output HelloWorld.expresso
```
8. Tal vez no me especifiqué bien en la manera que quiero que trabajen los argumentos `--out` debe permitir establecer el directorio en el cual se guarda el `.class`. Si no se establece, el `.class` debe guardarse en la misma ruta que el archivo `.expresso`. Mientras que `--verbose` debe mostrar comentarios sobre cómo avanza el proceso de **transpilado**, **compilado** y/o **ejecución** en el caso del comando `run`.

9. Tengo un problema con un **NullPointerException** al no poner el argumento `--out`.  
Es decir, si no defino el `outputDir`, este se vuelve `null` y me genera el error.  
¿Cómo puedo asignar correctamente la ruta por defecto (donde esté el `.expresso`) si el `--out` no se define?





