package io.github.arseniji.kalah.api;

import io.github.arseniji.kalah.core.Side;

import java.util.List;

public record GameView(
        int[] board,
        Side current,
        boolean gameOver,
        List<Integer> legalMoves,
        List<MoveStep> steps
) {

}