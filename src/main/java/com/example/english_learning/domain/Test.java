package com.example.english_learning.domain;

import java.util.Scanner;
import java.util.Set;

import static java.lang.System.out;

public class Test {
    private static final Set<String> OPERATION_SET = Set.of("*", "/", "+", "-");
    private static final String INCORRECT_INPUT = "некорретный ввод";

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            out.println("хуярь пример");
            String[] customerInput = scanCustomerInput();
            String[] validatedInput = validateCustomerInput(customerInput);
            if (validatedInput == null) {
                continue;
            }
            int result = calculate(customerInput);
            out.println(result);
        }
    }

    static int calculate(String[] input) {
        int firstOperand = Integer.parseInt(input[0]);
        String operator = input[1];
        int secondOperand = Integer.parseInt(input[2]);
        int result;
        switch (operator) {
            case "*" -> result = firstOperand * secondOperand;
            case "/" -> result = firstOperand / secondOperand;
            case "+" -> result = firstOperand + secondOperand;
            case "-" -> result = firstOperand - secondOperand;
            default -> result = 0;
        }
        return result;
    }

    static String[] validateCustomerInput(String[] input) {
        if (input.length < 3) {
            out.println(INCORRECT_INPUT);
            return null;
        }
        if (!OPERATION_SET.contains(input[1])) {
            out.println(INCORRECT_INPUT);
            return null;
        }
        if ((!validateDigit(input[0]) || !validateDigit(input[2]))) {
            out.println(INCORRECT_INPUT);
            return null;
        }
        return input;
    }

    static String[] scanCustomerInput() {
        return new Scanner(System.in).nextLine().split(" ");
    }

    static boolean validateDigit(String digit) {
        try {
            Integer.parseInt(digit);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
