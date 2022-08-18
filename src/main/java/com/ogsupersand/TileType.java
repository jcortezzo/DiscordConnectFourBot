package com.ogsupersand;

import lombok.Getter;

@Getter
public enum TileType {
    EMPTY("🔵"),
    RED("🔴"),
    YELLOW("🟡");

    private String symbol;

    private TileType(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }
}
