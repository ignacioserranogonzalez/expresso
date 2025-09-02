package una.paradigmas.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.nio.file.Files;
import java.io.IOException;
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
        try {
            Path outputDir = commonOptions.outputDir != null ? commonOptions.outputDir : input.getParent();
            Path templateFile = Paths.get(
                System.getProperty("PROJECT_ROOT"), 
                "resources/template/HelloWorld.java");
            Path classFile = outputDir.resolve(input.getFileName().toString().replace(".expresso", ".class"));

            var expTime   = Files.getLastModifiedTime(input);
            var classTime = Files.exists(classFile) ? Files.getLastModifiedTime(classFile) : null;

            if (classTime != null && classTime.compareTo(expTime) >= 0) {
                log("Usando .class existente y actualizado...");
                execute(classFile, outputDir);

            } else {

                validateInput(input);

                if (classTime != null && expTime.compareTo(classTime) > 0) {
                    log("Recompilando...");
                    if (!BuildCommand.buildCommon(templateFile, outputDir)) return;
                } else {
                    log("Transpilando .expresso a .java...");
                    templateFile = TranspileCommand.transpileCommon(input, outputDir);
                    if (templateFile == null) return;
                    log("Compilando .java...");
                    if (!BuildCommand.buildCommon(templateFile, outputDir)) return;
                }
                
                execute(classFile, outputDir);
            }

        } catch (Exception e) {
            System.err.println("ERROR - " + e.getMessage());
        }
    }
    
    private static void validateInput(Path input) throws IOException {
        log("Leyendo .expresso...");
        String filename = input.getFileName().toString();
        if (!Files.exists(input) || Files.size(input) == 0 || !filename.toLowerCase().endsWith(".expresso")) {
            throw new IllegalArgumentException("Archivo .expresso no existe o esta vacio");
        }
    }

    private static void log(String msg) {
        if (commonOptions.verbose) System.out.println(msg);
    }

    private void execute(Path classFile, Path outputDir) {
        String className = classFile.getFileName().toString().replace(".class", "");
        
        log("Ejecutando " + className + "...");
        
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", outputDir.toString(), className);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log("SUCCESS - Ejecucion completada");
            } else {
                System.err.println("ERROR - Fallo en la ejecucion (codigo: " + exitCode + ")");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR - No se pudo ejecutar: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}