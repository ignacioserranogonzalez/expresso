package org.example;

import org.example.cli.TranspileCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "expressor", subcommands = { TranspileCommand.class, BuildCommand.class, RunCommand.class })
public class App {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}