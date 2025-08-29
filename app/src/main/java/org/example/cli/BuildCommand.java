package org.example.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.nio.file.Path;

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