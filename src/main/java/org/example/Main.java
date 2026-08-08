package org.example;

import org.example.bot.MinimaxBot;
import org.example.bot.RandomBot;

public class Main {
     static void main() {
         for (int i = 0; i < 10; i++){
             Cli c = new Cli(new MinimaxBot(5), new RandomBot());
             c.startGame();
         }
    }
}
