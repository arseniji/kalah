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

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        for (int pitIndex : engine.legalMoves(state)){
            int score = search(engine.move(state,pitIndex),depth - 1, me,alpha,beta);
            if (score > bestScore) {
                bestPit = pitIndex;
                bestScore = score;
            }
            alpha = Math.max(bestScore,alpha);
        }
        return bestPit;
    }

    private int search(GameState state, int depth, Side me, int alpha, int beta){
        if (state.gameOver() || depth == 0) return eval(state,me);
        boolean maximization = me == state.current();
        int best = maximization ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int pitIndex : engine.legalMoves(state)){
            int score = search(engine.move(state,pitIndex),depth - 1, me,alpha,beta);
            if (maximization){
                best = Math.max(best,score);
                alpha = Math.max(alpha,best);
            }
            else {
                best = Math.min(best,score);
                beta = Math.min(beta,best);
            }
            if (beta <= alpha) break;
        }
        return best;
    }

    private int eval(GameState state, Side me){
        int diff = state.board().getPit(me.store) - state.board().getPit(me.opponent().store);
        return state.gameOver() ? diff * 100 : diff; //chtobi hotel zakonchit
    }

}
