package io.github.arseniji.kalah.api;

import io.github.arseniji.kalah.core.Side;

public record MoveStep(
        int pit,
        Side side,
        int[] board
) {
}
