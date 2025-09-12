grammar Expresso;

program: stat* EOF; 

// statements
stat: NEWLINE                        # blank
    | expr NEWLINE                   # expression
//    | LET ID ASSIGN expr NEWLINE     # letDecl
//    | PRINT '(' expr ')' NEWLINE     # print
;

// expressions
expr: expr POW expr                  # PowExpr
//    | expr (MULT | DIV) expr         # MultDivExpr
    | expr (PLUS | MINUS) expr       # BinaryOp
//    | expr INC                       # PostIncrExpr
//    | expr DEC                       # PostDecrExpr
//    | ID '(' expr ')'                # CallExpr
//    | ID LAMBDA expr                 # LambdaExpr
//    | '(' expr ')'                   # ParenExpr
//    | SIGNED_INT                     # SignedIntExpr
    | INT                              # Int
//    | ID                             # IdExpr
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