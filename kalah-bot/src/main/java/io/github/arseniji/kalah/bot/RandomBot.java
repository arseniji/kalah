package io.github.arseniji.kalah.bot;

import io.github.arseniji.kalah.core.Engine;
import io.github.arseniji.kalah.core.GameState;

import java.util.List;
import java.util.Random;

public class RandomBot implements Bot {
    Engine engine = new Engine();
    Random random = new Random();

    @Override
    public int chooseMove(GameState state){
        List<Integer> legal = engine.legalMoves(state);
        return legal.get(random.nextInt(legal.size()));
    }
}
