package io.github.arseniji.kalah.core;

import org.junit.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class SideTest {

    @Test
    public void opponentIsSymmetric() {
        assertEquals(Side.NORTH, Side.SOUTH.opponent());
        assertEquals(Side.SOUTH, Side.NORTH.opponent());
    }

    @Test
    public void ownsOnlyOwnPits() {
        for (int i = 0; i <= 5; i++) assertTrue(Side.SOUTH.owns(i), "SOUTH должен владеть ямкой " + i);
        for (int i = 7; i <= 12; i++) assertTrue(Side.NORTH.owns(i), "NORTH должен владеть ямкой " + i);
        assertFalse(Side.SOUTH.owns(7));
        assertFalse(Side.NORTH.owns(0));
    }

    @Test
    public void storeIsNotOwnedAsPit() {
        assertFalse(Side.SOUTH.owns(Side.SOUTH.store));
        assertFalse(Side.NORTH.owns(Side.NORTH.store));
    }
}