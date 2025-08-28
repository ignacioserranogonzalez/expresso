package org.example.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.*;

// TranspileCommand.java
@Command(name = "transpile")
public class TranspileCommand implements Runnable {
    @Mixin private CommonOptions commonOptions;
    @Parameters(index = "0") private Path input;
    @Option(names = {"--out"}) private Path outputDir = Paths.get(".");
    
    @Override
    public void run() {
        transpileCommon(commonOptions, input, outputDir, "transpile");
    }

    protected static Path transpileCommon(CommonOptions commonOptions, Path input, Path outputDir, String context) {
        try {
            validateInput(input);
            Path outputFile = prepareOutputFile(input, outputDir);
            transpile(commonOptions, outputFile);
            
            if (commonOptions.verbose) {
                System.out.println("SUCCESS - Archivo .java guardado en: " + outputFile.toAbsolutePath());
            }
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
        if (commonOptions.verbose) System.out.println("Leyendo .expresso...");
        String template = Files.readString(Paths.get("resources/template/HelloWorld.java"));
        
        if (commonOptions.verbose) System.out.println("Transpilando...");
        Files.writeString(outputFile, template);
    }
}