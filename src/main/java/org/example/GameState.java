package org.example;

public record GameState(
        Board board,
        Side current,
        boolean gameOver
) {
}
