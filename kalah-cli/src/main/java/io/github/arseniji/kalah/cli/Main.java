package io.github.arseniji.kalah.cli;

import io.github.arseniji.kalah.bot.MinimaxBot;
import io.github.arseniji.kalah.bot.RandomBot;

public class Main {
     static void main() {
         for (int i = 0; i < 10; i++){
             Cli c = new Cli(new MinimaxBot(5), new RandomBot());
             c.startGame();
         }
    }
}
