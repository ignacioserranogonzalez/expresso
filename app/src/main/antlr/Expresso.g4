grammar Expresso;

program: stat* EOF; 

// statements
stat: NEWLINE                        # blank
    | expr NEWLINE                   # expression
    | LET ID ASSIGN expr NEWLINE     # letDecl
    | PRINT '(' expr ')' NEWLINE     # print
    | COMMENT NEWLINE                # comment
    | MULTILINE_COMMENT NEWLINE      # multilineComment
;

// expressions
expr: expr POW expr                  # Pow
    | expr (MULT | DIV) expr         # MultDiv
    | expr (PLUS | MINUS) expr       # AddSub
    | expr INC                       # PostInc
    | expr DEC                       # PostDec
    | (PLUS | MINUS) num             # UnaryOp
    | ID '(' expr ')'                # Call
    | '(' expr ')'                   # Paren
    | ID LAMBDA expr                 # Lambda
    | INT                            # Int
    | ID                             # Id
;

num: INT
    | FLOAT;

// Lexer
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