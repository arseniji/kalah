package org.example.game;

public record GameState(
        Board board,
        Side current,
        boolean gameOver
) {
}
