package org.example;

import org.example.bot.Bot;
import org.example.game.Board;
import org.example.game.Engine;
import org.example.game.GameState;
import org.example.game.Side;

import java.util.Scanner;

public class Cli {
    private GameState state;
    private final Engine engine;
    private final Bot south;
    private final Bot north;

    public Cli(){
        this(null, null);
    }

    public Cli(Bot south, Bot north){
        this.state = new GameState(new Board(), Side.SOUTH, false);
        this.engine = new Engine();
        this.south = south;
        this.north = north;
    }

    public void startGame(){
        Scanner sc = new Scanner(System.in);
        while (!state.gameOver()){
            showBoard(state.board());
            Bot bot = botFor(state.current());
            int index;
            if (bot != null){
                index = bot.chooseMove(state);
                System.out.println("Ход " + state.current() + " (бот): " + index);
            } else {
                System.out.println("Ход " + state.current() + ", доступны " + engine.legalMoves(state) + ":");
                if (!sc.hasNextInt()){
                    if (!sc.hasNext()){
                        System.out.println("Ввод закончился, выходим");
                        return;
                    }
                    System.out.println("Нужно число");
                    sc.next();
                    continue;
                }
                index = sc.nextInt();
                if (!engine.isLegal(state, index)) {
                    System.out.println("Нельзя так ходить");
                    continue;
                }
            }
            state = engine.move(state, index);
        }
        showBoard(state.board());
        showResult(state.board());
    }

    private Bot botFor(Side side){
        return side == Side.SOUTH ? south : north;
    }

    public void showBoard(Board board){
        int[] b = board.getBoard();
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

    public void showResult(Board board){
        int south = board.getPit(Side.SOUTH.store);
        int north = board.getPit(Side.NORTH.store);
        System.out.println("SOUTH: " + south + "   NORTH: " + north);
        if (south > north) System.out.println("Победил SOUTH");
        else if (north > south) System.out.println("Победил NORTH");
        else System.out.println("Ничья");
    }
}
