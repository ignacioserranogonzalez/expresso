package una.paradigmas.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import una.paradigmas.ast.AstBuilder;
import una.paradigmas.ast.ExpressoLexer;
import una.paradigmas.ast.ExpressoParser;
import una.paradigmas.ast.JavaCodeGenerator;
import una.paradigmas.node.Program;
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
    private static CommonOptions commonOptions;
    
    @Parameters(index = "0") 
    private Path input;
    
    @Override
    public void run() {
        transpileCommon(input, commonOptions.outputDir);
    }

    protected static Path transpileCommon(Path input, Path outputDir) {
        try {
            validateInput(input);
            Path outputFile = prepareOutputFile(input, outputDir);
            transpile(input, outputFile);
            
            if (commonOptions.verbose) System.out.println("SUCCESS - Archivo .java guardado en: " + outputFile.toAbsolutePath());
            return outputFile;
            
        } catch (Exception e) {
            System.err.println("ERROR - " + e.getMessage());
            return null;
        }
    }
    
    private static void validateInput(Path input) throws IOException {
        log("Leyendo .expresso...");
        String filename = input.getFileName().toString();
        if (!Files.exists(input) || Files.size(input) == 0 || !filename.toLowerCase().endsWith(".expresso")) {
            throw new IllegalArgumentException("Archivo .expresso no existe o esta vacio");
        }
    }
    
    private static Path prepareOutputFile(Path input, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        String baseName = input.getFileName().toString().replaceAll("(?i)\\.expresso$", "");
        String capitalizedName = capitalize(baseName) + ".java";
        return outputDir.resolve(capitalizedName);
    }

    private static void transpile(Path input, Path outputFile) throws IOException {
        // lee archivo .expresso
        String expressoCode = Files.readString(input, StandardCharsets.UTF_8);
        if (expressoCode.trim().isEmpty()) {
            throw new IllegalArgumentException("El archivo .expresso está vacío");
        }

        // Parseo con ANTLR4
        CharStream charStream = CharStreams.fromString(expressoCode);
        ExpressoLexer lexer = new ExpressoLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExpressoParser parser = new ExpressoParser(tokens);
       
        // Construir AST con AstBuilder
        AstBuilder astBuilder = new AstBuilder();
        Program ast = astBuilder.visitProgram(parser.program());

        // Genera código Java
        String className = outputFile.getFileName().toString().replace(".java", "");
        JavaCodeGenerator generator = new JavaCodeGenerator(className);
        String javaCode = generator.generate(ast);

        // Escribe el archivo .java en output dir
        Files.writeString(outputFile, javaCode, StandardCharsets.UTF_8);
    }
    
    private static void log(String msg) {
        if (commonOptions.verbose) System.out.println(msg);
    }

     private static String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}