grammar Expresso;

program: stat* EOF; 

// statements
stat: NEWLINE                        # blank
    | expr NEWLINE        # expression
    | LET ID ASSIGN expr NEWLINE     # letDecl
    | PRINT '(' expr ')' NEWLINE     # print
;

// expressions (igual que antes)
expr: <assoc=right> expr POW expr                           # Pow
    | <assoc=right> expr '?' expr ':' expr                  # TernaryCondition
    | expr (PLUS | MINUS) expr                              # AddSub
    | expr (MULT | DIV) expr                                # MultDiv
    | lambdaParams LAMBDA expr                              # Lambda
    | (PLUS | MINUS) num                                    # UnaryOp
    | expr INC                                              # PostInc
    | expr DEC                                              # PostDec
    | ID '(' callArgs? ')'                                  # Call
    | '(' expr ')'                                          # Paren
    | INT                                                   # Int
    | ID                                                    # Id
;

lambdaParams: '(' ')'          // 0 args
    | '(' ID (',' ID)? ')'     // 1-2 args con ()
    | ID                       // 1 arg sin ()
;

callArgs: expr (',' expr)* ;

num: INT;

// Lexer (igual)
LET     : 'let';
ASSIGN  : '=';
PRINT   : 'print';
LAMBDA  : '->';

INT : [0-9]+;
POW : '**';
INC : '++';
DEC : '--';
PLUS  : '+';
MINUS  : '-';
MULT  : '*';
DIV  : '/';

ID  : [a-zA-Z_][a-zA-Z0-9_]*;

COMMENT: '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT: '/*' (~[*] | '*' ~[/])* '*/' -> skip;
NEWLINE: '\r'? '\n';
WS: [ \t]+ -> skip;