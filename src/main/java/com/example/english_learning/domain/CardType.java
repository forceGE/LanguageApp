package com.example.english_learning.domain;

import java.util.Map;

public enum CardType {
    TEXT, TEST, LANGUAGE;

    private final static Map<String, CardType> CARD_TYPE_MATCHER = Map.of(
            "Language", LANGUAGE,
            "Test", TEST,
            "Text answer", TEXT
    );

    public static CardType defaultType() {
        return LANGUAGE;
    }

    public static CardType getByPreview(String str) {
        return CARD_TYPE_MATCHER.get(str);
    }
}