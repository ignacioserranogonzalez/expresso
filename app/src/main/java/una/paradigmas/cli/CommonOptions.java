package una.paradigmas.cli;

import java.nio.file.Path;
import java.nio.file.Paths;

import picocli.CommandLine.Option;

/**
 * Proyecto: Expresso - Transpilador de lenguaje Expresso a Java
 * Curso: [EIF400-II-2025] Paradigmas de Programacion
 * Universidad Nacional de Costa Rica
 * 
 * Autores:
 * - Ignacio Serrano Gonzalez
 * - Kendall Miso Chinchilla Araya
 * - Minor Brenes Aguilar
 * 
 * Codigo de grupo: 02-1PM
 * 
 * Nota: Este codigo fue generado parcialmente con asistencia de IA
 * y posteriormente modificado, adaptado y validado por el equipo
 * de desarrollo para cumplir con los requerimientos especificos
 * del proyecto.
 */

public class CommonOptions {
    @Option(names = {"--verbose"}) 
    protected boolean verbose;

    @Option(names = {"--out"}) 
    protected Path outputDir = Paths.get(".");
}
