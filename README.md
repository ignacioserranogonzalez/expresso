# Expresso - Sprint Inicial
EIF400-II-2025 Paradigmas de Programación, Grupo 02-1PM  
Universidad Nacional de Costa Rica, Escuela de Informática

## Autores
- Kendall Miso Chinchilla Araya - 119310542  
- Ignacio Serrano Gonzalez - 402600631  
- Minor Brenes Aguilar - 116730106  
- Pablo Chavarria Alvarez - 117810573  

## Descripción
CLI "expressor" para el minilenguaje Expresso, implementado en Java 23+. Simula la transpilación, compilación y ejecución de archivos `.expresso` a Java, generando archivos `.java` y `.class` en el directorio especificado. Usa Picocli para manejar argumentos, Gradle para la construcción, y `jpackage` para generar una imagen ejecutable en consola pura (Windows/cmd). El diseño sigue principios de OOP (clases por subcomando, encapsulación), modularidad (paquete `org.example.cli`), y estilo DRY con manejo de errores alinead con los principio de Knuth.

## Versiones de las herramientas utilizadas
1. JDK 23.0.2
2. Gradle 9.0.0
   
## Compilación
Ejecute los siguientes comandos en la raiz del proyecto (expresso/) desde un terminal Windows (cmd) para poder utilizar expressor:
```bash
.\gradle build
.\gradle jpackageImage
```
## Manual de uso de los comandos soportados por expressor
1. **transpile**: Lee de disco el archivo HelloWorld.expresso que no está vacío, salva textualmente HelloWorld.java en la carpeta seleccionado, si no se selecciona alguna en particular, salva en la misma carpeta donde se ejecuta el comando expressor. Este se puede ejecutar como el usuario desee, con o sin argumentos opcionales.
   
 ```bash
expressor transpile --verbose --out output HelloWorld.expresso
expressor transpile HelloWorld.expresso
```  
2. **build**: Realiza el proceso de transpile de ser necesario y además permite compilar el archivo .java generado en la transpilación y generar el .class de este. Se puede ejecutar como el usuario desee, con o sin argumentos opcionales.

 ```bash
expressor build --verbose --out output HelloWorld.expresso
expressor build HelloWorld.expresso
```
3. **run**: Realiza el proceso de build si este no se realizo anteriormente y además permite ejecutar el archivo .class generado en la compilación (build) y mostrar el contenido de este. Se puede ejecutar como el usuario desee, con o sin argumentos opcionales.

 ```bash
expressor run --verbose --out output HelloWorld.expresso
expressor run HelloWorld.expresso
```

**Opciones Comunes**

`--out`: para definir una ruta de carpeta en la que se guarda la salida en .java, si esa carpeta no existiera la crea en el directorio desde donde se esta ejecutando
   
`--verbose`: permite observar los pasos que se estan realizando al momento de la ejecución de un comando.

**NOTA IMPORTANTE**: Este proyecto utiliza la ruta relativa `expresso/resources/template` para acceder al archivo `HelloWorld.java` durante la (fingida) transpilación, utilizando una propiedad (`PROJECT_ROOT`) definida al empaquetar con jpackage ya que así lo exigen las instrucciones el Sprint 1 inicial. Sin embargo, en un entorno real, se recomienda utilizar el directorio `resources/` generado automáticamente por Gradle, accediendo al template a través del `classpath` en `TranspileCommand`, o bien una carpeta `resources/template` en el mismo directorio del ejecutable, evitando el uso de rutas relativas complejas.


## Prompts de IA (Íntegros)

Modelos de inteligencia artificial consultados: Grok, DeepSeek, Gemini, ChatGPT

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





