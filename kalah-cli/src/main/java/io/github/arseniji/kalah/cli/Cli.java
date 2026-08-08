package io.github.arseniji.kalah.cli;

import io.github.arseniji.kalah.api.GameSession;
import io.github.arseniji.kalah.api.GameView;
import io.github.arseniji.kalah.api.LocalSession;
import io.github.arseniji.kalah.api.MoveStep;
import io.github.arseniji.kalah.bot.Bot;
import io.github.arseniji.kalah.core.Side;

import java.util.Scanner;

public class Cli {
    private final GameSession session;

    public Cli(){
        this(null, null);
    }

    public Cli(Bot south, Bot north){
        this(new LocalSession(south, north));
    }

    public Cli(GameSession session){
        this.session = session;
    }

    public void startGame(){
        Scanner sc = new Scanner(System.in);
        GameView view = session.view();

        if (!view.steps().isEmpty()) showSteps(view);
        else showBoard(view.board());

        while (!view.gameOver()){
            System.out.println("Ход " + view.current() + ", доступны " + view.legalMoves() + ":");

            if (!sc.hasNextInt()){
                if (!sc.hasNext()){
                    System.out.println("Ввод закончился, выходим");
                    return;
                }
                System.out.println("Нужно число");
                sc.next();
                continue;
            }
            int pit = sc.nextInt();
            if (!view.legalMoves().contains(pit)) {
                System.out.println("Нельзя так ходить");
                continue;
            }

            view = session.move(pit);
            showSteps(view);
        }
        showResult(view);
    }

    private void showSteps(GameView view){
        for (MoveStep step : view.steps()){
            System.out.println(step.side() + " ходит из лунки " + step.pit() + ":");
            showBoard(step.board());
        }
    }

    public void showBoard(int[] b){
        System.out.print('\t');
        for (int i = Side.NORTH.last; i >= Side.NORTH.first; i--){
            System.out.print(b[i] + " ");
        }
        System.out.println();
        System.out.println(b[Side.NORTH.store] + "                " + b[Side.SOUTH.store]);
        System.out.print('\t');
        for (int i = Side.SOUTH.first; i <= Side.SOUTH.last; i++){
            System.out.print(b[i] + " ");
        }
        System.out.println();
        System.out.println();
    }

    public void showResult(GameView view){
        int south = view.board()[Side.SOUTH.store];
        int north = view.board()[Side.NORTH.store];
        System.out.println("SOUTH: " + south + "   NORTH: " + north);
        if (south > north) System.out.println("Победил SOUTH");
        else if (north > south) System.out.println("Победил NORTH");
        else System.out.println("Ничья");
    }
}
