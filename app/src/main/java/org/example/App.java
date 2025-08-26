package org.example;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "expressor", subcommands = { TranspileCommand.class }, description = "Transpilador Expresso")
public class App {
    @Option(names = {"--verbose"}, description = "Muestra pasos del proceso")
    boolean verbose;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}