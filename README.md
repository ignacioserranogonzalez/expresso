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

#### Versiones de las herramientas utilizadas
- JDK: 23.0.2
- Gradle: 9.0.0
- Picocli: 4.7.6 (para la implementación del CLI)
- jpackage (utilizado para generar un paquete ejecutable opcional)

#### Referencias usadas
- Picocli: https://picocli.info/ (versión 4.7.6 para manejo de comandos CLI)
- jpackage: Parte del jdk 23 y usado para empaquetado nativo
- Documentación Gradle: https://docs.gradle.org/9.0.0/userguide/building_java_projects.html
- Clase ProcessBuilder: https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html

#### Notas Adicionales
- El proyecto se desarrolló usando Gradle como sistema de build.
- La carpeta `resources/template` contiene el archivo `HelloWorld.java` requerido, ubicado en la raíz del proyecto.
- Consultar el `build.gradle` para detalles de dependencias y configuración.

---

# Manual de Uso
## Pasos para Compilación/Ejecución

Nota: en caso de utilizar Graddle Wrapper se utiliza el comando `gradlew` en lugar de `gradle`

1. Ubicado en la raiz del proyecto, abrir una instancia de Windows cmd y contruir el proyecto con:
```bash
gradle clean build
```

2. Para generar el ejecutable y comenzar a usar expressor como programa de linea de comandos, ejecutar:
```bash
gradle jpackageImage
```
Se generara un archivo ejecutable (.exe) en la siguiente ruta: `\app\build\jpackage\expressor`

3. Ubicarse en la ruta de generacion del ejecuatable de expressor (`\app\build\jpackage\expressor\expressor.exe`) y abrir una instancia de cmd

## Comandos soportados por expressor

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

Las rutas del `.expresso` que se usen como input en los comando que contengan espacios en blanco debe ser encerradas entre comillas ("")
Por ejemplo: `expressor run "C:/UNA/CICLO II 2025/PARADIGMAS/proyecto/HelloWorld.expresso"`

**Opciones Comunes**

`--out`: define una ruta de carpeta en la que se guarda la salida en .java, si esa carpeta no existiera la crea en el directorio desde donde se esta ejecutando (ej. --out outputFolder)
   
`--verbose`: permite observar los pasos que se estan realizando al momento de la ejecución de un comando.

**NOTA IMPORTANTE**: Este proyecto utiliza la ruta relativa `expresso/resources/template` para acceder al archivo `HelloWorld.java` durante la (fingida) transpilación, utilizando una propiedad (`PROJECT_ROOT`) definida al empaquetar con jpackage ya que así lo exigen las instrucciones el Sprint 1 inicial. Sin embargo, en un entorno real, se recomienda utilizar el directorio `resources/` generado automáticamente por Gradle, accediendo al template a través del `classpath` en `TranspileCommand`, o bien una carpeta `resources/template` en el mismo directorio del ejecutable, evitando el uso de rutas relativas complejas.

## Prompts de IA (Íntegros)

Modelos de inteligencia artificial consultados: Grok, DeepSeek, Gemini, ChatGPT

- como creo un proyecto en gradle de cero en cmd ?

- quiero crear una aplicacion de cmd en java (donde se le pueden pasar comandos personalizados) usando picocli. como puedo hacer
eso en java ? por el momento ocupo que el comando base de ejecucion tenga el nombre "expressor" y tenga un comando llamado "transpile" que
lee un archivo HelloWorld.expresso (lenguaje inventado) y lo convierta a un archivo HelloWorld.java copiando sus contenidos

- segun lo pedido en el anexo del sprint 1, requiero iniciar con una base para el comando de transpile (es fingido por el momento)

- q es gradlew ? puedo usar gradle normal para hacer el run ?

- en realidad la estructua de mi proyecto es java/org/example/App.java es ees el main

- mejora este codigo para sea dry y siga los principios de knuth

- segun los estilos requeridos, como puedo mejorar mi codigo ? primero q nada, se pide dry y knuth. yo acabo e implementar un commonoptions para que el --verbose sea un Mixin en cada comando y no se repita. iniciemos con transpile (como puedo mejorarlo para que cumpla con todo lo que se pide ?):

- esta es la manera estandar de hacer un ejecutable con jpackage ?

- elshadow jar es parte de jpackage ? no se puede hcae rsolo con jpackage solamente ?

-  se requier q eusemos la forma estandar con jpackage, en vez de usar otros metodos (no se que otros habran) mas faciles, para sufrir. cual uso entonces ?

-  eso es todo ? no es muy facil ?

-  primer error

-  me funciono con este. encontre el expressor.exe, pero no hay carpeta bin ahi. intente ejecutar un cmd enesa carptea, y cuando hago el comando expressor transpile --verbose --out output HelloWorld.expresso, no sale nada

