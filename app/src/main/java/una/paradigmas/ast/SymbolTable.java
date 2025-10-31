package una.paradigmas.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

public class SymbolTable {
    private final Map<String, SymbolInfo> symbols = new HashMap<>();
    
    public enum SymbolType {
        FUNCTION,
        VARIABLE,    
        PARAMETER, 
        CONSTRUCTOR,
        DATA_TYPE
    }
    
    public record SymbolInfo(SymbolType symbolType, String typeLiteral) {}
    
    public void addSymbol(String name, SymbolType symbolType, String typeLiteral) {
        symbols.put(name, new SymbolInfo(symbolType, typeLiteral));
    }

    public boolean isConstructor(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null && info.symbolType() == SymbolType.CONSTRUCTOR;
    }
    
    public boolean isFunction(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null && info.symbolType() == SymbolType.FUNCTION;
    }

    public boolean isDataType(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null && info.symbolType() == SymbolType.DATA_TYPE;
    }

    public SymbolType getSymbolType(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null ? info.symbolType() : null;
    }
    
    public String getTypeLiteral(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null ? info.typeLiteral() : "unknown";
    }
    
    public void setTypeLiteral(String name, String type) {
        SymbolInfo oldInfo = symbols.get(name);
        if (oldInfo != null) 
            symbols.put(name, new SymbolInfo(oldInfo.symbolType(), type));
    }

    public boolean contains(String name) {
        return symbols.containsKey(name);
    }
    
    public Set<String> getFunctionNames() {
        return symbols.entrySet().stream()
            .filter(entry -> entry.getValue().symbolType() == SymbolType.FUNCTION)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
    
    public Set<String> getConstructorNames() {
        return symbols.entrySet().stream()
            .filter(entry -> entry.getValue().symbolType() == SymbolType.CONSTRUCTOR)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
    
    public Set<String> getDataNames() {
        return symbols.entrySet().stream()
            .filter(entry -> entry.getValue().symbolType() == SymbolType.DATA_TYPE)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
    
    public Set<String> getAllSymbols() {
        return symbols.keySet();
    }
    
    public Set<String> getSymbolsByType(SymbolType type) {
        return symbols.entrySet().stream()
            .filter(entry -> entry.getValue().symbolType() == type)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return symbols.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> String.format("  %s: [%s, %s]", 
                e.getKey(), e.getValue().symbolType(), e.getValue().typeLiteral()))
            .collect(Collectors.joining("\n", 
                symbols.isEmpty() ? "SymbolTable{vacía}" : "SymbolTable {\n", 
                symbols.isEmpty() ? "" : "\n}"));
    }
}