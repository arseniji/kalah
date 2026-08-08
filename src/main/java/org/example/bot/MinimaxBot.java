package org.example.bot;

import org.example.game.Board;
import org.example.game.Engine;
import org.example.game.GameState;
import org.example.game.Side;

import java.util.*;

public class MinimaxBot implements Bot{
    private static final int INF = 1_000_000;

    private final Engine engine = new Engine();
    private final int depth;
    private final Evaluator personality;
    private final int tolerance;
    private final Random random = new Random();

    public MinimaxBot(int depth){
        this(depth,Personality.Balanced);
    }

    public MinimaxBot(int depth, Evaluator personality){
        this(depth, personality, 0);
    }

    public MinimaxBot(Difficulty difficulty, Evaluator personality){
        this(difficulty.depth, personality, difficulty.tolerance);
    }

    public MinimaxBot(int depth, Evaluator personality, int tolerance){
        this.depth = depth;
        this.personality = personality;
        this.tolerance = tolerance;
    }

    @Override
    public int chooseMove(GameState state) {
        Side me = state.current();

        List<Integer> moves = engine.legalMoves(state);
        Collections.shuffle(moves, random);
        orderedMoves(moves, state.board(), state.current());

        int[] scores = new int[moves.size()];
        int bestScore = -INF;
        for (int i = 0; i < moves.size(); i++){
            int score = search(engine.move(state, moves.get(i)), depth - 1, me, bestScore - tolerance - 1, INF);
            scores[i] = score;
            bestScore = Math.max(bestScore, score);
        }

        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < moves.size(); i++)
            if (scores[i] >= bestScore - tolerance) candidates.add(moves.get(i));

        return candidates.get(random.nextInt(candidates.size()));
    }

    private int search(GameState state, int depth, Side me, int alpha, int beta){
        if (state.gameOver() || depth == 0) return personality.evaluate(state, me);
        boolean maximization = me == state.current();
        int best = maximization ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int pitIndex : orderedMoves(engine.legalMoves(state),state.board(),state.current())){
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

    private List<Integer> orderedMoves(List<Integer> legal, Board board, Side side) {
        legal.sort(Comparator.comparingInt(pitIndex -> {
            int score = 0;
            int target = pitIndex + board.getPit(pitIndex);
            if (target == side.store) score += 1000; // extra
            else if (target <= side.last && board.getPit(target) == 0) {
                int loot = board.getPit(board.oppositePitIndex(target));
                if (loot > 0) score += (loot + 1) * 5;                   // zakhvat
            }
            return -score;
        }));
        return legal;
    }
}
