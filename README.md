# Expresso - Sprint Final
EIF400-II-2025 Paradigmas de Programación, Grupo 02-1PM  
Universidad Nacional de Costa Rica, Escuela de Informática

## Autores
- Kendall Miso Chinchilla Araya - 119310542  
- Ignacio Serrano Gonzalez - 402600631  
- Minor Brenes Aguilar - 116730106  
- Pablo Chavarria Alvarez - 117810573  

## Descripción
CLI "expressor" para el minilenguaje Expresso, implementado en Java 23+. Realiza la transpilación, compilación y ejecución de archivos `.expresso` a Java, generando archivos `.java` y `.class` en el directorio especificado. Soporta tipos primitivos `(int, float, boolean, string, any)`, tipos de usuario con `data`, `lambdas`, funciones recursivas con `fun`, pattern matching con `match`, casting con `:` y operadores `relacionales/booleanos`. Usa Picocli para manejar argumentos, Gradle para la construcción, y `jpackage` para generar una imagen ejecutable en consola pura (Windows/cmd). El diseño sigue principios de OOP (clases por subcomando, encapsulación), modularidad (paquete `una.paradigmas.cli`), y estilo DRY con manejo de errores alineado con los principio de Knuth.

#### Versiones de las herramientas utilizadas
- JDK: 23.0.2
- Gradle: 9.0.0
- Picocli: 4.7.6 (para la implementación del CLI)

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

1. Abrir una instancia de consola de Windows y ubicarse en la raíz del proyecto (`...\expresso`).
2. Construir el proyecto. _(El proyecto incluye Gradle Wrapper (gradlew), una versión empaquetada de Gradle para usar sin instalación global (opcional instalar Gradle))_. Ejecute:


Gradle Wrapper
```bash
gradlew clean build
```
Como alternativa, si Gradle está instalado globalmente, use `gradle clean build`. 

Resultado esperado: 
```bash
BUILD SUCCESSFUL in 16s
4 actionable tasks: 4 executed
Configuration cache entry reused.
```
y una ejecución automática de lo casos de prueba jupyter, saturn, y pluto.

3. Generar un ejecutable y comenzar a usar expressor como programa de linea de comandos. Ejecute:

Gradle Wrapper
```bash
gradlew jpackageImage
```
Resultado esperado:
```bash
BUILD SUCCESSFUL in 10s
6 actionable tasks: 2 executed, 4 up-to-date
Configuration cache entry stored.
```

Se generará un archivo ejecutable (.exe) en la siguiente ruta: `.\expresso\app\build\jpackage\expressor\expressor.exe`.

4. Ubicarse a través del siguiente comando en la ruta de generacion del ejecutable de expressor. _(Debe estar ubicado en la raiz del proyecto (`...\expresso`) antes de ejecutar el siguiente comando)_. Ejecute:

```bash
cd app\build\jpackage\expressor
```
y
```bash
dir
```
Se mostrará una lista de directorios y archivos en esa ubicación. Debe estar presente el ejecutable **expressor.exe**.

5. Listo. Ya puede comenzar a utilizar expressor como programa de linea de comandos **en la misma ubicación de expressor.exe**.

Nota: expressor es un programa de CLI que se ejecuta a través de comandos en la misma ubicación en donde fue generado el ejecutable. Estar en una ruta diferente o cambiar la ubicación del ejecutable u otros archivos del proyecto podría resultar en mal funcionamiento o imposibilidad de utilizar expressor.  

## Comandos soportados por expressor

Las rutas del archivo `.expresso` que se usen como input en los comando que contengan espacios en blanco deben ser encerradas entre comillas ("").
Por ejemplo: `expressor run "C:/UNA/CICLO II 2025/PARADIGMAS/HelloWorld.expresso"`.

(Se debe reemplazar {ruta__al_.expresso} por la ruta real del archivo).

1. **transpile**: Lee de disco un archivo HelloWorld.expresso que no está vacío, salva textualmente HelloWorld.java en la carpeta seleccionado, si no se selecciona alguna en particular, salva en la misma carpeta donde se ejecuta el comando expressor. Este se puede ejecutar como el usuario desee, con o sin argumentos opcionales.

