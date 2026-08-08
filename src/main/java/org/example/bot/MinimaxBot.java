package org.example.bot;

import org.example.game.Engine;
import org.example.game.GameState;
import org.example.game.Side;

public class MinimaxBot implements Bot{
    private final Engine engine = new Engine();
    private final int depth;

    public MinimaxBot(int depth){
        this.depth = depth;
    }

    @Override
    public int chooseMove(GameState state) {
        Side me = state.current();
        int bestScore = Integer.MIN_VALUE;
        int bestPit = -1;

        for (int pitIndex : engine.legalMoves(state)){
            int score = search(engine.move(state,pitIndex),depth - 1, me);
            if (score > bestScore) {
                bestPit = pitIndex;
                bestScore = score;
            }
        }
        return bestPit;
    }

    private int search(GameState state, int depth, Side me){
        if (state.gameOver() || depth == 0) return eval(state,me);
        boolean maximization = me == state.current();
        int best = maximization ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int pitIndex : engine.legalMoves(state)){
            int score = search(engine.move(state,pitIndex),depth - 1, me);
            best = maximization ? Math.max(best,score) : Math.min(best,score);
        }
        return best;
    }

    private int eval(GameState state, Side me){
        int diff = state.board().getPit(me.store) - state.board().getPit(me.opponent().store);
        return state.gameOver() ? diff * 100 : diff; //chtobi hotel zakonchit
    }

}
