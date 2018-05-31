# L-Compiler
A linguagem “L” é uma linguagem imperativa simplificada, com características do C e Pascal.

## Alfabeto
| A | B | C | D | E | F | G | H | I | J | K | L | M | N | O | P | Q | R | S | T | U | V | W | X | Y | Z |<br>
| a | b | c | d | e | f | g | h | i | j | k | l | m | n | o | p | q | r | s | t | u | v | w | x | y | z |<br>
| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | ' ' | _ | . | , | ; | & | : | ( | \) | [ | \] | \{ | \} | + | - | " | ' |<br>
| / | % | ^ | @ | ! | ? | > | < | = | \n | \t <br>

## Tokens
| Tokem | Lexema |
| --- | --- |
| AND | and |
| ATTR | <- |
| BEGIN | begin |
| CHAR | char |
| CLOSE_BRACKET | ] |
| CLOSE_PARENTHESIS | ) |
| COMMA | , |
| CONST | d+ U 0DDh U '(alfabeto)' U "(alfabeto)\*\" <br>onde D = (0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,A,B,C,D,E,F)
| DIFFERENT | <> |
| DIVIDE | / |
| DO | do |
| ELSE | else |
| END | end |
| EOF |  |
| EQUAL | = |
| FINAL | final |
| FOR | for |
| GREATHER | > |
| GREATHER_EQUAL | >= |
| ID | L( L\|d\|\_ )\* U \_(\_)\*( L\|d )( L\|d\|\_ )* |
| IF | if |
| INT | int |
| LESS | < |
| LESS_EQUALS | <= |
| MINUS | - |
| MULTIPLY | \* |
| NOT | not |
| OPEN_BRACKET | [ |
| OPEN_PARENTHESIS | ( |
| OR | or |
| MOD | % |
| READLN | readln |
| SEMICOLON | ; |
| STEP | step |
| SUM | + |
| THEN | then |
| TO | to |
| WRITE | write |
| WRITELN | writeln |

## Gramática

O primeiro símbolo da gramática é o S

```
S -> {VARIAVEL|CONSTANTE} { COMANDO }

VARIAVEL -> (int|char) id[ "["const"]" | ATRIBUICAO ] { ,id[ "["const"]" | ATRIBUICAO] };
ATRIBUICAO -> <- [+|-] const
CONSTANTE -> final id = [+|-] const;

COMANDO ->
           for id <- EXP to EXP [step const] do BLOCO |
           if EXP then BLOCO [else BLOCO] |
           readln "(" id[ "[" EXP "]" ] ")"; |
           write "(" EXP {,EXP} ")"; |
           writeln "(" EXP{ ,EXP} ")"; |
           id <- EXP; |
           ;

BLOCO -> begin { COMANDO } end; | COMANDO

EXP  -> EXPS[ (<|>|<=|>=|=|<>)EXPS ]
EXPS -> [+|-] T{ (+|-|or)T }
T    -> F{ (*|/|%|and)F }
F    -> "("EXP")" | not F | const | id[ "["EXP"]" ]
```
