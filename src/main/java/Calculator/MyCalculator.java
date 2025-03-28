package Calculator;
import java.util.*;

/**
 * Класс MyCalculator предоставляет функциональность для вычисления математических выражений,
 * заданных в виде строки. Поддерживает операции сложения, вычитания, умножения, деления,
 * а также использование переменных и математических функций (sin, cos, sqrt).
 */
public class MyCalculator {

    private Map<String, Double> variables = new HashMap<>();

    /**
     * Вычисляет значение математического выражения, заданного в виде строки.
     * Метод выполняет проверку корректности входных данных, разбивает выражение на токены,
     * и производит вычисления с учетом приоритета операций и скобок.
     *
     * @param expression Строка, содержащая математическое выражение.
     *                   Может включать числа, переменные, операторы (+, -, *, /),
     *                   математические функции (sin, cos, sqrt) и скобки.
     * @return Результат вычисления выражения.
     * @throws Exception Если выражение некорректно: пустое, содержит несбалансированные скобки,
     *                   недопустимые символы или другие ошибки.
     */
    public double evaluate(String expression) throws Exception {

        if (expression == null || expression.trim().isEmpty()) {
            throw new Exception("Выражение не может быть пустым.");
        }

        if (!areBracketsBalanced(expression)) {
            throw new Exception("Несбалансированные скобки в выражении.");
        }

        List<String> tokens = tokenize(expression);
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (isNumber(token)) {
                values.push(Double.parseDouble(token));
            } else if (isVariable(token)) {
                double value = getVariableValue(token);
                values.push(value);
            } else if (isFunction(token)) {
                if (i + 1 >= tokens.size() || !tokens.get(i + 1).equals("(")) {
                    throw new Exception("Функция " + token + " должна быть использована с круглыми скобками.");
                }
                operators.push(token);
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    applyPendingOperation(values, operators);
                }
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push("(");
            } else if (token.equals(")")) {
                resolveParentheses(values, operators);
            } else {
                throw new Exception("Неверный токен: " + token);
            }
        }

        while (!operators.isEmpty()) {
            applyPendingOperation(values, operators);
        }

        if (values.size() != 1) {
            throw new Exception("Некорректное выражение.");
        }

        return values.pop();
    }

    /**
     * Применяет операцию из стека операторов к двум последним значениям в стеке значений.
     *
     * @param values    Стек значений, содержащий операнды.
     * @param operators Стек операторов, содержащий математические операции.
     * @throws Exception Если в стеках недостаточно операндов или операторов для выполнения операции.
     */
    private void applyPendingOperation(Stack<Double> values, Stack<String> operators) throws Exception {
        if (values.size() < 2 || operators.isEmpty()) {
            throw new Exception("Недостаточно операндов для операции.");
        }
        String operator = operators.pop();
        double b = values.pop();
        double a = values.pop();
        values.push(applyOperation(operator, b, a));
    }

    /**
     * Обрабатывает закрывающую скобку, выполняя все операции внутри скобок
     * и применяя функции, если они указаны перед скобками.
     *
     * @param values    Стек значений, содержащий операнды.
     * @param operators Стек операторов, содержащий математические операции и функции.
     * @throws Exception Если скобки несбалансированы или возникают ошибки при вычислении.
     */
    private void resolveParentheses(Stack<Double> values, Stack<String> operators) throws Exception {
        while (!operators.isEmpty() && !operators.peek().equals("(")) {
            applyPendingOperation(values, operators);
        }
        if (operators.isEmpty()) {
            throw new Exception("Несбалансированные скобки.");
        }
        operators.pop();

        if (!operators.isEmpty() && isFunction(operators.peek())) {
            String func = operators.pop();
            double arg = values.pop();
            values.push(applyFunction(func, arg));
        }
    }

    /**
     * Разбивает строку выражения на токены (числа, операторы, переменные, функции, скобки).
     *
     * @param expression Строка, содержащая математическое выражение.
     * @return Список токенов, на которые разбито выражение.
     * @throws IllegalArgumentException Если в выражении содержится недопустимый символ.
     */
    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                currentToken.append(c);
            } else if (Character.isLetter(c)) {
                currentToken.append(c);
            } else if ("+-*/()".indexOf(c) != -1) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                tokens.add(Character.toString(c));
            } else if (Character.isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else {
                throw new IllegalArgumentException("Недопустимый символ: " + c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    /**
     * Проверяет, является ли токен числом.
     *
     * @param token Токен для проверки.
     * @return true, если токен является числом; иначе false.
     */
    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Проверяет, является ли токен переменной.
     *
     * @param token Токен для проверки.
     * @return true, если токен является переменной; иначе false.
     */
    private boolean isVariable(String token) {
        return Character.isLetter(token.charAt(0)) && !isFunction(token);
    }

    /**
     * Проверяет, является ли токен математической функцией (sin, cos, sqrt).
     *
     * @param token Токен для проверки.
     * @return true, если токен является функцией; иначе false.
     */
    private boolean isFunction(String token) {
        return token.equals("sin") || token.equals("cos") || token.equals("sqrt");
    }

    /**
     * Проверяет, является ли токен оператором (+, —, *, /).
     *
     * @param token Токен для проверки.
     * @return true, если токен является оператором; иначе false.
     */
    private boolean isOperator(String token) {
        return "+-*/".indexOf(token) != -1;
    }

    /**
     * Возвращает значение переменной. Если переменная не определена,
     * запрашивает её значение у пользователя через консоль.
     *
     * @param variable Имя переменной.
     * @return Значение переменной.
     * @throws Exception Если пользователь вводит некорректное значение для переменной.
     */
    private double getVariableValue(String variable) throws Exception {
        if (variables.containsKey(variable)) {
            return variables.get(variable);
        }

        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        System.out.println("Введите значение для переменной " + variable + ":");
        try {
            double value = scanner.nextDouble();
            variables.put(variable, value);
            return value;
        } catch (InputMismatchException e) {
            throw new Exception("Неверное значение для переменной " + variable);
        }
    }

    /**
     * Определяет приоритет оператора.
     *
     * @param operator Оператор (+, -, *, /).
     * @return Приоритет оператора (1 для + и -, 2 для * и /).
     */
    private int precedence(String operator) {
        if (operator.equals("+") || operator.equals("-")) {
            return 1;
        } else if (operator.equals("*") || operator.equals("/")) {
            return 2;
        }
        return 0;
    }

    /**
     * Применяет математическую операцию к двум операндам.
     *
     * @param operator Оператор (+, -, *, /).
     * @param b        Второй операнд.
     * @param a        Первый операнд.
     * @return Результат применения операции.
     * @throws Exception Если происходит деление на ноль или оператор неизвестен.
     */
    private double applyOperation(String operator, double b, double a) throws Exception {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("Деление на ноль.");
                return a / b;
            default:
                throw new IllegalArgumentException("Неизвестная операция: " + operator);
        }
    }

    /**
     * Применяет математическую функцию к аргументу.
     *
     * @param function Функция (sin, cos, sqrt).
     * @param x Аргумент функции.
     * @return Результат применения функции.
     * @throws ArithmeticException Если функция не определена для данного аргумента (например, sqrt отрицательного числа).
     */
    private double applyFunction(String function, double x) {
        switch (function) {
            case "sin":
                return Math.sin(x);
            case "cos":
                return Math.cos(x);
            case "sqrt":
                if (x < 0) throw new ArithmeticException("Квадратный корень отрицательного числа не определен.");
                return Math.sqrt(x);
            default:
                throw new IllegalArgumentException("Неизвестная функция: " + function);
        }
    }

    /**
     * Проверяет, сбалансированы ли скобки в выражении.
     *
     * @param expression Строка, содержащая математическое выражение.
     * @return true, если скобки сбалансированы; иначе false.
     */
    private boolean areBracketsBalanced(String expression) {
        int balance = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false;
        }
        return balance == 0;
    }
}