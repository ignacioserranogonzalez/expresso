grammar Expresso;
import ExpressoTypes;

program: stat* EOF; 

// statements
stat: NEWLINE                                                               # blank
    | LET ID (':' type)? ASSIGN expr NEWLINE                                # letDecl
    | FUN ID '(' paramList? ')' ':' type '=' expr NEWLINE                   # funDecl
    | DATA ID ASSIGN '{' NEWLINE* constructorList NEWLINE* '}' NEWLINE      # dataDecl
    | expr NEWLINE                                                          # expression
;

constructorList: constructor (',' NEWLINE* constructor)*;

constructor: ID arguments?;

arguments: '(' argument (',' argument)* ')';

argument: (ID ':')? type;

// expressions
expr
    : NOT expr                                      # NotExpr
    | expr ':' type                                 # CastExpr 
    | <assoc=right> expr POW expr                   # Pow
    | expr (MULT | DIV) expr                        # MultDiv
    | expr (PLUS | MINUS) expr                      # AddSub
    | expr (LT | LE | GT | GE) expr                 # RelOp
    | expr (EQ | NE) expr                           # RelOp
    | expr (AND | OR) expr                          # LogicalOp
    | <assoc=right> expr '?' expr ':' expr          # TernaryCondition
    | (PLUS | MINUS) expr                           # UnaryOp
    | expr (MINUS MINUS | PLUS PLUS)                # PostOp
    | 'match' expr 'with' NEWLINE* matchRule+       # Match
    | lambdaParams LAMBDA expr                      # Lambda
    | expr '(' argList? ')'                         # CallChain
    | ID '(' argList? ')'                           # Call
    | PRINT '(' expr ')'                            # Print
    | '^' constructorExpr                           # ConstructorInvocation
    | '(' expr ')'                                  # Paren
    | '(' expr (',' expr)+ ')'                      # TupleLiteral
    | INT                                           # Int
    | FLOAT                                         # Float
    | BOOLEAN                                       # Boolean
    | STRING                                        # String
    | NONE                                          # None
    | ID                                            # Id
    ;

matchRule: pattern ('if' expr)? '->' expr NEWLINE+;

pattern
    : data_pattern
    | native_pattern
;

data_pattern
    : ID ('(' pattern (',' pattern)* ')')?  # DataOrVariablePattern
;

native_pattern
    : '_'             # WildcardPattern
    | 'none'          # NonePattern
    | INT             # IntPattern
    | STRING          # StringPattern
    | BOOLEAN         # BooleanPattern
;

lambdaParams
    : '(' paramList? ')'   # LambdaParamList
    | param                # SingleLambdaParam  
    ;

paramList: param (',' param)*;

param: ID (':' type)?;

constructorExpr: ID ('(' argList ')')?;

argList: expr (',' expr)*;

// Lexer
LET     : 'let';
DATA    : 'data';
ASSIGN  : '=';
PRINT   : 'print';
FUN     : 'fun';
LAMBDA  : '->';
NONE    : 'none';

INT     : [0-9]+;
FLOAT   : [0-9]+ '.' [0-9]* | '.' [0-9]+;
BOOLEAN : 'true' | 'false';
STRING  : '"' (ESC | ~["\\])* '"';

fragment ESC : '\\' (["\\/bfnrt] | UNICODE);
fragment UNICODE : 'u' HEX HEX HEX HEX;
fragment HEX : [0-9a-fA-F];

POW     : '**';
PLUS    : '+';
MINUS   : '-';
MULT    : '*';
DIV     : '/';

ID: [a-zA-Z_][a-zA-Z0-9_]*;

// operadores relacionales y logicos
LT      : '<';
LE      : '<=';
GT      : '>';
GE      : '>=';
EQ      : '==';
NE      : '!=';
AND     : '&&';
OR      : '||';
NOT     : '!';

COMMENT: '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT: '/*' (~[*] | '*' ~[/])* '*/' -> skip;
NEWLINE: '\r'? '\n';
WS: [ \t]+ -> skip;