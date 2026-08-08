package io.github.arseniji.kalah.bot;

import io.github.arseniji.kalah.core.GameState;

public interface Bot {
    int chooseMove(GameState state);
}
