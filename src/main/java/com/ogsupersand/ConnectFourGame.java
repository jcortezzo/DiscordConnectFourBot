package com.ogsupersand;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

public class ConnectFourGame {
    private static final int HEIGHT = 6;
    private static final int WIDTH = 7;
    private static final int NUM_TO_WIN = 4;

    private BiMap<Point, Tile> boardMap;
    private BiMap<Player, TileType> playerTileMap;

    private Player[] players;
    private int turn;

    private static final String INVALID_COLUMN_MESSAGE = "%d is not a valid column, width is %d";

    public ConnectFourGame(Player... players) {
        this.players = Arrays.copyOf(players, players.length);
        this.turn = 0;

        playerTileMap = HashBiMap.create();
        for (Player p : players) {
            playerTileMap.put(p, p.getTileType());
        }

        boardMap = HashBiMap.create();
    }

    public boolean isGameStarted() {
        return boardMap != null;
    }

    public void startGame() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile t = new Tile();
                boardMap.put(new Point(x, y), t);
            }
        }
    }

    public Player dropPiece(int column) {
        return dropPiece(getPlayerToMove(), column);
    }

    public Player dropPiece(Player player, int column) {
        if (!isValidColumn(column)) {
            throw new InvalidMoveException(
                String.format(INVALID_COLUMN_MESSAGE, column, WIDTH)
            );
        } else if (isColumnFull(column)) {
            throw new InvalidMoveException(
                String.format("Column %d is full, cannot place piece.", column)
            );
        }

        Tile droppedTile = Iterables.getLast(
                IntStream.range(0, HEIGHT)
                .mapToObj(i -> boardMap.get(new Point(column, i)))
                .filter(tile -> tile.isEmpty())
                .collect(Collectors.toList()));
        droppedTile.setType(player.getTileType());
        turn++;

        return pieceWins(droppedTile) ? player : null;
    }

    public boolean pieceWins(Tile t) {
        Point p = boardMap.inverse().get(t);
        Set<Point> vertical = new HashSet<>();
        Set<Point> horizontal = new HashSet<>();
        Set<Point> diagonalDown = new HashSet<>();
        Set<Point> diagonalUp = new HashSet<>();

        List<Set<Point>> allDirections = List.of(
            vertical,
            horizontal,
            diagonalDown,
            diagonalUp
        );

        updateSet(vertical, p, 0, 1);
        updateSet(vertical, p, 0, -1);

        updateSet(horizontal, p, 1, 0);
        updateSet(horizontal, p, -1, 0);

        updateSet(diagonalDown, p, 1, 1);
        updateSet(diagonalDown, p, -1, -1);

        updateSet(diagonalUp, p, 1, -1);
        updateSet(diagonalUp, p, -1, 1);

        return allDirections.stream()
                .filter(set -> set.size() >= NUM_TO_WIN)
                .collect(Collectors.toSet())
                .size() > 0;
    }

    private void updateSet(Set<Point> set, Point p, int xWeight, int yWeight) {
        for (int i = 0; i < NUM_TO_WIN; i++) {
            Point newPoint = new Point(p.x + i * xWeight, p.y + i * yWeight);
            if (!boardMap.containsKey(newPoint)) {
                break;
            } else if (boardMap.get(newPoint).getType() != boardMap.get(p).getType()) {
                break;
            } else {
                set.add(newPoint);
            }
        }
    }

    public boolean isColumnFull(int column) {
        if (!isValidColumn(column)) {
            throw new InvalidMoveException(
                String.format(INVALID_COLUMN_MESSAGE, column, WIDTH)
            );
        }
        return IntStream.range(0, HEIGHT)
                .mapToObj(i -> boardMap.get(new Point(column, i)))
                .filter(tile -> tile.isEmpty())
                .collect(Collectors.toList())
                .size() == 0;
    }

    public boolean isValidColumn(int column) {
        return column >= 0 && column < WIDTH;
    }

    public Player getPlayerToMove() {
        return players[turn % numPlayers()];
    }

    public int numPlayers() {
        return players.length;
    }

    @Override
    public String toString() {
        if (!isGameStarted()) return "";

        StringBuilder result = new StringBuilder();
        for (Player p : players) {
            result.append(String.format("%s: %s%n", p.getDiscordUser().getAsMention(), p.getTileType().toString()));
        }
        
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                result.append(boardMap.get(new Point(x, y)).getType().toString());
            }
            result.append(String.format("%n"));
        }
        return result.toString();
    }
}
