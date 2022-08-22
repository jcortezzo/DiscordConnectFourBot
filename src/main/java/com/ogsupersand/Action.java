package com.ogsupersand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Action {
    START_GAME("connect4"),
    DROP("drop");

    private String action;
}
