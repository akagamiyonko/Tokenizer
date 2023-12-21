import java.util.HashMap;
import java.util.Map;

public class Tokenizer {
    private Map<String, Integer> variables;

    public Tokenizer() {
        variables = new HashMap<>();
    }

    public void tokenize(String program) {
        String[] statements = program.split(";");
        for (String statement : statements) {
            statement = statement.trim();
            if (!statement.isEmpty()) {
                try {
                    evaluateStatement(statement);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            }
        }

        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    private void evaluateStatement(String statement) {
        String[] parts = statement.split("=");
        if (parts.length != 2) {
            throw new SyntaxErrorException("Syntax error in statement");
        }

        String identifier = parts[0].trim();
        String expression = parts[1].trim();

        if (!isValidIdentifier(identifier)) {
            throw new SyntaxErrorException("Invalid identifier: " + identifier);
        }

        if (variables.containsKey(identifier) && variables.get(identifier) == null) {
            throw new UninitializedVariableException("Variable " + identifier + " is uninitialized");
        }

        int value = evaluateExpression(expression);
        variables.put(identifier, value);
    }

    private boolean isValidIdentifier(String identifier) {
        return identifier.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    private int evaluateExpression(String expression) {
        return evaluateExp(expression);
    }

    private int evaluateExp(String exp) {
        String[] terms = exp.split("\\+");
        int result = evaluateTerm(terms[0]);

        for (int i = 1; i < terms.length; i++) {
            if (!terms[i].trim().isEmpty()) {
                result += evaluateTerm(terms[i]);
            }
        }

        return result;
    }

    private int evaluateTerm(String term) {
        String[] factors = term.split("\\*");
        int result = evaluateFact(factors[0]);

        for (int i = 1; i < factors.length; i++) {
            if (!factors[i].trim().isEmpty()) {
                result *= evaluateFact(factors[i]);
            }
        }

        return result;
    }

    private int evaluateFact(String fact) {
        fact = fact.trim();

        if (fact.startsWith("(") && fact.endsWith(")")) {
            return evaluateExp(fact.substring(1, fact.length() - 1));
        }

        if (fact.startsWith("-")) {
            return -evaluateFact(fact.substring(1));
        }

        if (fact.matches("\\d+")) {
            return Integer.parseInt(fact);
        }

        if (variables.containsKey(fact)) {
            if (variables.get(fact) == null) {
                throw new UninitializedVariableException("Variable " + fact + " is uninitialized");
            }
            return variables.get(fact);
        }

        throw new InvalidExpressionException("Invalid expression: " + fact);
    }

    public static void main(String[] args) {
        Tokenizer tokenizer = new Tokenizer();
        String[] programs = {
                "x = 001;",
                "x_2 = 0;",
                "x = 0; y = x; z = ---(x+y);",
                "x = 1; y = 2; z = ---(x+y)*(x+-y);"
        };

        for (int i = 0; i < programs.length; i++) {
            System.out.println("Input " + (i + 1));
            System.out.println(programs[i]);
            System.out.println("Output " + (i + 1));
            tokenizer.tokenize(programs[i]);
            System.out.println();
        }
    }

    static class SyntaxErrorException extends RuntimeException {
        public SyntaxErrorException(String message) {
            super(message);
        }
    }

    static class UninitializedVariableException extends RuntimeException {
        public UninitializedVariableException(String message) {
            super(message);
        }
    }

    static class InvalidExpressionException extends RuntimeException {
        public InvalidExpressionException(String message) {
            super(message);
        }
    }
}
