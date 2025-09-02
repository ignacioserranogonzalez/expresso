package una.paradigmas.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import java.nio.file.Path;
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
    private CommonOptions commonOptions;

    @Parameters(index = "0")
    private Path input;

    @Override
    public void run() {
        try {
            // transpile
            Path javaFile = TranspileCommand.transpileCommon(commonOptions, input, commonOptions.outputDir);
            if (javaFile == null) return;

            // compile
            boolean compiled = BuildCommand.buildCommon(commonOptions, javaFile, commonOptions.outputDir);
            if (!compiled) return;

            // run
            executeCommon(javaFile, commonOptions.outputDir);

        } catch (Exception e) {
            System.err.println("ERROR - " + e.getMessage());
        }
    }

    private void executeCommon(Path javaFile, Path outputDir) {
        String className = javaFile.getFileName().toString().replace(".java", "");
        
        if (commonOptions.verbose) System.out.println("Ejecutando " + className + "...");
        
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", outputDir.toString(), className);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                if (commonOptions.verbose) System.out.println("SUCCESS - Ejecucion completada");
            } else {
                System.err.println("ERROR - Fallo en la ejecucion (codigo: " + exitCode + ")");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR - No se pudo ejecutar: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}