package org.example.bot;

import org.example.game.GameState;
import org.example.game.Side;

public interface Evaluator {
    int evaluate(GameState state, Side me);
}
