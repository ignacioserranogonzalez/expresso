package una.paradigmas.pattern;

public sealed interface Pattern
    permits DataPattern, NativePattern, VarPattern, WildcardPattern {
}

