package io.github.arseniji.kalah.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EngineTest {

    private final Engine engine = new Engine();

    private static GameState state(Side side, int... pits) {
        return new GameState(new Board(pits), side, false);
    }

    private static GameState initial() {
        return new GameState(new Board(), Side.SOUTH, false);
    }

    private static int total(Board b) {
        int sum = 0;
        for (int v : b.getBoard()) sum += v;
        return sum;
    }

    @Test
    public void initialLegalMovesForSouth() {
        assertEquals(List.of(0, 1, 2, 3, 4, 5), engine.legalMoves(initial()));
    }

    @Test
    public void initialLegalMovesForNorth() {
        GameState s = new GameState(new Board(), Side.NORTH, false);
        assertEquals(List.of(7, 8, 9, 10, 11, 12), engine.legalMoves(s));
    }

    @Test
    public void emptyPitIsNotLegal() {
        GameState s = state(Side.SOUTH, 0, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0);
        assertFalse(engine.isLegal(s, 0));
        assertEquals(List.of(1, 2, 3, 4, 5), engine.legalMoves(s));
    }

    @Test
    public void opponentPitAndStoresAreNotLegal() {
        GameState s = initial();
        assertFalse(engine.isLegal(s, 7));
        assertFalse(engine.isLegal(s, 6));
        assertFalse(engine.isLegal(s, 13));
    }

    @Test
    public void illegalMoveThrows() {
        GameState s = initial();
        assertThrows(IllegalArgumentException.class, () -> engine.move(s, 7));
        assertThrows(IllegalArgumentException.class, () -> engine.move(s, 6));
        assertThrows(IllegalArgumentException.class, () -> engine.sowPath(s, 12));
    }

    @Test
    public void lastStoneInOwnStoreGivesExtraTurn() {
        GameState next = engine.move(initial(), 2);
        assertArrayEquals(new int[]{4, 4, 0, 5, 5, 5, 1, 4, 4, 4, 4, 4, 4, 0}, next.board().getBoard());
        assertEquals(Side.SOUTH, next.current());
        assertFalse(next.gameOver());
    }

    @Test
    public void northExtraTurnWhenLastStoneInNorthStore() {
        GameState s = new GameState(new Board(), Side.NORTH, false);
        GameState next = engine.move(s, 9);
        assertEquals(1, next.board().getPit(13));
        assertEquals(Side.NORTH, next.current());
    }

    @Test
    public void ordinaryMoveSwitchesTurn() {
        GameState next = engine.move(initial(), 0);
        assertArrayEquals(new int[]{0, 5, 5, 5, 5, 4, 0, 4, 4, 4, 4, 4, 4, 0}, next.board().getBoard());
        assertEquals(Side.NORTH, next.current());
        assertFalse(next.gameOver());
    }

    @Test
    public void captureTakesOppositeStonesAndLastStoneToStore() {
        GameState s = state(Side.SOUTH, 1, 0, 0, 0, 0, 3, 0, 0, 2, 0, 0, 5, 0, 0);
        GameState next = engine.move(s, 0);
        assertArrayEquals(new int[]{0, 0, 0, 0, 0, 3, 6, 0, 2, 0, 0, 0, 0, 0}, next.board().getBoard());
        assertEquals(Side.NORTH, next.current());
    }

    @Test
    public void noCaptureWhenOppositePitIsEmpty() {
        GameState s = state(Side.SOUTH, 1, 0, 0, 0, 0, 3, 0, 0, 2, 0, 0, 0, 0, 0);
        GameState next = engine.move(s, 0);
        assertArrayEquals(new int[]{0, 1, 0, 0, 0, 3, 0, 0, 2, 0, 0, 0, 0, 0}, next.board().getBoard());
    }

    @Test
    public void noCaptureWhenLastStoneLandsOnOpponentSide() {
        GameState s = state(Side.SOUTH, 1, 0, 0, 0, 0, 2, 0, 0, 3, 0, 0, 0, 0, 0);
        GameState next = engine.move(s, 5);
        assertArrayEquals(new int[]{1, 0, 0, 0, 0, 0, 1, 1, 3, 0, 0, 0, 0, 0}, next.board().getBoard());
        assertEquals(Side.NORTH, next.current());
    }

    @Test
    public void noCaptureWhenLandingOnNonEmptyOwnPit() {
        GameState s = state(Side.SOUTH, 4, 0, 0, 0, 1, 0, 0, 0, 5, 0, 0, 0, 0, 0);
        GameState next = engine.move(s, 0);
        assertArrayEquals(new int[]{0, 1, 1, 1, 2, 0, 0, 0, 5, 0, 0, 0, 0, 0}, next.board().getBoard());
        assertEquals(Side.NORTH, next.current());
    }

    @Test
    public void northCaptureWorksSymmetrically() {
        GameState s = state(Side.NORTH, 0, 0, 0, 0, 4, 1, 0, 1, 0, 0, 0, 0, 0, 0);
        GameState next = engine.move(s, 7);
        assertEquals(5, next.board().getPit(13));
        assertEquals(0, next.board().getPit(4));
        assertEquals(0, next.board().getPit(8));
        assertEquals(Side.SOUTH, next.current());
    }




    @Test
    public void gameEndsWhenSideBecomesEmptyAndRemainingStonesGoToOwners() {
        GameState s = state(Side.SOUTH, 0, 0, 0, 0, 0, 1, 10, 0, 3, 0, 0, 0, 0, 20);
        GameState next = engine.move(s, 5);
        assertTrue(next.gameOver());
        assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 23}, next.board().getBoard());
    }

    @Test
    public void gameEndsWhenOpponentSideIsEmpty() {
        GameState s = state(Side.SOUTH, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        GameState next = engine.move(s, 5);
        assertTrue(next.gameOver());
        assertEquals(0, next.board().getPit(0));
        assertEquals(3, next.board().getPit(6));
    }

    @Test
    public void engineSowPathForInitialState() {
        assertArrayEquals(new int[]{3, 4, 5, 6}, engine.sowPath(initial(), 2));
    }

    @Test
    public void engineSowPathSkipsOpponentStore() {
        GameState s = state(Side.SOUTH, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0);
        assertArrayEquals(new int[]{6, 7, 8, 9, 10, 11, 12, 0}, engine.sowPath(s, 5));
    }


    @Test
    public void moveDoesNotMutateOriginalState() {
        GameState s = initial();
        int[] before = s.board().getBoard();
        engine.move(s, 0);
        assertArrayEquals(before, s.board().getBoard());
        assertEquals(Side.SOUTH, s.current());
    }

}