# Expresso - Sprint Medio
EIF400-II-2025 Paradigmas de Programación, Grupo 02-1PM  
Universidad Nacional de Costa Rica, Escuela de Informática

## Autores
- Kendall Miso Chinchilla Araya - 119310542  
- Ignacio Serrano Gonzalez - 402600631  
- Minor Brenes Aguilar - 116730106  
- Pablo Chavarria Alvarez - 117810573  

## Descripción
CLI "expressor" para el minilenguaje Expresso, implementado en Java 23+. Simula la transpilación, compilación y ejecución de archivos `.expresso` a Java, generando archivos `.java` y `.class` en el directorio especificado. Usa Picocli para manejar argumentos, Gradle para la construcción, y `jpackage` para generar una imagen ejecutable en consola pura (Windows/cmd). El diseño sigue principios de OOP (clases por subcomando, encapsulación), modularidad (paquete `una.paradigmas.cli`), y estilo DRY con manejo de errores alinead con los principio de Knuth.

#### Versiones de las herramientas utilizadas
- JDK: 23.0.2
- Gradle: 9.0.0
- Picocli: 4.7.6 (para la implementación del CLI)

# Guía de Instalación y Configuración del Entorno

## Requisitos Previos

### 1. Instalación de JDK 23
**Descarga:**
- Visite [Oracle JDK 23](https://www.oracle.com/java/technologies/downloads/#java23) o use [OpenJDK 23](https://jdk.java.net/23/)
- Seleccione la versión adecuada para su sistema operativo Windows

**Instalación:**
- Ejecute el instalador descargado
- Siga las instrucciones del asistente de instalación
- Asegúrese de instalar en una ruta sin espacios (recomendado: `C:\Java\jdk-23.0.2`)

**Configuración de variables de entorno:**

1. Abra una consola de Windows CMD como administrador (Inicio > escribe "cmd" > clic derecho > "Ejecutar como administrador").

2. Ejecute los siguientes comandos:
```cmd
setx JAVA_HOME "C:\Java\jdk-23.0.2"
setx PATH "%PATH%;%JAVA_HOME%\bin"
```
3. Cierre y vuelva a abrir la consola para aplicar los cambios.

**Verificación:**
```cmd
java -version
```
Debería mostrar: `java version "23.0.2" 2024-07-16`

### 2. Instalación de Gradle 9.0.0
**Descarga:**
- Descargue desde [Gradle Releases](https://gradle.org/releases/)
- Versión: gradle-9.0.0-bin.zip

**Instalación:**
- Extraiga el archivo ZIP en `C:\Gradle\gradle-9.0.0`

**Configuración de variables de entorno:**

1. Abra CMD como administrador.

2. Ejecute:

```cmd
setx GRADLE_HOME "C:\Gradle\gradle-9.0.0"
setx PATH "%PATH%;%GRADLE_HOME%\bin"
```
3. Cierre y reabra la consola.

**Verificación:**
```cmd
gradle -version
```
Debería mostrar:

```cmd
------------------------------------------------------------
Gradle 9.0.0
------------------------------------------------------------

Build time:    2025-07-31 16:35:12 UTC
Revision:      328772c6bae126949610a8beb59cb227ee580241

Kotlin:        2.2.0
Groovy:        4.0.27
Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
Launcher JVM:  23.0.2 (Oracle Corporation 23.0.2+7-58)
Daemon JVM:    C:\Program Files\Java\jdk-23 (no JDK specified, using current Java home)
```

### Gradle Wrapper

Dentro del proyecto se incluye Gradle Wrapper (version de Gradle solo para el entorno del proyecto), el cual puede usarse para contruir el proyecto sin necesidad de una instalación global de Gradle.

**Verificación de Gradle Wrapper**
Ubiquese en la raiz del proyecto (`...\expresso`) y ejecute:
```bash
gradlew -v
```
Debería mostrar: `Gradle 9.0.0`

Si el comando falla, asegúrese de tener permisos de ejecución en la carpeta del proyecto. Puede verificar permisos ejecutando:

```cmd 
dir gradlew
```

### 3. Clonar el proyecto
**Clonar/descargar el proyecto en alguna ubicacion:**
```cmd
git clone <url-del-repositorio>
```
Url del repositorio: https://github.com/ignacioserranogonzalez/expresso.git

O bien si no quiere clonarlo, descargue el zip en la misma Url, teniendo el zip descargado puede descomprimirlo en la carpeta que guste.

## Solución de Problemas Comunes

### Si Java no es reconocido:
- Verifique que JAVA_HOME apunte al directorio correcto
- Reinicie la consola después de configurar variables

### Si Gradle no es reconocido:
- Use el wrapper del proyecto: `./gradlew` en lugar de `gradle`

### Permisos insuficientes:
- Ejecute la consola como Administrador para instalaciones globales
---
#### Referencias usadas
- Picocli: https://picocli.info/ (versión 4.7.6 para manejo de comandos CLI)
- jpackage: Parte del jdk 23 y usado para empaquetado nativo
- Documentación Gradle: https://docs.gradle.org/9.0.0/userguide/building_java_projects.html
- Clase ProcessBuilder: https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html

#### Notas Adicionales
- La carpeta `resources/template` contiene el archivo `HelloWorld.java` requerido, ubicado en la raíz del proyecto.
- Consultar el `build.gradle` para detalles de dependencias y configuración.



# Manual de Uso

## Pasos para Compilación/Ejecución

1. Abrir una instancia de consola de Windows
1. Ubicarse en la raíz del proyecto (`...\expresso`)
3. Construir el proyecto. Ejecute:

Importante: Si no instalo gradle de manera global en su computador como se le indicó que podría realizarlo en la guia de instalación puede utilizar gradlew (Gradle Wrapper) 
   
Gradle
```bash
gradle clean build
```
o Gradle Wrapper
```bash
gradlew clean build
```

2. Generar un ejecutable y comenzar a usar expressor como programa de linea de comandos. Ejecute:
   
Gradle
```bash
gradle jpackageImage
```

o Gradle Wrapper
```bash
gradlew jpackageImage
```
salida esperada:
```bash
Reusing configuration cache.

> Task :app:jpackageImage
[19:23:18.652] Creating app package: expressor in C:\UNA\CICLO II 2025\PARADIGMAS\proyecto\REVISION\EIF400-II-2025_Expresso_Initial_02-1PM_Ignacio_Serrano\expresso\app\build\jpackage
[19:23:30.135] Command [PID: -1]:
    jlink --output build\jpackage\expressor\runtime --module-path C:\\Program Files\\Java\\jdk-23\\jmods --add-modules jdk.management.jfr,java.rmi,jdk.jdi,jdk.charsets,jdk.xml.dom,java.xml,java.datatransfer,jdk.jstatd,jdk.httpserver,java.desktop,java.security.sasl,jdk.zipfs,java.base,jdk.javadoc,jdk.management.agent,jdk.jshell,jdk.editpad,jdk.jsobject,java.sql.rowset,jdk.sctp,jdk.unsupported,java.smartcardio,jdk.jlink,java.security.jgss,jdk.nio.mapmode,java.compiler,jdk.dynalink,jdk.unsupported.desktop,jdk.accessibility,jdk.security.jgss,jdk.incubator.vector,java.sql,java.transaction.xa,java.xml.crypto,java.logging,jdk.jfr,jdk.internal.md,jdk.crypto.cryptoki,jdk.net,java.naming,jdk.internal.ed,java.prefs,java.net.http,jdk.compiler,jdk.internal.opt,jdk.naming.rmi,jdk.jconsole,jdk.attach,jdk.crypto.mscapi,jdk.internal.le,java.management,jdk.jdwp.agent,jdk.internal.jvmstat,java.instrument,jdk.management,jdk.security.auth,java.scripting,jdk.jdeps,jdk.jartool,java.management.rmi,jdk.jpackage,jdk.naming.dns,jdk.localedata --strip-native-commands --strip-debug --no-man-pages --no-header-files
[19:23:30.135] Output:
    WARNING: Using incubator modules: jdk.incubator.vector

[19:23:30.135] Returned: 0

[19:23:30.139] Using default package resource JavaApp.ico [icon] (add expressor.ico to the resource-dir to customize).
[19:23:30.162] Warning: Windows Defender may prevent jpackage from functioning. If there is an issue, it can be addressed by either disabling realtime monitoring, or adding an exclusion for the directory "C:\Users\takoy\AppData\Local\Temp\jdk.jpackage3006707898795195769".
[19:23:30.410] Using default package resource WinLauncher.template [Template for creating executable properties file] (add expressor.properties to the resource-dir to customize).
[19:23:30.574] Succeeded in building Windows Application Image package

BUILD SUCCESSFUL in 16s
4 actionable tasks: 4 executed
Configuration cache entry reused.
```

Se generará un archivo ejecutable (.exe) en la siguiente ruta: `.\expresso\app\build\jpackage\expressor\expressor.exe`

3. Ubicarse en la ruta de generacion del ejecutable de expressor. Ejecute:
_(Debe estar ubicado en la raiz del proyecto (`...\expresso`) antes de ejecutar el siguiente comando)_
```bash
cd app\build\jpackage\expressor
```
y
```bash
dir
```
Se mostrará una lista de directorios y archivos en esa ubicación. Debe estar presente el ejecutable **expressor.exe**

4. Opcionalmete se puede ejecutar el comando:
  
Gradle
```bash
gradle clean test
```
o Gradle Wrapper
```bash
gradlew clean test
```
para ejecutar todos los test del proyecto. Aunque estos se van a ejecutar automaticamente con el `gradle clean build` o `gradlew clean build` que se ejecuta al inicio para construir el proyecto.


5. Listo. Ya puede comenzar a utilizar expressor como programa de linea de comandos **en la misma ubicación de expressor.exe**

Nota: expressor es un programa de CLI que se ejecuta a través de comandos en la misma ubicación en donde fue generado el ejecutable. Estar en una ruta diferente o cambiar la ubicación del ejecutable u otros archivos del proyecto podría resultar en mal funcionamiento o imposibilidad de utilizar expressor.  

## Comandos soportados por expressor (Solo para futura referencia. Los Casos de Prueba de Sprint 2 en la siguiente sección)

Las rutas del archivo `.expresso` que se usen como input en los comando que contengan espacios en blanco deben ser encerradas entre comillas ("")
Por ejemplo: `expressor run "C:/UNA/CICLO II 2025/PARADIGMAS/HelloWorld.expresso"`

(Se debe reemplazar {ruta__al_.expresso} por la ruta real del archivo)

1. **transpile**: Lee de disco un archivo HelloWorld.expresso que no está vacío, salva textualmente HelloWorld.java en la carpeta seleccionado, si no se selecciona alguna en particular, salva en la misma carpeta donde se ejecuta el comando expressor. Este se puede ejecutar como el usuario desee, con o sin argumentos opcionales.

Ejemplos de uso:

 ```bash
expressor transpile --verbose --out output {ruta__al_.expresso}
```
 ```bash
expressor transpile {ruta__al_.expresso}
```

2. **build**: Realiza el proceso de transpile de ser necesario y además permite compilar el archivo .java generado en la transpilación y generar el .class de este. Se puede ejecutar como el usuario desee, con o sin argumentos opcionales.

Ejemplos de uso:

 ```bash
expressor build --verbose --out output {ruta__al_.expresso}
```
 ```bash
expressor build {ruta__al_.expresso}
```

3. **run**: Realiza el proceso de build si este no se realizo anteriormente y además permite ejecutar el archivo .class generado en la compilación (build) y mostrar el contenido de este. Se puede ejecutar como el usuario desee, con o sin argumentos opcionales.

Ejemplos de uso:

 ```bash
expressor run --verbose --out output {ruta__al_.expresso}
```
 ```bash
expressor run {ruta__al_.expresso}
```

**Opciones Comunes**

`--out`: define una ruta de carpeta en la que se guarda la salida en .java, si esa carpeta no existiera la crea en el directorio desde donde se esta ejecutando (ej. --out outputFolder). Es opcional (si no se especifica, se guarda todo en la misma ubicación desde donde se ejecuta el comando)
   
`--verbose`: permite observar los pasos que se estan realizando al momento de la ejecución de un comando.

# Casos de Prueba del Sprint 2 (Earth)

El proyecto incluye los tres archivos de prueba Earth HelloWorld0.expresso, HelloWorld1.expresso y HelloWorld2.expresso

**Nota importante:** Todos los archivos .expresso que no terminen con una línea en blanco al final obtendrán el mensaje: missing NEWLINE at 'EOF', sin embargo no afecta a la ejecución de los casos de prueba.

### Ejecutar los tests manualmente

(Es requisito haber ejecutado el comando `gradle clean build` y `gradle jpackageImage` (o sus alternativas con `gradlew`) en la raiz del proyecto (`...\expresso`) con anterioridad)

Ubicarse en la ruta del ejecutable (app\build\jpackage\expressor):
_(Debe estar ubicado en la raiz del proyecto (`...\expresso`) antes de ejecutar el siguiente comando)_
```bash
cd app\build\jpackage\expressor
```



## Test 1: HelloWorld0.expresso

```bash
expressor transpile --verbose ..\..\..\..\testcloria\HelloWorld0.expresso
```
```bash
expressor build --verbose ..\..\..\..\testcloria\HelloWorld0.expresso
```
```bash
expressor run --verbose ..\..\..\..\testcloria\HelloWorld0.expresso
```
## Test 2: HelloWorld1.expresso
```bash
expressor transpile --verbose ..\..\..\..\testcloria\HelloWorld1.expresso
```
```bash
expressor build --verbose ..\..\..\..\testcloria\HelloWorld1.expresso
```
```bash
expressor run --verbose ..\..\..\..\testcloria\HelloWorld1.expresso
```
## Test 3: HelloWorld2.expresso
```bash
expressor transpile --verbose ..\..\..\..\testcloria\HelloWorld2.expresso
```
```bash
expressor build --verbose ..\..\..\..\testcloria\HelloWorld2.expresso
```
```bash
expressor run --verbose ..\..\..\..\testcloria\HelloWorld2.expresso
```

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

- Tengo esta gramatica para trabajar con ANTLR4, actualmente esta para un proyecto con JavaScript, sin embargo la quiero migrar para un proyecto en java, y adicional la gramática debe ser extendida para manejar let y lambas , manteniendo el estilo de programación DRY y Knuth

grammar Expr;


// Punto de entrada: cero o más sentencias terminadas por NEWLINE
prog : stat* EOF ;


// Una sentencia puede ser una expresión -> imprimir, o línea vacía
stat : expr NEWLINE         # printExpr
       | NEWLINE            # blank
;


// Expresiones con precedencia y unario '-'
expr :    '-' expr 					# unaryMinus
		| expr op=('*'|'/') expr 	# MulDiv
		| expr op=('+'|'-') expr 	# AddSub
		| INT # int
		| '(' expr ')' 				# parens
;


// LEXER
INT : [0-9]+ ;
NEWLINE: ('\r'? '\n') ;
WS : [ \t]+ -> skip ;

- Estas son las tareas asignadas por el coordinador

- que podría ir haciendo o investigando para realizar las tareas que me corresponden. Tengo que esperar que los otros compañeros avancen con el proyecto?

- le hice unos pequeños cambios al traspile. ANALIZA si es mejor usar var o el tipo de dato específicamente

- quiero poder ejecutar el comando de esta manera "E: expressor transpile --out generated "C:\Users\minor\Documents\Estudio\Minor\CICLO II 25\Paradigmas\Proyecto1_Sprint1\expresso\app\examples\HelloWorld.expresso"", pero me salta este error line 7:18 missing NEWLINE at '<EOF>'

- Asegurarse de que run ejecute el .class resultante.

- ayúdame disminuir el código repetido

- la versión final quedo así, revisa que cumpla con el principio DRY

- donde es más conveniente que colocar el try de public void run()

- este if se puede quitar

- verifica que las tareas 7 y 8 estén completas y cumplen con el SPEC

- hice unos cambios revisalos. solo los cambios!

- verifica que la tarea 9: Maneje reglas específicas del spec

- la clase JavaCodeGenerator cambio bastante. EXPLICAME los cambios realizados por los compañeros

- Revisa la clase una por una y evalúa si cumplen con "Donde sea apropiado se debe usar FP-Java. Se espera estilo fluido, DRY, Knuth."
- Teniendo el modelo el ast construido, cual es la mejor manera de implementar un generador de codigo a partir de ese ast?
- Como funciona string builder, y porque es buena opcion para generar el codigo java a partir del AST construido.
- Que similitudes tiene el AST print visitor y la manera de recorrer el program que recibe mi javaCodeGenerator??
- Ya tengo mi javaCodeGeneratorConstruido, siguiendo tus recomendaciones, pero a la hora de construir el codigo a partir del program, nos imports no se estan mostrando, a que se debe?
- El javaCodeGenerator genera el codigo adecuadamente, pero los comentarios `(//)` solo aparecen dentro del main, no fuera de el, como hago para cambiar esto.
- La manera en las que se recorren los statements del program es muy ineficiente, en el aspecto que se hacen 2 recorridos uno par los comentarios fuera del main y otro para los internos, como puedo unificar ese recorrido?

- para q sirve el sourcesets ? esta siendo utilizado ?
- COMMENT: '//' ~[\r\n] -> skip ; Q SIGNIFICA ~ en antlr.
- el id es parte del lexer ? que cosas es parte del Lexer
- OP: ('*'|'/'); en este caso para q son los parentesis
- y si quiero incluir ++, --, y ** ?
- el orden en que se definen en la gramatica importa ? es decir, recae en mi la responsabilida de definir el orden, o antlr lo lee sin importar el orden que yo ponga ?
- de momento esta bien mi gramatica para leer este .expresso ? // HelloWorld.expresso let x = 666
- al poner el int y id como caso base, eso implica que mi expresos podria venir una linea que solo sea un x por ejemplo, o solo un 1 ?
- LET ID '=' expr deberia ser una sentencia o una expression ?
- que piensas de los cambios ? son correctos para // HelloWorld.expresso let x = 666 print(x) let y = 10 print(y) ?
- para hacee un javacodegenerator usando mi visitor, comi se podria hacer usando pattern matching en java ?
- peudes nombrar las expr para que no me de error ?
- los archivos fueron generados en esta ruta build/generated-src/antlr/main/AQUI es correcto esto ?????
- xq esta mal ?? (refiriéndose a visitLambda)
- ayuda (cuando el Lambda no funcionaba)
- q esta mal ? (sobre la gramática con comentarios)
- no me gusta eso de atom. no puedes dejar la misma estructura de mi gramatica pero cambiar las precedencias y asociatividad ?
- que significa que pow asocia a la derecha ?
- <assoc=right> es de antlr integrado ?
- eso que implica en mi codigo del visitor. debo cambiar algo ?
- me gustaria resumir el lambda en un solo visit
- lambdaExpr: (ID (',' ID)?)? LAMBDA expr # Lambda cuantos args acepta esta version ?
- modifica gramatica y: [código de visitLambda]
- termina el print (refiriéndose a AstPrintVisitor)
- parece q hay un problema con la asociatividad del pow
- pero el orden esta bien asi no ?? xq pasa eso entonces
- lambdaArgs: '(' ')' | ID | '(' ID (',' ID)? ')' debe estar limitado a 2 maximo
- se debe modificar para usar ? (sobre visitLambda con lambdaArgs)
- xq se imprime varias veces ?
- esta bien mi regla lambdaArgs para manejar esto ? quiero que solo haga un visit, entonces quiero manejarlo con una regla que obtenga esos casos
- para los comentarios opcionales, quiero que se visiten creando nuevos nodos de comentarios, no imprimirlos
- lo CORRECTO seria usar String o Objeto Comment ? no hablo de simpleza, me refiero a lo correcto
- es correcta esta salida ? para esto: [input de HelloWorld1]
- ademas, xq sale un // antes del comentario de bloque ? tiene que ver con mi gramatica ?
- tambien hay que ajustar para que el comentario de bloque // se agrege si es parte de los iniciales
- que le hace falta a mi gramatica para que lea calls de dos argumentos ?
- lo hice asi. dame el astbuilder y lo demas para incluirlo
- solo me falta modificar el javacodegenerator para el caso Call
- aqui esta lo demas que hice: [gramática con callArgs]
- todo esto es correcto ? (sobre la implementación de Call)
- ya n se ocupa un visitor extra como este ? (sobre visitCallExpr)
- salida correcta ??/ (sobre HelloWorld2)
- xq falla el AND.apply(OR, NOT); ??
- modifique la gramatica asi. dame las modificaiones para [nuevo visitCall]
- me esta imprimiendo un ; despues del comentario. cual es el problema en el javacodegenerator
- ocupo que la salida sea: print(f.apply(x) + f.apply(y)); // expected 445010
- eso es un error de java ? no se supone que las variables de los lambdas son independientes ?
