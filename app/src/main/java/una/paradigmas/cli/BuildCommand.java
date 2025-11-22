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
 * - Ignacio Serrano Gonzalez
 * - Kendall Miso Chinchilla Araya
 * - Minor Brenes Aguilar
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
        if (input == null) {
            throw new IllegalArgumentException("Must provide a .expresso file path as argument");
        }
        Path outputDir = commonOptions.outputDir != null ? commonOptions.outputDir : Path.of("generated");
        buildCommon(input, outputDir);
    }

    public static Path buildCommon(Path input, Path outputDir) {
        Path javaFile = prepareJavaFile(input, outputDir);
   
        try {
            if (!Files.exists(javaFile)) {
                TranspileCommand.transpileCommon(input, outputDir);
                if (!Files.exists(javaFile)) {
                    throw new IOException("Transpilation failed, .java file not generated: " + javaFile);
                }
            }
   
            compileJavaFile(javaFile, outputDir);
            log("\n[SUCCESS] - .class file compiled at: " + outputDir.resolve(sanitizeFileName(capitalize(getBaseName(input)) + ".class")).toAbsolutePath() + "\n");
       
        } catch (IOException e) {
            System.err.println("[ERROR] - " + e.getMessage() + "\n");
        }
        return javaFile;
    }

    private static void compileJavaFile(Path javaFile, Path outputDir) throws IOException {
        log("\nCompiling " + javaFile.getFileName() + " to .class...");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IOException("[ERROR] - JavaCompiler not available");
        }
        int result = compiler.run(null, null, null,
                            "-d", outputDir.toString(),
                            javaFile.toString());
        if (result != 0) {
            throw new IOException("Compilation failed with exit code: " + result);
        }
    }

    private static void log(String msg) {
        if (commonOptions.verbose) System.out.println(msg);
    }

    private static Path prepareJavaFile(Path input, Path outputDir) {
        String baseName = getBaseName(input);
        String capitalizedName = capitalize(baseName) + ".java";
        return outputDir.resolve(sanitizeFileName(capitalizedName));
    }

    private static String getBaseName(Path input) {
        return input.getFileName().toString().replaceAll("(?i)\\.expresso$", "");
    }

    private static String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[<>|?*\\\\:/\"]", "_");
    }
}