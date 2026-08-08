package io.github.arseniji.kalah.bot;

import io.github.arseniji.kalah.core.GameState;
import io.github.arseniji.kalah.core.Side;

public interface Evaluator {
    int evaluate(GameState state, Side me);
}
