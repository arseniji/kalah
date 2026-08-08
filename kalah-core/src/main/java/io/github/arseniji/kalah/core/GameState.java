package io.github.arseniji.kalah.core;

public record GameState(
        Board board,
        Side current,
        boolean gameOver
) {
}
