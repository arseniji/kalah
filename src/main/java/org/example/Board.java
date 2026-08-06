package org.example;

import java.util.Arrays;

public class Board {
    private final int[] board;
    public static int boardLength = 14;

    public Board(){
        board = new int[]{4,4,4,4,4,4,0,4,4,4,4,4,4,0};
    }

    public int getPit(int index){
        return board[index];
    }

    public void setPit(int index,int value){
        board[index] = value;
    }

    public int[] getBoard(){
        return Arrays.copyOf(board, board.length);
    }

    public int oppositePit(int index){
        return Math.abs(boardLength - 2 - index);
    }

    public boolean isMovable(int index, Player p){
        if (index > 13) return false;
        if (index < 0) return false;
        if (index == 6 || index == 13) return false;
        if (p.number == 1 && index > 5) return false;
        if (p.number == 2 && index < 7) return false;
        return board[index] != 0;
    }

    public int move(int index,Player p){
        int value = board[index];
        board[index] = 0;
        while (value > 0){
            index = nextPit(index);
            if (index == 6 && p.number == 2 || index == 13 && p.number == 1) index = nextPit(index);
            board[index]+=1;
            value--;
        }
        return index;
    }

    public void showBoard(){
        System.out.print('\t');
        for (int i = 12; i > 6; i--){
            System.out.print(getPit(i) + " ");
        }
        System.out.println();
        System.out.println(getPit(13) + "                " + getPit(6));
        System.out.print('\t');
        for (int i = 0; i < 6; i++){
            System.out.print(getPit(i) + " ");
        }
        System.out.println();
        System.out.println();
    }



    private int nextPit(int currentIndex){
        if (currentIndex < 13) return currentIndex+1;
        else return 0;
    }
}
