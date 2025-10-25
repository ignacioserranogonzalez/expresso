package una.paradigmas.node;

public sealed interface Pattern extends Node permits DataPat, NativePat, WildcardPat, VarPat {}
