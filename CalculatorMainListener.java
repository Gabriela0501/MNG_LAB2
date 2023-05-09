import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;

public class CalculatorMainListener extends CalculatorBaseListener{

    Deque<Integer> numbers = new ArrayDeque<>();
    @Override
    public void enterExpression(CalculatorParser.ExpressionContext ctx) {
        System.out.println("enterExpression:" + ctx.getText());
        super.enterExpression(ctx);
    }
    public void exitExpression(CalculatorParser.AddExpressionContext ctx) {
        System.out.println("exitExpression:" + ctx.getText());
        Integer value = numbers.pop();
        double value2;
        for (int i = 1; i < ctx.getChildCount(); i=i+2){
            String operator = ctx.getChild(i).getText();
            if(operator == "+"){
                value = value + numbers.pop();
            }else if(operator == "-"){
                value = value - numbers.pop();
            }else if(operator == "*"){
                value = value * numbers.pop();
            }else if(operator == "/"){
                value = value / numbers.pop();
            }else if(operator == "^"){
                value2 = Math.pow(value,numbers.pop());
            }else if(operator == "sqrt"){
                value2 = Math.pow(value,1/numbers.pop());
            }else{
                throw new IllegalArgumentException("Invalid operator");
            }
            if (ctx.getChild(i+1).getText().equals("-")&& i < ctx.getChildCount()-1) {
                numbers.add(-value);
                value = 0;
            }
        }
        numbers.add(value);
        super.exitAddExpression(ctx);
    }

    @Override
    public void exitMultiplyExpression(CalculatorParser.MultiplyExpressionContext ctx) {
        System.out.println("exitMultiplyExpression: " + ctx.getText());
        List<CalculatorParser.PowerExpressionContext> powerExpressions = ctx.powerExpression();
        Integer value = Integer.parseInt(powerExpressions.get(0).getText());
        for (int i = 1; i < powerExpressions.size(); i++) {
            String operator = ctx.getChild(2*i - 1).getText();
            int number = Integer.parseInt(powerExpressions.get(i).getText());
            switch (operator) {
                case "*":
                    value = value * number;
                    break;
                case "/":
                    value = value / number;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operator: " + operator);
            }

        }
        numbers.add(value);
        super.exitMultiplyExpression(ctx);
    }

    @Override
    public void exitAddExpression(CalculatorParser.AddExpressionContext ctx) {
        System.out.println("exitAddExpression:" + ctx.getText());
        Integer result = 0;
        int znak = 1;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof CalculatorParser.MultiplyExpressionContext) {
                Integer value = numbers.pop();
                result = result + znak * value;
            } else if (child.getText().equals("+")) {
                znak = 1;
            } else if (child.getText().equals("-")) {
                znak = -1;
            }
        }
        numbers.add(result);
        super.exitAddExpression(ctx);
    }
    //Kod zakomentowany jest to kod, który dotyczy obliczania potęg i pierwiastków, ale niestety coś w nim
    //nie działa i przez to psuje się cały program, błąd jaki wyskakuje to Stack is empty :) niestety
    //po wielu probach zmian nadal nie działa
    @Override
    public void exitSqrtExpression(CalculatorParser.SqrtExpressionContext ctx) {
        System.out.println("exitSqrtExpression:" + ctx.getText());
        if (numbers.isEmpty()) {

            throw new IllegalArgumentException("Stack is empty!");
        }
        else if (numbers.peek() < 0) {
            throw new IllegalArgumentException("Cannot take the square root of a negative number");
        }
        else {
            double value = Math.sqrt(numbers.pop());
            numbers.push((int) value);
        }
        super.exitSqrtExpression(ctx);
    }

    @Override
    public void exitPowerExpression(CalculatorParser.PowerExpressionContext ctx) {
        System.out.println("exitPowerExpression:" + ctx.getText());
        List<CalculatorParser.SqrtExpressionContext> sqrtExpressions = ctx.sqrtExpression();
        double result = Double.parseDouble(sqrtExpressions.get(0).getText());
        for (int i = 1; i < sqrtExpressions.size(); i++) {
            String operator = ctx.getChild(2*i - 1).getText();
            double number = Double.parseDouble(sqrtExpressions.get(i).getText());
            switch (operator) {
                case "^":
                    result = Math.pow(result, number);
                    break;
                case "sqrt":
                    result = Math.pow(result, 1 / number);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operator: " + operator);
            }
        }
        numbers.push((int) result);
        super.exitPowerExpression(ctx);
    }

    public static void main(String[] args) throws Exception {
        //CharStream charStreams = CharStreams.fromFileName("./example.txt");
        Integer result = calc("4 + 22 / 2 * 3");
        System.out.println("Result = " + result);
    }

    public static Integer calc(String expression) {
        return calc(CharStreams.fromString(expression));
    }

    public static Integer calc(CharStream charStream) {
        CalculatorLexer lexer = new CalculatorLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CalculatorParser parser = new CalculatorParser(tokens);
        ParseTree tree = parser.expression();
        ParseTreeWalker walker = new ParseTreeWalker();
        CalculatorMainListener mainListener = new CalculatorMainListener();
        walker.walk(mainListener, tree);
        return mainListener.getResult();
    }
    private Integer getResult() {
        return numbers.peek();
    }
}