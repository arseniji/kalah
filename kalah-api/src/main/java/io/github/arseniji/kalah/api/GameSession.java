package io.github.arseniji.kalah.api;

public interface GameSession {
    GameView view();
    GameView move(int pit);
}
