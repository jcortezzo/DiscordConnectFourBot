package com.ogsupersand;

public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException() {
        super();
    }

    public InvalidMoveException(String msg) {
        super(msg);
    }
}
