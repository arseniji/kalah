package io.github.arseniji.kalah.bot;

import io.github.arseniji.kalah.core.Board;
import io.github.arseniji.kalah.core.GameState;
import io.github.arseniji.kalah.core.Side;

public enum Personality implements Evaluator{
    Greedy(10,0,2,0,0),
    Aggressive(10,0,6,-1,3),
    Balanced(10,1,3,-3,2),
    Defensive(10,2,1,-8,1);

    private final int wMaterial, wHarvest, wThreat, wRisk, wTempo;

    Personality(int material, int harvest, int threat, int risk, int tempo) {
        this.wMaterial = material;
        this.wHarvest = harvest;
        this.wThreat = threat;
        this.wRisk = risk;
        this.wTempo = tempo;
    }


    @Override
    public int evaluate(GameState state, Side me) {
        if (state.gameOver()){
            int diff = state.board().getPit(me.store) - state.board().getPit(me.opponent().store);
            if (diff > 0) return 10000 + diff;
            if (diff < 0) return  -10000 + diff;
            return 0;
        }
        Board b = state.board();
        Side opp = me.opponent();

        int material = b.getPit(me.store) - b.getPit(me.opponent().store);
        int harvest = sideSum(b,me) - sideSum(b,opp);
        int threat = bestCapture(b,me);
        int risk = bestCapture(b,opp);
        int tempo = extraTurnMoves(b,me);

        return material * wMaterial + harvest * wHarvest + threat * wThreat + risk * wRisk + tempo * wTempo;
    }

    int sideSum(Board b,Side s){
        int sum = 0;
        for (int i = s.first; i <= s.last; i++){
            sum+=b.getPit(i);
        }
        return sum;
    }

    int bestCapture (Board b, Side s){
        int best = 0;
        for (int j = s.first; j <= s.last; j++){
            int n = b.getPit(j);
            if (n == 0) continue;
            int landing = j + n;
            if (landing > s.last) continue;
            if (b.getPit(landing) != 0) continue;
            best = Math.max(best, b.getPit(b.oppositePitIndex(landing)) + 1);
        }
        return best;
    }

    int extraTurnMoves(Board b, Side s){
        int count = 0;
        for (int j = s.first; j <= s.last; j++)
            if (b.getPit(j) > 0 && j + b.getPit(j) == s.store) count++;
        return count;
    }
}
