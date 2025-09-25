package una.paradigmas.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder;
import java.lang.Process;

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

@Command(name = "run")
public class RunCommand implements Runnable {

    @Mixin
    private static CommonOptions commonOptions;

    @Parameters(index = "0")
    private Path input;

    @Override
    public void run() {
        Path outputDir = commonOptions.outputDir != null ? commonOptions.outputDir : Path.of("generated");
        Path classFile = prepareClassFile(input, outputDir);
        try {
            // Verifica que el .class exista
            if (!Files.exists(classFile)) {
                throw new IOException("Archivo .class no encontrado: " + classFile + ". Ejecuta 'build' primero.");
            }
            // Ejecuta el .class
            runClassFile(classFile, outputDir);
            log("SUCCESS - Ejecución completada exitosamente.");
        
        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR - " + e.getMessage());
        }
    }

    private void runClassFile(Path classFile, Path outputDir) throws IOException, InterruptedException {
        String className = classFile.getFileName().toString().replaceAll("\\.class$", "");
        log("Ejecutando " + className + "...");
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", outputDir.toString(), className);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Captura la salida
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (commonOptions.verbose) {
                    System.out.println(line);
                }
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Ejecución falló con código de salida: " + exitCode);
        }
    }

    private static void log(String msg) {
        if (commonOptions.verbose) System.out.println(msg);
    }

     private Path prepareClassFile(Path input, Path outputDir) {
        String baseName = getBaseName(input);
        String capitalizedName = capitalize(baseName) + ".class";
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