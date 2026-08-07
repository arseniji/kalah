package org.example.bot;

import org.example.game.GameState;

public interface Bot {
    int chooseMove(GameState state);
}
