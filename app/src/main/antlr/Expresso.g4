grammar Expresso;

// Punto de entrada: cero o más sentencias terminadas por NEWLINE
prog: stat* EOF;

// Una sentencia puede ser: expresión, declaración let, línea vacía
stat: expr NEWLINE          # printExpr
    | LET ID '=' expr NEWLINE # letDecl
    | NEWLINE               # blank
;

// Expresiones con precedencia, unario '-', lambdas y variables
expr: '-' expr                              # unaryMinus
    | expr op=('*'|'/') expr               # MulDiv
    | expr op=('+'|'-') expr               # AddSub
    | <assoc=right> expr '->' expr         # lambdaExpr
    | LAMBDA ID '->' expr                  # lambdaDef
    | LET ID '=' expr IN expr              # letInExpr
    | expr expr                            # application
    | INT                                  # int
    | ID                                   # id
    | '(' expr ')'                         # parens
;

// PALABRAS RESERVADAS
LET: 'let';
IN: 'in';
LAMBDA: 'lambda' | 'λ';  // Soporte para ambas notaciones

// IDENTIFICADORES (variables)
ID: [a-zA-Z_][a-zA-Z0-9_]*;

// LEXER BÁSICO
INT: [0-9]+;
NEWLINE: '\r'? '\n';
WS: [ \t]+ -> skip;
