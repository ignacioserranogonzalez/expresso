grammar ExpressoTypes;

// Grammar of types
type : flatType          # TypeFlat
     | tuple             # TypeTuple  
     | '(' type ')'      # TypeParen
;

flatType : atomic        # FlatAtomic
         | arrow         # FlatArrow
;

atomic : 'any'           # AtomicAny
      | 'void'           # AtomicVoid
      | 'int'            # AtomicInt
      | 'float'          # AtomicFloat
      | 'string'         # AtomicString
      | ID               # CustomType
;

tuple : '(' flatType (',' flatType)+ ')'   # TupleType
; // No se puede anidar tuples

arrow : tuple '->' flatType                # ArrowTuple
      | atomic '->' flatType               # ArrowAtomic  
      | '(' arrow ')' '->' flatType        # ArrowParen
; // '->' Asocia derecha. No es posible retornar un tuple.

// usergiven :  
// un nombre creado con una declaracion data.

// Lexer
ID: [a-zA-Z_][a-zA-Z0-9_]*;
WS: [ \t]+ -> skip;