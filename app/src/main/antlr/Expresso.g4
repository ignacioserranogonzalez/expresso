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
expr: <assoc=right> expr '?' expr ':' expr  # TernaryCondition
    | (ID (',' ID)?)? LAMBDA expr           # Lambda
    | expr (PLUS | MINUS) expr              # AddSub
    | expr (MULT | DIV) expr                # MultDiv
    | <assoc=right> expr POW expr           # Pow
    | (PLUS | MINUS) num                    # UnaryOp
    | expr INC                              # PostInc
    | expr DEC                              # PostDec
    | ID '(' expr ')'                       # Call
    | '(' expr ')'                          # Paren
    | INT                                   # Int
    | ID                                    # Id
;

num: INT;

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