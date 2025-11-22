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

@Command(name = "run")
public class RunCommand implements Runnable {

    @Mixin
    private static CommonOptions commonOptions;

    @Parameters(index = "0")
    private Path input;

    private void runClassFile(Path classFile, Path outputDir) throws IOException, InterruptedException {
        String className = classFile.getFileName().toString().replaceAll("\\.class$", "");
        log("\nRunning " + className + "...");
        System.out.println();
        
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", outputDir.toString(), className);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Code execution failed. Exit code: " + exitCode);
        }
    }
    
    @Override
    public void run() {
        if (input == null) {
            throw new IllegalArgumentException("Must provide the path to an .expresso file as an argument");
        }
        Path outputDir = commonOptions.outputDir != null ? commonOptions.outputDir : Path.of("generated");
        
        try {
            String className = capitalize(getBaseName(input));
            Path classFile = outputDir.resolve(className + ".class");
        
            boolean originalVerbose = commonOptions.verbose;
            commonOptions.verbose = true;
            
            if (!Files.exists(classFile)) {
                BuildCommand.buildCommon(input, outputDir);
            }

            commonOptions.verbose = originalVerbose;
            
            if (!Files.exists(classFile)) {
                throw new IOException("Class file not generated: " + classFile);
            }
            
            runClassFile(classFile, outputDir);
            log("\n[SUCCESS] - Code Execution Successful\n");
        
        } catch (IOException | InterruptedException e) {
            System.err.println("[ERROR] - " + e.getMessage());
        }
    }
    
    private String getBaseName(Path input) {
        return input.getFileName().toString().replaceAll("(?i)\\.expresso$", "");
    }

    private static void log(String msg) {
        if (commonOptions.verbose) System.out.println(msg);
    }

    private static String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}