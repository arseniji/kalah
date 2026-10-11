package io.github.arseniji.kalah.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class BoardTest {

    private static final int NO_SKIP = -1;

    @Test
    public void initialBoardHasFourStonesInEveryPitAndEmptyStores() {
        Board b = new Board();
        assertArrayEquals(new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0}, b.getBoard());
    }

    @Test
    public void constructorCopiesInputArray() {
        int[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
        Board b = new Board(src);
        src[0] = 99;
        assertEquals(1, b.getPit(0));
    }

    @Test
    public void getBoardReturnsCopy() {
        Board b = new Board();
        int[] copy = b.getBoard();
        copy[0] = 99;
        assertEquals(4, b.getPit(0));
    }

    @Test
    public void oppositePitIndexIsMirrored() {
        Board b = new Board();
        assertEquals(12, b.oppositePitIndex(0));
        assertEquals(7, b.oppositePitIndex(5));
        assertEquals(0, b.oppositePitIndex(12));
        assertEquals(5, b.oppositePitIndex(7));
        for (int i = 0; i <= 5; i++) {
            assertEquals(i, b.oppositePitIndex(b.oppositePitIndex(i)));
        }
    }

    @Test
    public void takeAllReturnsStonesAndEmptiesPit() {
        Board b = new Board();
        assertEquals(4, b.takeAll(2));
        assertEquals(0, b.getPit(2));
    }

    @Test
    public void addIncreasesPit() {
        Board b = new Board();
        b.add(6, 5);
        assertEquals(5, b.getPit(6));
    }

    @Test
    public void sowDistributesOneStoneToEachNextPit() {
        Board b = new Board();
        int last = b.sow(0, Side.NORTH.store);
        assertEquals(4, last);
        assertArrayEquals(new int[]{0, 5, 5, 5, 5, 4, 0, 4, 4, 4, 4, 4, 4, 0}, b.getBoard());
    }


    @Test
    public void sowEndingInOwnStoreReturnsStoreIndex() {
        Board b = new Board();
        assertEquals(6, b.sow(2, Side.NORTH.store));
    }

    @Test
    public void sowFromNorthSkipsSouthStore() {
        Board b = new Board(new int[]{0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 8, 0});
        int last = b.sow(12, Side.SOUTH.store);
        assertEquals(7, last);
        assertEquals(0, b.getPit(6));
        assertEquals(1, b.getPit(13));
    }


    @Test
    public void sowPathSkipsOpponentStore() {
        Board b = new Board(new int[]{0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0});
        assertArrayEquals(new int[]{6, 7, 8, 9, 10, 11, 12, 0}, b.sowPath(5, Side.NORTH.store));
    }

    @Test
    public void sowWithoutSkipVisitsEveryPit() {
        Board b = new Board(new int[]{14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        b.sow(0, NO_SKIP);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, b.getBoard());
    }
}