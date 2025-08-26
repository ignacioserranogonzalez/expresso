package org.example;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(name = "transpile", description = "Transpila un archivo .expresso a .java")
public class TranspileCommand implements Runnable {
    @Parameters(index = "0", description = "Archivo .expresso a transpilar")
    private File inputFile;

    @Option(names = {"--out"}, description = "Carpeta de salida (por defecto: .)")
    private String outputDir = ".";

    @Option(names = {"--verbose"}, description = "Muestra pasos del proceso")
    private boolean verbose;

    @Override
    public void run() {
        if (!inputFile.exists() || inputFile.length() == 0 || !inputFile.getName().endsWith(".expresso")) {
            System.err.println("Error: Archivo .expresso no existe o está vacío");
            return;
        }

        
        Path templatePath = Paths.get("resources/template/HelloWorld.java");
        System.out.println(templatePath.toAbsolutePath());
        try {
            if (!Files.exists(templatePath) || Files.size(templatePath) == 0) {
                System.err.println("Error: Plantilla HelloWorld.java no existe o está vacía");
                return;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String outputPath = outputDir.equals(".") ? inputFile.getParent() : outputDir;
        File outputDirFile = new File(outputPath);
        if (!outputDirFile.exists()) outputDirFile.mkdirs();

        String outputFileName = inputFile.getName().replace(".expresso", ".java");
        File outputFile = new File(outputDirFile, outputFileName);

        if (verbose) System.out.println("Leyendo archivo...");
        try (FileWriter writer = new FileWriter(outputFile)) {
            if (verbose) System.out.println("Transpilando...");
            String template = new String(Files.readAllBytes(templatePath));
            writer.write(template);
            if (verbose) System.out.println("Guardando en: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }
}