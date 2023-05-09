import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;

public class CalculatorMainListener extends CalculatorBaseListener{

    Deque<Double> numbers = new ArrayDeque<>();
    @Override
    public void enterExpression(CalculatorParser.ExpressionContext ctx) {
        System.out.println("enterExpression:" + ctx.getText());
        super.enterExpression(ctx);
    }
    public void exitExpression(CalculatorParser.AddExpressionContext ctx) {
        double right = numbers.pop();
        double left = numbers.pop();
        double result = 0;
        for(int i = 1;i< ctx.getChildCount();i = i+2)
        if (ctx.getChild(i).getText() == "+") {
            result = left + right;
        } else {
            result = left - right;
        }
        numbers.push(result);
    }

    @Override
    public void exitMultiplyExpression(CalculatorParser.MultiplyExpressionContext ctx) {
        System.out.println("exitMultiplyExpression: " + ctx.getText());
        List<CalculatorParser.PowerExpressionContext> powerExpressions = ctx.powerExpression();
        double value = numbers.pop();
        for (int i = 1; i < powerExpressions.size(); i++) {
            String operator = ctx.getChild(2*i - 1).getText();
            Double number = numbers.pop();
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
        double result = 0;
        int znak = 1;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof CalculatorParser.MultiplyExpressionContext) {
                double value = numbers.pop();
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
            numbers.push(value);
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
        numbers.push(result);
        super.exitPowerExpression(ctx);
    }

    @Override
    public void exitAtom(CalculatorParser.AtomContext ctx) {
        if(ctx.MINUS() !=null){
            numbers.add((-1* Double.valueOf(ctx.INT().toString())));
        }else{
            numbers.add((Double.valueOf(ctx.INT().toString())));
        }
        super.exitAtom(ctx);
    }

    public static void main(String[] args) throws Exception {
        //CharStream charStreams = CharStreams.fromFileName("./example.txt");
        Double result = calc("6 + 2/3^1/5");
        System.out.println("Result = " + result);
    }

    public static Double calc(String expression) {
        return calc(CharStreams.fromString(expression));
    }

    public static Double calc(CharStream charStream) {
        CalculatorLexer lexer = new CalculatorLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CalculatorParser parser = new CalculatorParser(tokens);
        ParseTree tree = parser.expression();
        ParseTreeWalker walker = new ParseTreeWalker();
        CalculatorMainListener mainListener = new CalculatorMainListener();
        walker.walk(mainListener, tree);
        return mainListener.getResult();
    }
    private Double getResult() {
        return numbers.peek();
    }
}