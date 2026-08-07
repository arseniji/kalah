package org.example;

import java.util.Arrays;

public class Board {
    private final int[] board;

    public Board(){
        this.board = new int[]{4,4,4,4,4,4,0,4,4,4,4,4,4,0};
    }

    public Board(int[] board){
        this.board = Arrays.copyOf(board,board.length);
    }

    public int getPit(int index){
        return board[index];
    }

    public int[] getBoard(){
        return Arrays.copyOf(board, board.length);
    }

    public int oppositePit(int index){
        return Math.abs(board.length - 2 - index);
    }


    public int sow(int from, int skip){
        int value = takeAll(from);
        int index = from;
        while (value > 0){
            index = nextPit(index);
            if (index == skip) index = nextPit(index);
            board[index]++;
            value--;
        }
        return index;
    }

    public int takeAll(int index){
        int value = board[index];
        board[index] = 0;
        return value;
    }

    public void add(int index, int count){
        board[index] += count;
    }

    private int nextPit(int currentIndex){
        if (currentIndex < Side.NORTH.store) return currentIndex+1;
        else return 0;
    }
}
