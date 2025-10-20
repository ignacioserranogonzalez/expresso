grammar Expresso;
import ExpressoTypes;

program: stat* EOF; 

// statements
stat: NEWLINE                                               # blank
    | expr NEWLINE                                          # expression
    | LET ID (':' type)? ASSIGN expr NEWLINE                # letDecl
    | PRINT '(' expr ')' NEWLINE                            # print
    | FUN ID '(' paramList? ')' ':' type '=' expr NEWLINE   # funDecl
    | DATA ID ASSIGN '{' constructorList '}' NEWLINE        # dataDecl
;

paramList: param (',' param)*;
param: ID (':' type)?;

constructorList: constructor (',' constructor)*;
constructor: ID arguments?;
arguments: '(' argument (',' argument)* ')';
argument: (ID ':')? type;

// expressions
expr: <assoc=right> expr POW expr                    # Pow
    | <assoc=right> expr '?' expr ':' expr           # TernaryCondition
    | (PLUS | MINUS) expr                            # UnaryOp
    | expr (PLUS | MINUS) expr                       # AddSub
    | expr (MULT | DIV) expr                         # MultDiv
    | lambdaParams LAMBDA expr                       # Lambda
    | expr (INC | DEC)                               # PostOp
    | ID '(' argList? ')'                            # Call
    | NEW constructorExpr                            # ConstructorInvocation
    | '(' expr ')'                                   # Paren
    | INT                                            # Int
    | FLOAT                                          # Float
    | BOOLEAN                                        # Boolean
    | STRING                                         # String
    | ID                                             # Id
;

// gramatica sugerida para ^
constructorExpr: ID ('(' argList ')')?;
argList: expr (',' expr)*;  // Regla unificada para listas de argumentos de Call y ConstructorInvocation

lambdaParams: '(' ')'          // 0 args
    | '(' ID (',' ID)? ')'     // 1-2 args con ()
    | ID                       // 1 arg sin ()
;

// Lexer (igual que antes)
LET     : 'let';
DATA    : 'data';
ASSIGN  : '=';
PRINT   : 'print';
FUN     : 'fun';
LAMBDA  : '->';
NEW     : '^';

INT     : [0-9]+;
FLOAT   : [0-9]+ '.' [0-9]* | '.' [0-9]+;
BOOLEAN : 'true' | 'false';
STRING  : '"' (ESC | ~["\\])* '"';

fragment ESC : '\\' (["\\/bfnrt] | UNICODE);
fragment UNICODE : 'u' HEX HEX HEX HEX;
fragment HEX : [0-9a-fA-F];

POW     : '**';
INC     : '++';
DEC     : '--';
PLUS    : '+';
MINUS   : '-';
MULT    : '*';
DIV     : '/';

ID: [a-zA-Z_][a-zA-Z0-9_]*;

COMMENT: '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT: '/*' (~[*] | '*' ~[/])* '*/' -> skip;
NEWLINE: '\r'? '\n';
WS: [ \t]+ -> skip;