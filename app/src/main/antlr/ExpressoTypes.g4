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
      | ID               # AtomicId
;

tuple : '(' flatType (',' flatType)+ ')'   # TupleType
;

arrow : tuple '->' flatType                # ArrowTuple
      | atomic '->' flatType               # ArrowAtomic  
      | '(' arrow ')' '->' flatType        # ArrowParen
;