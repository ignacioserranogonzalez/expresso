package una.paradigmas.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Proyecto: Expresso - Transpilador de lenguaje Expresso a Java
 * Curso: [EIF400-II-2025] Paradigmas de Programacion
 * Universidad Nacional de Costa Rica
 * 
 * Autores:
 * - Kendall Miso Chinchilla Araya  -   119310542
 * - Ignacio Serrano Gonzalez       -   402600631
 * - Minor Brenes Aguilar           -   116730106
 * - Pablo Chavarria Alvarez        -   117810573
 * 
 * Codigo de grupo: 02-1PM
 * 
 * Nota: Este codigo fue generado parcialmente con asistencia de IA
 * y posteriormente modificado, adaptado y validado por el equipo
 * de desarrollo para cumplir con los requerimientos especificos
 * del proyecto.
 */

@Command(name = "build")
public class BuildCommand implements Runnable {

    @Mixin 
    private static CommonOptions commonOptions;

    @Parameters(index = "0") 
    private Path input;

    @Override
    public void run() {
        
        Path outputDir = commonOptions.outputDir != null ? commonOptions.outputDir : Path.of("generated");
        Path javaFile = prepareJavaFile(input, outputDir);

        try {
            // Verificar que el .java exista
            if (!Files.exists(javaFile)) {
                throw new IOException("Archivo .java no encontrado: " + javaFile + ". Ejecuta 'transpile' primero.");
            }

            // Compilar con JavaCompiler
            compileJavaFile(javaFile, outputDir);
            log("SUCCESS - Archivo .class compilado en: " + outputDir.resolve(getBaseName(input) + ".class").toAbsolutePath());
        
        } catch (IOException e) {
            System.err.println("ERROR - " + e.getMessage());
        }
    }

    private void compileJavaFile(Path javaFile, Path outputDir) throws IOException {
        log("Compilando " + javaFile.getFileName() + " a .class...");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IOException("ERROR - JavaCompiler no disponible.");
        }

        int result = compiler.run(null, null, null, 
                            "-d", outputDir.toString(), 
                            javaFile.toString());

        if (result != 0) {
            throw new IOException("Compilación falló con código de salida: " + result);
        }
        
    }

    private static void log(String msg) {
        if (commonOptions.verbose) System.out.println(msg);
    }

    private Path prepareJavaFile(Path input, Path outputDir) {
        String baseName = getBaseName(input);
        String capitalizedName = capitalize(baseName) + ".java";
        return outputDir.resolve(capitalizedName);
    }

    private String getBaseName(Path input) {
        return input.getFileName().toString().replaceAll("(?i)\\.expresso$", "");
    }

    private static String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}

