grammar Expresso;

prog: stat* EOF;

stat: letStmt NEWLINE
    | printStmt NEWLINE
    | NEWLINE
    ;

letStmt: 'let' ID '=' expr;
printStmt: 'print' expr;

expr: expr ('+' | '-') expr
    | expr ('*' | '/') expr
    | INT
    | ID
    | '(' expr ')'
    ;

ID: [a-zA-Z_][a-zA-Z_0-9]*;
INT: [0-9]+;
NEWLINE: '\r'? '\n';
WS: [ \t]+ -> skip;