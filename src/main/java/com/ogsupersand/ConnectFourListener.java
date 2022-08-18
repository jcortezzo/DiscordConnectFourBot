package com.ogsupersand;

import java.util.Scanner;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ConnectFourListener extends ListenerAdapter {

    private static final String COMMAND_TOKEN = "?";

    private ConnectFourGame game;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        String msgString = msg.getContentRaw();
        MessageChannel channel = event.getChannel();

        if (!msgString.startsWith(COMMAND_TOKEN)) {
            return;
        }
        String fullMessage = msgString.substring(COMMAND_TOKEN.length());
        
        Scanner scan = new Scanner(fullMessage.toLowerCase());
        String command = scan.next().toLowerCase();
        System.out.println(fullMessage.toLowerCase());

        System.out.println(command);
        if (command.equals("start")) {
            Player player1 = new Player(event.getAuthor(), TileType.RED);
            long userId = Long.parseLong(scan.next().replaceAll("[^a-zA-Z0-9]", ""));
            User player2User = App.jda.retrieveUserById(userId).complete();
            Player player2 = new Player(player2User, TileType.YELLOW);
            game = new ConnectFourGame(player1, player2);
            game.startGame();
        } else {
            if (command.equals("drop")) {
                int column = scan.nextInt();
                Player winner = game.dropPiece(column);
                if (winner != null) {
                    sendMessage(channel, String.format("%s wins!", winner.getMention()));
                }
            }
        }

        sendMessage(channel, game == null ? "" : game.toString());
    }

    private void sendMessage(MessageChannel channel, String s) {
        channel.sendMessage(s.isEmpty() ? "empty" : s).queue();
    }
}