Ejemplos de uso:

 ```bash
expressor transpile --out output {ruta__al_.expresso} --verbose
```
 ```bash
expressor transpile {ruta__al_.expresso}
```

2. **build**: Realiza el proceso de transpile de ser necesario y además permite compilar el archivo .java generado en la transpilación y generar el .class de este. Se puede ejecutar como el usuario desee, con o sin argumentos opcionales.

Ejemplos de uso:

 ```bash
expressor build --out output {ruta__al_.expresso} --verbose
```
 ```bash
expressor build {ruta__al_.expresso}
```

3. **run**: Realiza el proceso de build si este no se realizo anteriormente y además permite ejecutar el archivo .class generado en la compilación (build) y mostrar el contenido de este. Se puede ejecutar como el usuario desee, con o sin argumentos opcionales.

Ejemplos de uso:

 ```bash
expressor run --out output {ruta__al_.expresso} --verbose
```
 ```bash
expressor run {ruta__al_.expresso}
```

**Opciones Comunes**

`--out`: define una ruta de carpeta en la que se guarda la salida en .java, si esa carpeta no existiera la crea en el directorio desde donde se esta ejecutando (ej. --out outputFolder). Es opcional (si no se especifica, se guarda todo en la misma ubicación desde donde se ejecuta el comando).
   
`--verbose`: permite observar los pasos que se estan realizando al momento de la ejecución de un comando.

# Casos de Prueba del Sprint Final (Testcases)

El proyecto incluye los dos archivos de prueba `pluto.expresso` y `jupyter.expresso`.

**Nota importante:** Todos los archivos .expresso que no terminen con una línea en blanco al final obtendrán el mensaje: missing NEWLINE at 'EOF', sin embargo no afecta a la ejecución de los casos de prueba.

### Ejecutar los tests manualmente

(Es requisito haber ejecutado el comando `gradlew clean build` y `gradlew jpackageImage` en la raiz del proyecto (`...\expresso`) con anterioridad)

Ubicarse en la ruta del ejecutable (app\build\jpackage\expressor):
_(Debe estar ubicado en la raiz del proyecto (`...\expresso`) antes de ejecutar el siguiente comando)_
```bash
cd app\build\jpackage\expressor
```

## Test 1: pluto.expresso

```bash
expressor transpile --verbose ..\..\..\..\testcloria\pluto.expresso
```
```bash
expressor build --verbose ..\..\..\..\testcloria\pluto.expresso
```
```bash
expressor run --verbose ..\..\..\..\testcloria\pluto.expresso
```
## Test 2: jupyter.expresso
```bash
expressor transpile --verbose ..\..\..\..\testcloria\jupyter.expresso
```
```bash
expressor build --verbose ..\..\..\..\testcloria\jupyter.expresso
```
```bash
expressor run --verbose ..\..\..\..\testcloria\jupyter.expresso
```
## Test 3: saturn.expresso
```bash
expressor transpile --verbose ..\..\..\..\testcloria\saturn.expresso
```
```bash
expressor build --verbose ..\..\..\..\testcloria\saturn.expresso
```
```bash
expressor run --verbose ..\..\..\..\testcloria\saturn.expresso
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
- estoy implementando data types y records que en expresso se ven asi 
 data nat = {
    Zero,
    S(n:nat)
 }
 data list = {
    Nil,
    Cons(car:any, cdr:list)
 }
 data gender = {
    Male,
    Female
 }
 y al transpilarse se verían asi 
 sealed interface Nat permits Zero, S {}
 record Zero() implements Nat {}
 record S(Nat n) implements Nat {}

- cual es la mejor manera de implementarlo siguiendo mi paradigma actual en el proyecto.

- Necesito que me expliques esta tarea que consiste Expresión print como valor (none). El print puede ser también expresión. En que consiste dame ejemplos.

- let x = none
- let y = print(42)

- Object x = null;
- Object y = printAndReturnNull(42);

- Porque es necesario printAndReturnNull como método auxiliar? es porque el print no puede retornar un valor e imprimir? 
