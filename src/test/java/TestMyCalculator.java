import Calculator.MyCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TestMyCalculator {

    private MyCalculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new MyCalculator();
    }

    /**
     * Тестирование вычисления простого выражения без переменных.
     */
    @Test
    public void testEvaluateSimpleExpression() throws Exception {
        double result = calculator.evaluate("3 + 5 * 2");
        assertEquals(13.0, result, "Выражение должно быть корректно вычислено.");
    }

    /**
     * Тестирование вычисления выражения с использованием скобок.
     */
    @Test
    public void testEvaluateWithParentheses() throws Exception {
        double result = calculator.evaluate("(3 + 5) * 2");
        assertEquals(16.0, result, "Выражение со скобками должно быть корректно вычислено.");
    }

    /**
     * Тестирование обработки деления на ноль.
     */
    @Test
    public void testDivisionByZero() {
        Exception exception = assertThrows(ArithmeticException.class, () -> {
            calculator.evaluate("10 / 0");
        });
        assertEquals("Деление на ноль.", exception.getMessage(), "Должна быть выброшена ошибка деления на ноль.");
    }

    /**
     * Тестирование использования переменных.
     */
    @Test
    public void testVariableUsage() throws Exception {
        // Подготовка фиксированного ввода для Scanner
        String input = "7.0\n"; // Значение для переменной x
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        double result = calculator.evaluate("x + 3");
        assertEquals(10.0, result, "Переменная должна быть корректно заменена значением.");
    }

    /**
     * Тестирование использования математических функций (sin, cos, sqrt).
     */
    @Test
    public void testMathFunctions() throws Exception {
        double sinResult = calculator.evaluate("sin(0)");
        assertEquals(0.0, sinResult, 0.0001, "Функция sin(0) должна вернуть 0.");

        double cosResult = calculator.evaluate("cos(0)");
        assertEquals(1.0, cosResult, 0.0001, "Функция cos(0) должна вернуть 1.");

        double sqrtResult = calculator.evaluate("sqrt(16)");
        assertEquals(4.0, sqrtResult, 0.0001, "Функция sqrt(16) должна вернуть 4.");
    }

    /**
     * Тестирование обработки несбалансированных скобок.
     */
    @Test
    public void testUnbalancedParentheses() {
        Exception exception = assertThrows(Exception.class, () -> {
            calculator.evaluate("(3 + 5 * 2");
        });
        assertEquals("Несбалансированные скобки в выражении.", exception.getMessage(),
                "Должна быть выброшена ошибка о несбалансированных скобках.");
    }

    /**
     * Тестирование обработки пустого выражения.
     */
    @Test
    public void testEmptyExpression() {
        Exception exception = assertThrows(Exception.class, () -> {
            calculator.evaluate("");
        });
        assertEquals("Выражение не может быть пустым.", exception.getMessage(),
                "Должна быть выброшена ошибка о пустом выражении.");
    }

    /**
     * Тестирование обработки недопустимых символов.
     */
    @Test
    public void testInvalidCharacters() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.evaluate("3 + @");
        });
        assertEquals("Недопустимый символ: @", exception.getMessage(),
                "Должна быть выброшена ошибка о недопустимом символе.");
    }

    /**
     * Тестирование обработки некорректного использования функции.
     */
    @Test
    public void testInvalidFunctionUsage() {
        Exception exception = assertThrows(Exception.class, () -> {
            calculator.evaluate("sin 0");
        });
        assertEquals("Функция sin должна быть использована с круглыми скобками.", exception.getMessage(),
                "Должна быть выброшена ошибка о некорректном использовании функции.");
    }
}