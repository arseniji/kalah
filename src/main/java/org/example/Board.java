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

    public void setPit(int index,int value){
        board[index] = value;
    }

    public int[] getBoard(){
        return Arrays.copyOf(board, board.length);
    }

    public int oppositePit(int index){
        return Math.abs(board.length - 2 - index);
    }

    public boolean isMovable(int index, Side s) {
        return s.owns(index) && board[index] != 0;
    }

    public int move(int index,Side s){
        int value = board[index];
        board[index] = 0;
        while (value > 0){
            index = nextPit(index);
            if (index == s.opponent().store) index = nextPit(index);
            board[index]+=1;
            value--;
        }
        return index;
    }

    private int nextPit(int currentIndex){
        if (currentIndex < Side.NORTH.store) return currentIndex+1;
        else return 0;
    }
}
