package org.example;
import java.util.Scanner;
import Calculator.MyCalculator;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MyCalculator calculator = new MyCalculator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите математическое выражение:");
        String expression = scanner.nextLine();

        try {
            double result = calculator.evaluate(expression);
            System.out.println("Результат: " + result);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        scanner.close();
    }
}