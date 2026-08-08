package io.github.arseniji.kalah.core;

import java.util.ArrayList;
import java.util.List;

public class Engine {
    public Engine(){}

    public boolean isLegal(GameState state, int index){
        return state.current().owns(index) && state.board().getPit(index) != 0;
    }

    public List<Integer> legalMoves(GameState state){
        List<Integer> moves = new ArrayList<>();
        Side side = state.current();
        for (int i = side.first; i <= side.last; i++)
            if (isLegal(state, i)) moves.add(i);
        return moves;
    }

    public GameState move(GameState state, int index){
        if (!isLegal(state, index))
            throw new IllegalArgumentException("Недопустимый ход: " + index);

        Board nextBoard = new Board(state.board().getBoard());
        Side side = state.current();

        int lastIndex = nextBoard.sow(index, side.opponent().store);

        if (isCapture(nextBoard, lastIndex, side)) {
            int captured = nextBoard.takeAll(nextBoard.oppositePitIndex(lastIndex)) + nextBoard.takeAll(lastIndex);
            nextBoard.add(side.store, captured);
        }

        Side turn = (lastIndex == side.store) ? side : side.opponent();
        boolean gameOver = isSideEmpty(nextBoard, Side.SOUTH) || isSideEmpty(nextBoard, Side.NORTH);
        if (gameOver) sweepRemaining(nextBoard);

        return new GameState(nextBoard, turn, gameOver);
    }

    private boolean isCapture(Board b, int lastIndex, Side s){
        if (!s.owns(lastIndex)) return false;
        return b.getPit(lastIndex) == 1 && b.getPit(b.oppositePitIndex(lastIndex)) != 0;
    }

    private boolean isSideEmpty(Board b, Side s){
        for (int i = s.first; i <= s.last; i++)
            if (b.getPit(i) != 0) return false;
        return true;
    }

    private void sweepRemaining(Board b){
        for (Side s : Side.values()){
            int sum = 0;
            for (int i = s.first; i <= s.last; i++) sum += b.takeAll(i);
            b.add(s.store, sum);
        }
    }
}
