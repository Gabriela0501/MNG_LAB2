grammar Calculator;

expression: addExpression;
addExpression: multiplyExpression ((PLUS | MINUS) multiplyExpression)*;
multiplyExpression: powerExpression ((MULT | DIV) powerExpression)*;
powerExpression: sqrtExpression (POWER sqrtExpression)?;
sqrtExpression: atom (SQRT atom)?;
atom: INT;

INT: [0-9]+;
PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
POWER: '^';
SQRT: 'sqrt';
WS: [ \t\r\n]+ -> skip;
