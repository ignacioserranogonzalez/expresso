# Expresso - Sprint Final
EIF400-II-2025 Paradigmas de Programación, Grupo 02-1PM  
Universidad Nacional de Costa Rica, Escuela de Informática
_Implementado en el marco del curso de Paradigmas de Programación, dirigido por el profesor Carlos Loría Saenz_

## Autores
- Ignacio Serrano Gonzalez
- Kendall Miso Chinchilla Araya
- Minor Brenes Aguilar  
- Pablo Chavarria Alvarez  

## Descripción
Transpilador CLI "expressor" para el minilenguaje Expresso, implementado en Java 23+. Realiza la transpilación, compilación y ejecución de archivos `.expresso` a Java, generando archivos `.java` y `.class` en el directorio especificado. Soporta tipos primitivos `(int, float, boolean, string, any)`, tipos de usuario con `data`, `lambdas`, funciones recursivas con `fun`, pattern matching con `match`, casting con `:` y operadores `relacionales/booleanos`. Usa Picocli para manejar argumentos, Gradle para la construcción, y `jpackage` para generar una imagen ejecutable en consola pura (Windows/cmd). El diseño sigue principios de OOP (clases por subcomando, encapsulación), modularidad (paquete `una.paradigmas.cli`), y un estilo de programación funcional.

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
