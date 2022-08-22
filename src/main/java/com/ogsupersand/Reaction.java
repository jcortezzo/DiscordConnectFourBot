package com.ogsupersand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Reaction {
    ONE("1️⃣", 1),
    TWO("2️⃣", 2),
    THREE("3️⃣", 3),
    FOUR("4️⃣", 4),
    FIVE("5️⃣", 5),
    SIX("6️⃣",6),
    SEVEN("7️⃣",7);
    
    private String symbol;
    private int column;
}