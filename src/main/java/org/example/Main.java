package org.example;

import org.example.bot.RandomBot;

public class Main {
     static void main() {
         for (int i = 0; i < 100; i++){
             Cli c = new Cli(new RandomBot(), new RandomBot());
             c.startGame();
         }
    }
}
