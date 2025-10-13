grammar Expresso;

program: stat* EOF; 

// statements
stat: NEWLINE                       # blank
    | expr NEWLINE                  # expression
    | LET ID ASSIGN expr NEWLINE    # letDecl
    | LET ID COLON TYPE ASSIGN expr NEWLINE  # letDeclWithType
    | PRINT '(' expr ')' NEWLINE    # print
    | function NEWLINE              # funDecl
;

function: FUN ID '(' paramList? ')' COLON TYPE '=' expr ;
paramList: param (',' param)*;
param: ID (COLON TYPE)?;                         // Parámetro con tipo opcional

// expressions (igual que antes)
expr: <assoc=right> expr POW expr                           # Pow
    | <assoc=right> expr '?' expr ':' expr                  # TernaryCondition
    | (PLUS | MINUS) expr                                   # UnaryOp
    | expr (PLUS | MINUS) expr                              # AddSub
    | expr (MULT | DIV) expr                                # MultDiv
    | lambdaParams LAMBDA expr                              # Lambda
    | expr (INC)                                            # PostOp
    | ID '(' callArgs? ')'                                  # Call
    | '(' expr ')'                                          # Paren
    | INT                                                   # Int
    | FLOAT                                                 # Float
    | BOOLEAN                                               # Boolean
    | STRING                                                # String
    | ID                                                    # Id
;

lambdaParams: '(' ')'                           // 0 args
            | '(' param (',' param)? ')'        // 1-2 args con o sin tipos
            | param                             // 1 arg sin () con o sin tipo
;

callArgs: expr (',' expr)* ;

num: INT;

// Lexer
TYPE: 'int' | 'float' | 'boolean' | 'string' | 'any';
COLON   : ':';
FUN     : 'fun';
LET     : 'let';
ASSIGN  : '=';
PRINT   : 'print';
LAMBDA  : '->';
POW     : '**';
PLUS    : '+';
MINUS   : '-';
MULT    : '*';
DIV     : '/';
INC     : '++';
BOOLEAN : 'true' | 'false';

INT: [0-9]+;
FLOAT: [0-9]+ '.' [0-9]* | '.' [0-9]+;
STRING: '"' (ESC | ~["\\])* '"';
ID: [a-zA-Z_][a-zA-Z0-9_]*;

fragment ESC: '\\' (["\\/bfnrt] | UNICODE);
fragment UNICODE: 'u' HEX HEX HEX HEX;
fragment HEX: [0-9a-fA-F];

COMMENT: '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT: '/*' (~[*] | '*' ~[/])* '*/' -> skip;
NEWLINE: '\r'? '\n';
WS: [ \t]+ -> skip;