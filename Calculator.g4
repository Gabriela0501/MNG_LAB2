grammar Calculator;
 
expression: multiplyingExpression ((PLUS | MINUS) multiplyingExpression)*;
multiplyingExpression: integralExpression ((MULT | DIV) multiplyingExpression)*;
integralExpression: MINUS INT | INT;
 
INT: [0-9]+ ;
PLUS: '+' ;
MINUS: '-' ;
MULT: '*';
DIV: '/';
INTEGRAL: 'cal';
WS : [ \t\r\n]+ -> skip ;
