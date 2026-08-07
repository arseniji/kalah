package org.example;

import java.util.Scanner;

public class Cli {
    private GameState state;
    private final Engine engine;

    public Cli(){
        state = new GameState(new Board(), Side.SOUTH, false);
        engine = new Engine();
    }

    public void startGame(){
        Scanner sc = new Scanner(System.in);
        while (!state.gameOver()){
            showBoard(state.board());
            System.out.println("Ход " + state.current() + ", выбери лунку:");
            if (!sc.hasNextInt()){
                if (!sc.hasNext()){
                    System.out.println("Ввод закончился, выходим");
                    return;
                }
                System.out.println("Нужно число");
                sc.next();
                continue;
            }
            int index = sc.nextInt();
            if (!state.board().isMovable(index, state.current())) {
                System.out.println("Нельзя так ходить");
                continue;
            }
            state = engine.move(state, index);
        }
        showBoard(state.board());
        showResult(state.board());
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
