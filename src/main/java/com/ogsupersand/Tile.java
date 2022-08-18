package com.ogsupersand;

import lombok.Data;

@Data
public class Tile {
    private TileType type;

    private static int staticHashCode = 0;

    private int hashCode;

    public Tile() {
        this(TileType.EMPTY);
    }

    public Tile(TileType type) {
        setType(type);
        hashCode = staticHashCode++;
    }

    public boolean isEmpty() {
        return this.type == TileType.EMPTY;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
