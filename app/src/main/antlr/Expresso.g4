grammar Expresso;

program: stat* EOF; 

// statements
stat: NEWLINE                        # blank
    | expr (COMMENT)? NEWLINE        # expression
    | LET ID ASSIGN expr (COMMENT)? NEWLINE     # letDecl
    | PRINT '(' expr ')' (COMMENT)? NEWLINE     # print
    | COMMENT NEWLINE                # comment
    | MULTILINE_COMMENT NEWLINE      # multilineComment
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

COMMENT: '//' ~[\r\n]*;
MULTILINE_COMMENT: '/*' (~[*] | '*' ~[/])* '*/';
NEWLINE: '\r'? '\n';
WS: [ \t]+ -> skip;