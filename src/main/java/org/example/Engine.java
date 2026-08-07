package org.example;

public class Engine {
    public Engine(){}

    public GameState move(GameState state, int index){
        if (!state.board().isMovable(index, state.current()))
            throw new IllegalArgumentException("Недопустимый ход: " + index);
        Board nextBoard = new Board(state.board().getBoard());
        Side side = state.current();
        int lastIndex = nextBoard.move(index, side);
        if (checkCapture(lastIndex,nextBoard,side)) {
            int opposite = nextBoard.oppositePit(lastIndex);
            int captured = nextBoard.getPit(opposite) + 1;
            nextBoard.setPit(opposite, 0);
            nextBoard.setPit(lastIndex, 0);
            nextBoard.setPit(side.store,nextBoard.getPit(side.store) + captured);
        }

        Side turn = (lastIndex == side.store) ? side : side.opponent();
        boolean gameOver = isSideEmpty(nextBoard, Side.SOUTH) || isSideEmpty(nextBoard, Side.NORTH);
        if (gameOver) sweepRemaining(nextBoard);

        return new GameState(nextBoard, turn, gameOver);
    }

    public boolean checkCapture(int lastIndex, Board nextBoard, Side s){
        if (s.owns(lastIndex))
            return nextBoard.getPit(lastIndex) == 1 && nextBoard.getPit(nextBoard.oppositePit(lastIndex)) != 0;
        return false;
    }

    public boolean isSideEmpty(Board b, Side s){
        for (int i = s.first; i <= s.last; i++)
            if (b.getPit(i) != 0) return false;
        return true;
    }

    public void sweepRemaining(Board b){
        for (Side s : Side.values()){
            int sum = 0;
            for (int i = s.first; i <= s.last; i++){ sum += b.getPit(i); b.setPit(i,0); }
            b.setPit(s.store, b.getPit(s.store) + sum);
        }
    }
}
