package com.ogsupersand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ConnectFourListener extends ListenerAdapter {
    private static final Set<String> VALID_ACTIONS = Set.of(
        Action.START_GAME.getAction(),
        Action.DROP.getAction()
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
        if (command.equals(Action.START_GAME.getAction())) {
            Player player1 = new Player(event.getAuthor(), TileType.RED);
            Player player2 = new Player(getUserById(scan.next()), TileType.YELLOW);

            game = new ConnectFourGame(player1, player2);
            game.startGame();
            gameMap.put(Set.of(player1.getDiscordUser(), player2.getDiscordUser()), game);
        } else if (command.equals(Action.DROP.getAction())) {
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
        
        sendBoard(channel, game);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        // Ignore this bot reacting to any posts
        User user = event.retrieveUser().complete();
        if (user.equals(App.getUser())) {
            return;
        }

        // Ignore reactions on any message not authored by this bot
        Message message = event.retrieveMessage().complete();
        if (!message.getAuthor().equals(App.getUser())) {
            return;
        }

        // Ignore if some User added a different invalid reaction
        boolean validReaction = Arrays.stream(Reaction.values())
                .filter(r -> Emoji.fromFormatted(r.getSymbol()).toString().equals(event.getEmoji().toString()))
                .findAny()
                .isPresent();
        if (!validReaction) {
            return;
        }

        String messageBody = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        Scanner scan = new Scanner(messageBody);
        Set<User> mentions = new HashSet<>();

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] split = line.split(": ");
            if (split.length <= 1) continue;

            mentions.add(getUserById(split[0]));
        }

        ConnectFourGame game = gameMap.get(mentions);

        if (game == null) {
            sendMessage(channel, 
                    String.format("Whoops! I screwed up, I can't find the game you're looking for. Sorry!")
            );
            return;
        }

        if (game.getPlayerToMove().getDiscordUser().equals(user)) {
            Player winner = game.dropPiece(
                Arrays.stream(Reaction.values()).filter(r -> Emoji.fromFormatted(r.getSymbol()).toString().equals(event.getEmoji().toString())).findFirst().get().getColumn() - 1
            );
            if (winner != null) {
                sendMessage(channel, String.format("%s wins!", winner.getMention()));
            }
        } else {
            sendMessage(channel, 
                    String.format("It is not your turn! Please take your turn %s.", game.getPlayerToMove().getDiscordUser().getAsMention())
            );
            return;
        }

        sendBoard(channel, game);
    }

    private User getUserById(String discordUserResponse) {
        long uid = Long.parseLong(discordUserResponse.replaceAll("[^a-zA-Z0-9]", ""));
        return App.jda.retrieveUserById(uid).complete();
    }

    private void sendMessage(MessageChannel channel, String s) {
        channel.sendMessage(s.isEmpty() ? "empty" : s).queue();
    }

    private void sendBoard(MessageChannel channel, ConnectFourGame game) {
        channel.sendMessage(game == null ? "empty" : game.toString()).queue(m -> {
                Arrays.stream(Reaction.values()).forEach(reaction -> 
                        m.addReaction(Emoji.fromUnicode(reaction.getSymbol())).queue()
                );
        });
    }
}
