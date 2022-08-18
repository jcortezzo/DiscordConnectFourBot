package com.ogsupersand;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point() {}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            Point other = (Point) obj;
            return this.x == other.x && this.y == other.y;
        }
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", this.x, this.y);
    }

    @Override
    public int hashCode() {
        // https://stackoverflow.com/questions/9135759/java-hashcode-for-a-point-class
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
