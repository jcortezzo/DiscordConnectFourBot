package com.ogsupersand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * Hello world!
 *
 */
public class App 
{  
    private static final String BOT_TOKEN_NAME = "ACCESS_TOKEN";

    public static JDA jda;

    public static void main(String[] args)
    {
        try {
            String token = System.getenv(BOT_TOKEN_NAME);
            jda = JDABuilder.createDefault(token)
                    .addEventListeners(new ConnectFourListener())
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static User getUser() {
        return jda == null ? null : jda.getSelfUser();
    }
}
