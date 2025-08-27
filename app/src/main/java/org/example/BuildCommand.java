package org.example;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(name = "build")
public class BuildCommand implements Runnable {
    @Parameters(index = "0")
    private File input;

    @Option(names = {"--out"})
    private String outputDir = ".";

    @Option(names = {"--verbose"})
    private boolean verbose;

    @Override
    public void run() {
       
        if (!input.exists() || input.length() == 0 || !input.getName().endsWith(".expresso")) {
            System.err.println("ERROR - Archivo .expresso no existe o está vacío");
            return;
        }

        Path templatePath = Paths.get("resources/template/HelloWorld.java");

        String outputPath = outputDir.equals(".") ? input.getParent() : outputDir;
        File outputDirFile = new File(outputPath);
        if (!outputDirFile.exists()) outputDirFile.mkdirs();

        String outputFileName = input.getName().replace(".expresso", ".java");
        File outputFile = new File(outputDirFile, outputFileName);

 
        if (!outputFile.exists()) {
            if (verbose) System.out.println("Leyendo .expresso...");
           
            try (FileWriter writer = new FileWriter(outputFile)) {
                if (verbose) System.out.println("Transpilando...");
                String template = new String(Files.readAllBytes(templatePath));
                writer.write(template);
                if (verbose) System.out.println("SUCCESS - Archivo .java guardado en: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("ERROR - No se pudo escribir el archivo " + e.getMessage());
                return;
            }
        } else {
            if (verbose) System.out.println("Archivo .java existente, omitiendo transpilacion.");
        }

        
        if (verbose) System.out.println("Compilando " + outputFile.getName() + " a .class...");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("ERROR - JavaCompiler no disponible");
            return;
        }
        int result = compiler.run(null, null, null, outputFile.getAbsolutePath());
        if (result == 0) {
            if (verbose) System.out.println("SUCCESS - Compilado a .class en: " + outputDirFile.getAbsolutePath());
        } else {
            System.err.println("ERROR - Fallo en la compilación.");
        }
    }
}