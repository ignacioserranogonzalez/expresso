package org.example.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.nio.file.Path;

@Command(name = "build")
public class BuildCommand implements Runnable {

    @Mixin
    private CommonOptions commonOptions;

    @Parameters(index = "0")
    private Path input;

    @Override
    public void run() {
        try {
            Path javaFile = TranspileCommand.transpileCommon(commonOptions, input, commonOptions.outputDir);
            if (javaFile != null) buildCommon(commonOptions, javaFile, commonOptions.outputDir);
        } catch (Exception e) {
            System.err.println("ERROR - " + e.getMessage());
        }
    }

    protected static boolean buildCommon(CommonOptions commonOptions, Path javaFile, Path outputDir) {
        if (commonOptions.verbose) System.out.println("Compilando " + javaFile.getFileName() + " a .class...");
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("ERROR - JavaCompiler no disponible.");
            return false;
        }

        int result = compiler.run(null, null, null, 
                                "-d", outputDir.toString(), 
                                javaFile.toString());

        if (result == 0) {
            if (commonOptions.verbose) System.out.println("SUCCESS - Compilado a .class en: " + outputDir.toAbsolutePath());
            return true;
        } else {
            System.err.println("ERROR - Fallo en la compilacion");
            return false;
        }
    }
}