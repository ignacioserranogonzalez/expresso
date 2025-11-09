package una.paradigmas.ast;

import java.util.List;

public record FunctionalTypeMapper() {
    
    public static String mapFunctionalType(List<String> paramTypes, String returnType, int paramCount, boolean isVoidLike) {
        if (paramTypes == null || paramCount < 0 || (paramTypes.size() != paramCount && paramCount > 0)) {
            throw new IllegalArgumentException("Invalid Parameter Count: paramTypes or paramCount are not consistent");
        }
        
        if (paramCount == 0) {
            return "Supplier<" + returnType + ">";
        }
        
        if (paramCount == 1) {
            String paramType = paramTypes.get(0);
            if (isVoidLike) {
                return "Consumer<" + paramType + ">";
            } else if (returnType.equals("boolean") || returnType.equals("Boolean")) {
                return "Predicate<" + paramType + ">";
            } else if (paramType.equals(returnType)) {
                return "UnaryOperator<" + returnType + ">";
            } else {
                return "Function<" + paramType + ", " + returnType + ">";
            }
        }
        
        if (paramCount == 2) {
            String param1Type = paramTypes.get(0);
            String param2Type = paramTypes.get(1);
            
            if (isVoidLike) {
                return "BiConsumer<" + param1Type + ", " + param2Type + ">";
            } else if (returnType.equals("boolean") || returnType.equals("Boolean")) {
                return "BiPredicate<" + param1Type + ", " + param2Type + ">";
            } else if (param1Type.equals(param2Type) && param1Type.equals(returnType)) {
                return "BinaryOperator<" + returnType + ">";
            } else {
                return "BiFunction<" + param1Type + ", " + param2Type + ", " + returnType + ">";
            }
        }
        
        String paramTypesStr = String.join(", ", paramTypes);
        return "Function" + paramCount + "<" + paramTypesStr + ", " + returnType + ">";
    }
    
    public static String getFunctionalMethodName(String functionalType) {
        if (functionalType == null) {
            return "apply";
        }
        
        return switch (functionalType) {
            case String ft when ft.startsWith("Supplier") -> "get";
            case String ft when ft.startsWith("Consumer") -> "accept";
            case String ft when ft.startsWith("BiConsumer") -> "accept";
            case String ft when ft.startsWith("Predicate") -> "test";
            case String ft when ft.startsWith("BiPredicate") -> "test";
            case String ft when ft.startsWith("UnaryOperator") || 
                               ft.startsWith("BinaryOperator") || 
                               ft.startsWith("Function") || 
                               ft.startsWith("BiFunction") -> "apply";
            default -> "apply";
        };
    }

    public static boolean isFunctionalType(String type) {
        if (type == null) return false;
        
        return type.startsWith("Supplier") ||
               type.startsWith("Consumer") ||
               type.startsWith("BiConsumer") ||
               type.startsWith("Predicate") ||
               type.startsWith("BiPredicate") ||
               type.startsWith("UnaryOperator") ||
               type.startsWith("BinaryOperator") ||
               type.startsWith("Function") ||
               type.startsWith("BiFunction");
    }
    
    public static boolean isVoidLike(String returnType) {
        return returnType != null && (returnType.equals("void") || returnType.equals("null"));
    }
}