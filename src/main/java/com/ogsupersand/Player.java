package com.ogsupersand;

import lombok.Getter;
import net.dv8tion.jda.api.entities.User;

@Getter
public class Player {
    private User discordUser;
    private TileType tileType;

    public Player(User discordUser, TileType tileType) {
        this.discordUser = discordUser;
        this.tileType = tileType;
    }

    public String getMention() {
        return discordUser.getAsMention();
    }
}
