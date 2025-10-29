package una.paradigmas.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SymbolTable {
    private final Map<String, SymbolType> symbols = new HashMap<>();
    
    public enum SymbolType {
        FUNCTION,
        VARIABLE,    
        PARAMETER, 
        CONSTRUCTOR,
        DATA_TYPE
    }
    
    public void addSymbol(String name, SymbolType type) {
        symbols.put(name, type);
    }

    public boolean isConstructor(String name) {
        return symbols.get(name) == SymbolType.CONSTRUCTOR;
    }
    
    public boolean isFunction(String name) {
        return symbols.get(name) == SymbolType.FUNCTION;
    }

    public boolean isDataType(String name) {
        return symbols.get(name) == SymbolType.DATA_TYPE;
    }

    public SymbolType getSymbolType(String name) {
        return symbols.get(name);
    }

    public boolean contains(String name) {
        return symbols.containsKey(name);
    }
    
    public Set<String> getFunctionNames() {
        return symbols.entrySet().stream()
            .filter(entry -> entry.getValue() == SymbolType.FUNCTION)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
    
    public Set<String> getConstructorNames() {
        return symbols.entrySet().stream()
            .filter(entry -> entry.getValue() == SymbolType.CONSTRUCTOR)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
    
    public Set<String> getDataNames() {
        return symbols.entrySet().stream()
            .filter(entry -> entry.getValue() == SymbolType.DATA_TYPE)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
    
    public Set<String> getAllSymbols() {
        return symbols.keySet();
    }
    
    public Set<String> getSymbolsByType(SymbolType type) {
        return symbols.entrySet().stream()
            .filter(entry -> entry.getValue() == type)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
}