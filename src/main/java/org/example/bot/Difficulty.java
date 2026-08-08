package org.example.bot;

public enum Difficulty {
    Easy(5, 30),
    Medium(7, 10),
    Hard(9,  0);

    public final int depth;
    public final int tolerance;

    Difficulty(int depth, int tolerance) {
        this.depth = depth;
        this.tolerance = tolerance;
    }
}
