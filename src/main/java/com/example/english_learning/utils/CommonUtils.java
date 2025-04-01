package com.example.english_learning.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtils {
    private static final String REPLACEMENT = "[\\p{Punct}&&[^']]";
    public boolean equalsByLevenshtein(String question, String answer) {
        var readyQuestion = question.toLowerCase().replaceAll(REPLACEMENT, "");
        var readyAnswer = answer.toLowerCase().replaceAll(REPLACEMENT, "");
        return calculateLevenshtein(readyQuestion, readyAnswer) <= getMaxLevenshteinDistance(readyAnswer);
    }

    private static int calculateLevenshtein(String correctAnswer, String actualAnswer) {
        var m = correctAnswer.length();
        var n = actualAnswer.length();

        if (m == 0) return n;
        if (n == 0) return m;

        int[][] matrix = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) matrix[i][0] = i;
        for (int j = 0; j <= n; j++) matrix[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (correctAnswer.charAt(i - 1) == actualAnswer.charAt(j - 1)) ? 0 : 1;

                matrix[i][j] = Math.min(
                        Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1),
                        matrix[i - 1][j - 1] + cost
                );
            }
        }
        return matrix[m][n];
    }

    private static int getMaxLevenshteinDistance(String s) {
        return s.length() <= 5 ? 0 : (s.length() - 1) / 10 + 1;
    }
}
