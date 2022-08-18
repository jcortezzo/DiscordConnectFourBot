package com.ogsupersand;

public enum Action {
    START_GAME("connect4"),
    DROP("drop");

    private String action;

    private Action(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}
