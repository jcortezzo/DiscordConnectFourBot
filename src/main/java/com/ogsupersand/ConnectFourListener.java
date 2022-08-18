package com.ogsupersand;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ConnectFourListener extends ListenerAdapter {
    private static final Set<String> VALID_ACTIONS = Set.of(
        Action.START_GAME.toString(),
        Action.DROP.toString()
    );
    private static final String COMMAND_TOKEN = "?";

    private Map<Set<User>, ConnectFourGame> gameMap;

    public ConnectFourListener() {
        gameMap = new HashMap<>();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        String msgString = msg.getContentRaw();
        MessageChannel channel = event.getChannel();

        if (!msgString.startsWith(COMMAND_TOKEN)) {
            return;
        }
        String fullMessage = msgString.substring(COMMAND_TOKEN.length());
        if (fullMessage.isBlank()) {
            return;
        }
        Scanner scan = new Scanner(fullMessage.toLowerCase());
        String command = scan.next().toLowerCase();

        if (!VALID_ACTIONS.contains(command)) {
            return;
        }
        ConnectFourGame game = null;
        if (command.equals(Action.START_GAME.toString())) {
            Player player1 = new Player(event.getAuthor(), TileType.RED);
            Player player2 = new Player(getUserById(scan.next()), TileType.YELLOW);

            game = new ConnectFourGame(player1, player2);
            game.startGame();
            gameMap.put(Set.of(player1.getDiscordUser(), player2.getDiscordUser()), game);
        } else if (command.equals(Action.DROP.toString())) {
            int column = scan.nextInt();
            User user1 = event.getAuthor();
            User user2 = getUserById(scan.next());

            game = gameMap.get(Set.of(user1, user2));

            if (game == null) {
                sendMessage(channel, 
                        String.format("There is no connect4 game between %s and %s!", user1, user2)
                );
                return;
            }

            if (!user1.equals(game.getPlayerToMove().getDiscordUser())) {
                sendMessage(channel, 
                        String.format("It is not your turn! Please take your turn %s.", user2.getAsMention())
                );
                return;
            }

            Player winner = game.dropPiece(column);
            if (winner != null) {
                sendMessage(channel, String.format("%s wins!", winner.getMention()));
            }
        }
        
        sendMessage(channel, game == null ? "" : game.toString());
    }

    private User getUserById(String discordUserResponse) {
        long uid = Long.parseLong(discordUserResponse.replaceAll("[^a-zA-Z0-9]", ""));
        return App.jda.retrieveUserById(uid).complete();
    }

    private void sendMessage(MessageChannel channel, String s) {
        channel.sendMessage(s.isEmpty() ? "empty" : s).queue();
    }
}
