package io.github.arseniji.kalah.cli;

import io.github.arseniji.kalah.bot.Difficulty;
import io.github.arseniji.kalah.bot.MinimaxBot;
import io.github.arseniji.kalah.bot.Personality;

public class Main {
    static void main() {
        Cli cli = new Cli(null, new MinimaxBot(Difficulty.Medium, Personality.Balanced));
        cli.startGame();
    }
}
