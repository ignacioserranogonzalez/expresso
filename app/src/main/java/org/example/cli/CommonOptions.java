package org.example.cli;

import java.nio.file.Path;
import java.nio.file.Paths;

import picocli.CommandLine.Option;

public class CommonOptions {
    @Option(names = {"--verbose"}) protected boolean verbose;
    @Option(names = {"--out"}) protected Path outputDir = Paths.get(".");
}
