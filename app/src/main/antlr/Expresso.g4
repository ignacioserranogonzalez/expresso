grammar Expresso;
import ExpressoTypes;

program: stat* EOF; 

// statements
stat: NEWLINE                                                               # blank
    | LET ID (':' type)? ASSIGN expr NEWLINE                                # letDecl
    | PRINT '(' expr ')' NEWLINE                                            # print
    | FUN ID '(' paramList? ')' ':' type '=' expr NEWLINE                   # funDecl
    | DATA ID ASSIGN '{' NEWLINE* constructorList NEWLINE* '}' NEWLINE      # dataDecl
    | expr NEWLINE                                                          # expression
;

paramList: param (',' param)*;

param: ID (':' type)?;

constructorList: constructor (',' NEWLINE* constructor)*;

constructor: ID arguments?;

arguments: '(' argument (',' argument)* ')';

argument: (ID ':')? type;

// expressions
expr: <assoc=right> expr POW expr                    # Pow
    | <assoc=right> expr '?' expr ':' expr           # TernaryCondition
    | 'match' expr 'with' NEWLINE* matchRule+        # Match
    | (PLUS | MINUS) expr                            # UnaryOp
    | expr (MINUS MINUS | PLUS PLUS)                 # PostOp
    | expr (PLUS | MINUS) expr                       # AddSub
    | expr (MULT | DIV) expr                         # MultDiv
    | lambdaParams LAMBDA expr                       # Lambda
    | ID '(' argList? ')'                            # Call
    | '^' constructorExpr                            # ConstructorInvocation
    | PRINT '(' expr ')'                             # PrintExpr
    | '(' expr ')'                                   # Paren
    | '(' expr (',' expr)+ ')'                       # TupleLiteral
    | INT                                            # Int
    | FLOAT                                          # Float
    | BOOLEAN                                        # Boolean
    | STRING                                         # String
    | NONE                                           # None 
    | ID                                             # Id
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
    : '(' ID (',' ID)* ')'   
    | ID                     
    | '(' ')'                
;

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

COMMENT: '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT: '/*' (~[*] | '*' ~[/])* '*/' -> skip;
NEWLINE: '\r'? '\n';
WS: [ \t]+ -> skip;