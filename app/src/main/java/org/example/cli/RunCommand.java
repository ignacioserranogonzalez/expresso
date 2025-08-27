package org.example.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
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

@Command(name = "run")
public class RunCommand implements Runnable {

    @Mixin
    private CommonOptions commonOptions;

    @Parameters(index = "0")
    private File input;

    @Option(names = {"--out"}, defaultValue = ".")
    private String outputDir;

    @Override
    public void run() {

        if (!input.exists() || input.length() == 0 || !input.getName().endsWith(".expresso")) {
            System.err.println("ERROR - Archivo .expresso no existe o está vacío");
            return;
        }

        // Transpilacion
        String outputPath;
         if (outputDir.equals(".")) {
            outputPath = input.getParent() != null ? input.getParent() : ".";
        } else {
            outputPath = outputDir;
        }
        File outputDirFile = new File(outputPath);
        if (!outputDirFile.exists()) outputDirFile.mkdirs();

        String outputFileName = input.getName().replace(".expresso", ".java");
        File outputFile = new File(outputDirFile, outputFileName);
        
        Path templatePath = Paths.get("resources/template/HelloWorld.java");

         if (commonOptions.verbose) System.out.println("Leyendo .expresso...");
           
        try (FileWriter writer = new FileWriter(outputFile)) {
            if (commonOptions.verbose) System.out.println("Transpilando...");
            String template = new String(Files.readAllBytes(templatePath));
            writer.write(template);
            if (commonOptions.verbose) System.out.println("SUCCESS - Archivo .java guardado en: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("ERROR - No se pudo escribir el archivo " + e.getMessage());
        }
        
        // Compilacion
        if (commonOptions.verbose) System.out.println("Compilando " + outputFile.getName() + " a .class...");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("ERROR - JavaCompiler no disponible");
            return;
        }
        int result = compiler.run(null, null, null, outputFile.getAbsolutePath());
        if (result != 0) {
            System.err.println("ERROR - Fallo en la compilacion.");
            return;
        }
        if (commonOptions.verbose) System.out.println("SUCCESS - Compilado a .class en: " + outputDirFile.getAbsolutePath());

        // Ejecucion
        if (commonOptions.verbose) System.out.println("Ejecutando " + outputFileName.replace(".java", "") + "...");
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", outputDir, outputFileName.replace(".java", ""));
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                if (commonOptions.verbose) System.out.println("SUCCESS - Ejecucion completada");
            } else {
                System.err.println("ERROR - Fallo en la ejecucion");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR - No se pudo ejecutar: " + e.getMessage());
        }
    }
}