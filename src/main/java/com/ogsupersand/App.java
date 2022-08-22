package com.ogsupersand;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private static final String DISCORD_CONFIG_PATH = "config.json";
    private static final String BOT_TOKEN_NAME = "botToken";

    public static JDA jda;

    public static void main(String[] args)
    {
        try {
            JSONObject config = (JSONObject) new JSONParser().parse(new FileReader(DISCORD_CONFIG_PATH));
            String token = (String) config.get(BOT_TOKEN_NAME);
            jda = JDABuilder.createDefault(token)
                    .addEventListeners(new ConnectFourListener())
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static User getUser() {
        return jda == null ? null : jda.getSelfUser();
    }
}
