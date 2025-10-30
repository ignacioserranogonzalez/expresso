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
expr
    : NOT expr                                      # NotExpr          // !expr
    | <assoc=right> expr POW expr                   # Pow              // x ** y
    | expr (MULT | DIV) expr                        # MultDiv          // * /
    | expr (PLUS | MINUS) expr                      # AddSub           // + -
    | expr (LT | LE | GT | GE) expr                 # RelOp            // < <= > >=
    | expr (EQ | NE) expr                           # RelOp            // == !=
    | expr AND expr                                 # AndOp            // &&
    | expr OR expr                                  # OrOp              // ||
    | <assoc=right> expr '?' expr ':' expr          # TernaryCondition // ?:
    | (PLUS | MINUS) expr                           # UnaryOp          // +expr, -expr
    | expr (MINUS MINUS | PLUS PLUS)                # PostOp           // x++, x--
    | 'match' expr 'with' NEWLINE* matchRule+       # Match            // match ...
    | lambdaParams LAMBDA expr                      # Lambda           // x -> ...
    | ID '(' argList? ')'                           # Call             // f(1, 2)
    | '^' constructorExpr                           # ConstructorInvocation // ^Cons(1, ^Nil)
    | PRINT '(' expr ')'                            # PrintExpr        // print("hi")
    | '(' expr ')'                                  # Paren             // (expr)
    | '(' expr (',' expr)+ ')'                      # TupleLiteral     // (1, 2)
    | INT                                           # Int              // 666
    | FLOAT                                         # Float            // 3.14
    | BOOLEAN                                       # Boolean          // true / false
    | STRING                                        # String           // "hello"
    | NONE                                          # None             // none
    | ID                                            # Id               // x
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

// operadores relacionales y booleanos
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