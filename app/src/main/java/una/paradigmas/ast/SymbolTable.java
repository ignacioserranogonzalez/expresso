package una.paradigmas.ast;

import java.util.HashMap;
import java.util.List;
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
    private SymbolTable parent = null;
    private final Map<String, List<String>> methodParamTypes = new HashMap<>();

    public void addMethodParamTypes(String name, List<String> paramTypes) {
        methodParamTypes.put(name, paramTypes);
    }

    public List<String> getMethodParamTypes(String name) {
        return methodParamTypes.get(name);
    }

    // private String ctxId = "SymbolTable";
    
    public enum SymbolType {
        METHOD,
        LAMBDA,
        VARIABLE,   
        PARAMETER, 
        CONSTRUCTOR,
        DATA_TYPE
    }
    
    public record SymbolInfo(SymbolType symbolType, String type, String functionType) {}

    public void setParent(SymbolTable p){
        this.parent = p;
    }

    public SymbolTable getParent(){
        return this.parent;
    }

    // public void setCtxId(String id){
    //     this.ctxId = id;
    // }

    // public String getCtxId(){
    //     return this.ctxId;
    // }
    
    public void addSymbol(String name, SymbolType symbolType, String type, String functionType) {
        symbols.put(name, new SymbolInfo(symbolType, type, functionType));
    }

    public boolean isConstructor(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null && info.symbolType() == SymbolType.CONSTRUCTOR;
    }
    
    public boolean isMethod(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null && info.symbolType() == SymbolType.METHOD;
    }

    public boolean isLambda(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null && info.symbolType() == SymbolType.LAMBDA;
    }

    public boolean isDataType(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null && info.symbolType() == SymbolType.DATA_TYPE;
    }

    public SymbolType getSymbolType(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null ? info.symbolType() : null;
    }
    
    public String getType(String name) {
        SymbolInfo info = symbols.get(name);
        return info != null ? info.type() : "unknown";
    }
    
    public void setType(String name, String type) {
        SymbolInfo oldInfo = symbols.get(name);
        if (oldInfo != null) 
            symbols.put(name, new SymbolInfo(oldInfo.symbolType(), type, null));
    }

    public String getFunctionType(String name) {
        SymbolInfo info = symbols.get(name);
        String functionalType = info.functionType();
        return info != null && functionalType != null ? functionalType : "unknown";
    }

    public void setFunctionType(String name, String type) {
        SymbolInfo oldInfo = symbols.get(name);
        if (oldInfo != null) 
            symbols.put(name, new SymbolInfo(oldInfo.symbolType(), oldInfo.type(), type));
    }

    public boolean contains(String name) {
        return symbols.containsKey(name);
    }

    public List<SymbolInfo> getParametersList() {
        return symbols.entrySet().stream()
            .filter(entry -> entry.getValue().symbolType() == SymbolType.PARAMETER)
            .map(Map.Entry::getValue)  // Cambiar de getKey() a getValue() para obtener SymbolInfo
            .collect(Collectors.toList());
    }
    
    public Set<String> getMethodNames() {
        return symbols.entrySet().stream()
            .filter(entry -> entry.getValue().symbolType() == SymbolType.METHOD)
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
            .map(e -> String.format("  %s: [%s, %s, %s]", 
                e.getKey(), e.getValue().symbolType(), e.getValue().type(), e.getValue().functionType()))
            .collect(Collectors.joining("\n", 
                symbols.isEmpty() ? " {empty}" : " {\n", 
                symbols.isEmpty() ? "" : "\n}"));
    }
}