package org.example.cli;

import picocli.CommandLine.Option;

public class CommonOptions {
    @Option(names = {"--verbose"})
    public boolean verbose;
}
