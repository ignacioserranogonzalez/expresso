grammar Expresso;

program: stat* EOF; 

// statements
stat: NEWLINE                       # blank
    | expr NEWLINE                  # expression
    | LET ID ASSIGN expr NEWLINE    # letDecl
    | PRINT '(' expr ')' NEWLINE    # print
    | function NEWLINE              # funDecl
;

function: FUN ID '(' paramList? ')' COLON TYPE '=' expr ;
paramList: param (',' param)*;
param: ID (COLON TYPE)?;                         // Parámetro con tipo opcional

// expressions
expr: <assoc=right> expr POW expr                           # Pow
    | <assoc=right> expr '?' expr ':' expr                  # TernaryCondition
    | (PLUS | MINUS) expr                                   # UnaryOp
    | expr (PLUS | MINUS) expr                              # AddSub
    | expr (MULT | DIV) expr                                # MultDiv
    | lambdaParams LAMBDA expr                              # Lambda
    | expr (INC | DEC)                                      # PostOp
    | ID '(' callArgs? ')'                                  # Call
    | '(' expr ')'                                          # Paren
    | FLOAT                                                 # Float
    | INT                                                   # Int
    | BOOL                                                  # Bool
    | STRING                                                # String
    | ID                                                    # Id
;

lambdaParams: '(' ')'                           // 0 args
            | '(' param (',' param)* ')'        // 1+ args con o sin tipos
            | param                             // 1 arg sin () con o sin tipo
;

callArgs: expr (',' expr)*;

num: INT | FLOAT;

// Lexer - ORDEN IMPORTANTE

// Palabras reservadas PRIMERO (antes de ID)
FUN     : 'fun';
LET     : 'let';
PRINT   : 'print';
TYPE    : 'int' | 'float' | 'boolean' | 'string' | 'any';
BOOL    : 'true' | 'false';

// Operadores
LAMBDA  : '->';
ASSIGN  : '=';
COLON   : ':';
POW     : '**';
INC     : '++';
DEC     : '--';
PLUS    : '+';
MINUS   : '-';
MULT    : '*';
DIV     : '/';

ID: [a-zA-Z_][a-zA-Z0-9_]*;

// Comentarios y whitespace
COMMENT : '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT : '/*' .*? '*/' -> skip;
NEWLINE : '\r'? '\n';
WS      : [ \t]+ -> skip;