package org.example.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

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

@Command(name = "transpile")
public class TranspileCommand implements Runnable {

    @Mixin 
    private CommonOptions commonOptions;
    
    @Parameters(index = "0") 
    private Path input;
    
    @Override
    public void run() {
        transpileCommon(commonOptions, input, commonOptions.outputDir);
    }

    protected static Path transpileCommon(CommonOptions commonOptions, Path input, Path outputDir) {
        try {
            validateInput(input);
            Path outputFile = prepareOutputFile(input, outputDir);
            transpile(commonOptions, outputFile);
            
            if (commonOptions.verbose) System.out.println("SUCCESS - Archivo .java guardado en: " + outputFile.toAbsolutePath());
            return outputFile;
            
        } catch (Exception e) {
            System.err.println("ERROR - " + e.getMessage());
            return null;
        }
    }
    
    private static void validateInput(Path input) throws IOException {
        String filename = input.getFileName().toString();
        if (!Files.exists(input) || Files.size(input) == 0 || !filename.toLowerCase().endsWith(".expresso")) {
            throw new IllegalArgumentException("Archivo .expresso no existe o esta vacio");
        }
    }
    
    private static Path prepareOutputFile(Path input, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        String filename = input.getFileName().toString();
        return outputDir.resolve(filename.replaceAll("(?i)\\.expresso$", ".java"));
    }
    
    private static void transpile(CommonOptions commonOptions, Path outputFile) throws IOException {

        Path templatePath = Paths.get(
            System.getProperty("PROJECT_ROOT"), 
            "resources/template/HelloWorld.java");

        if (commonOptions.verbose) System.out.println("Leyendo...\nBuscando template en: " + templatePath);
        if (!Files.exists(templatePath)) throw new IOException("No se encontro el template en: " + templatePath);
        
        String template = Files.readString(templatePath, StandardCharsets.UTF_8);
        
        if (commonOptions.verbose) System.out.println("Transpilando...");
        Files.writeString(outputFile, template, StandardCharsets.UTF_8);
    }
}