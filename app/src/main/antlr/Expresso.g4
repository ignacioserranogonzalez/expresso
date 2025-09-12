grammar Expresso;

program: stat* EOF; 

// statements
stat: NEWLINE                        # blank
    | expr NEWLINE                   # expression
    | LET ID ASSIGN expr NEWLINE     # letDecl
    | PRINT '(' expr ')' NEWLINE     # print
;

// expressions
expr: expr POW expr
    | expr (MULT | DIV) expr
    | expr (PLUS | MINUS) expr
    | expr INC
    | expr DEC
    | ID '(' expr ')' // solo un parametro de momento
    | ID LAMBDA expr
    | '(' expr ')'
    | SIGNED_INT
    | INT
    | ID
;

// Lexer

LET     : 'let';
ASSIGN  : '=';
PRINT   : 'print';
LAMBDA  : '->';

INT : [0-9]+;
SIGNED_INT : ('+'|'-')? INT;
ID  : [a-zA-Z_][a-zA-Z0-9_]*;
POW : '**';
INC : '++';
DEC : '--';
PLUS  : '+';
MINUS  : '-';
MULT  : '*';
DIV  : '/';

COMMENT: '//' ~[\r\n]* -> skip ;
NEWLINE: '\r'? '\n';
WS: [ \t]+ -> skip;
