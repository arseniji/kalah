package org.example.bot;

import org.example.game.Engine;
import org.example.game.GameState;

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