-  aunque yo ejecute el cmd en donde esta el .exe (tengo jdk23), no sale nada como salida, pero parece que si reconoce el comando al menos

-  FUNCIONO. ahora hay problemas con las rutas que especifico en mi codigo:

-  Lee de disco de una carpeta resources/template (relativo a raíz del proyecto) un archivo HelloWorld.java las instrucciones dicen esto. significa que el .exe debe leer ese mismo resources ? (muy por fuera del build/expressor.expressor.exe)?

-  siento que no bien hacer eso. muy probablemnete nos rebajen puntos por estilo

-  hay alguna forma de leer la raiz dl proyecto sin tener q hacer esto ?

-  es posible q al empaquetar con jpackage automaticamene se haga una env var y no se tenga q ejecutar en el mismo dir del exe ?

-  --java-options "-DPROJECT_ROOT=C:/ruta/a/expresso"
esto se puede definir como ../../../../ ?

- el spec pide: Lee de disco de una carpeta resources/template (relativo a raíz del proyecto). mi caprtea template esta en app/resources/template (y no en los resources que siempre incluye el proyecto al crearse). estoy mal ? debo cambiarlo ?

- el unico problema que hay son las rutas. las instrucciones dicen esto. "Lee de disco de una carpeta resources/template (relativo a raíz del proyecto) un archivo HelloWorld.java" lo que pasa es que cuando se genera el .exe queda en otra carpeta especificamnete expresso\app\build\jpackage\expressor\expressor.exe 
como las intrucciones dicen que hay q leer de la carpeta resources/template relativo al proyecto (es decir, expresso/resources/template), entonces habria que poner la ruta para que el .exe encuentre ese carpeta, volviendo como 5 niveles para llegar hasta la raiz, y de ahi leer el resources (y se ve muy mal)
la otra manera que pienso copiar el resources al directorio del exe, de manera que lo lea directamente desde ahi. el problema con eso es que no cumple con lo que dice el de "leer de la raiz del proyecto" estaria en expresso\app\build\jpackage\expressor\resources\template entonces no se q hacer. una opcion compromete buenas practicas en el codigo, y la otra opcion no sigue las instrucciones

-  Todo fuente (incl README): comentario identifica proyecto, curso, autores, código grupo. dame un tempalte de comentario para poer en todo fuente

- Siguiendo como referencia la estructura del comando `transpile`, explícame de qué manera puedo implementar un comando **build** usando la librería **picocli** y **javax.tools.JavaCompiler**.  
   Es decir, explícame cómo los elementos de estas librerías pueden ayudarme a construir el comando `build`, el cual debe realizar el proceso que ya hace `transpile` y además, ser capaz de pasar un archivo `.java` a uno `.class`. el comando debe ejecutarse con:
```bash
expressor build HelloWorld.expresso
expressor build --verbose --out output HelloWorld.expresso
```
- Tal vez no me especifiqué bien en la manera que quiero que trabajen los argumentos `--out` debe permitir establecer el directorio en el cual se guarda el `.class`. Si no se establece, el `.class` debe guardarse en la misma ruta que el archivo `.expresso`. Mientras que `--verbose` debe mostrar comentarios sobre cómo avanza el proceso de **transpilado**, **compilado** y/o **ejecución** en el caso del comando `run`.

- Tengo un problema con un **NullPointerException** al no poner el argumento `--out`.  
Es decir, si no defino el `outputDir`, este se vuelve `null` y me genera el error.  
¿Cómo puedo asignar correctamente la ruta por defecto (donde esté el `.expresso`) si el `--out` no se define?

- ¿Qué deberíamos agregar al **README** del proyecto?
  
- Vamos a hacer un proyecto Java dividido en sprint llamado **“Expresso”**, que es un mini lenguaje funcional que se transpila a Java.  
   Vamos a utilizar **Java 23.0.2**, **picocli** y **Gradle**.  
   Te voy a pasar una serie de documentos para que los analices y entiendas mejor el contexto del Sprint1.  
   *(Nota: le pasé los tres documentos que hablaban sobre el proyecto).*

- Los compañeros ya avanzaron con el proyecto, analiza el código adjunto y dime en qué puedo intervenir.  
   *(Aquí la IA me dijo varias cosas como que faltaba la clase `RunCommand`, el README, cumplimiento de DRY, etc.)*

- Voy a empezar con la implementación del subcomando **run** en una nueva rama llamada `comando-run`.  
   Es importante tener en cuenta esta asignación que me dio el coordinador:  
   *"Este mock equivale a la secuencia de los dos anteriores más la ejecución de la clase HelloWorld. Importante: usar el mismo proceso de ejecución del CLI para la ejecución del `.expresso`/`.java`".*

- Antes de agregar esto a mi proyecto, me gustaría entender paso a paso qué hace la parte de ejecución del método `run()`.

- ¿Qué es **ProcessBuilder** y para qué sirve?
