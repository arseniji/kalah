package org.example;

import java.util.Scanner;

public class Engine {
    Board board;
    Player p1;
    Player p2;
    Player current;

    public Engine(){
        board = new Board();
        p1 = new Player(1);
        p2 = new Player(2);
        current = p1;
    }

    public void startGame(){
        Scanner sc = new Scanner(System.in);
        boolean gameOver = false;
        while (!gameOver){
            board.showBoard();
            System.out.println("Игрок " + current.number + ", выбери лунку:");
            int index = sc.nextInt();
            if (!board.isMovable(index, current)) {
                System.out.println("Нельзя так ходить");
                continue;
            }
            move(index);
            gameOver = checkWin(p1) || checkWin(p2);
        }
        sweepRemaining();
        board.showBoard();
    }

    public void move(int index){
        if (board.isMovable(index,current)){
            int[] prevBoard = board.getBoard();
            int lastIndex = board.move(index,current);
            if (checkCapture(lastIndex,prevBoard,current)) {
                int captured = board.getPit(board.oppositePit(lastIndex)) + 1;
                board.setPit(board.oppositePit(lastIndex), 0);
                board.setPit(lastIndex, 0);
                if (current.number == 1) board.setPit(6,board.getPit(6) + captured);
                else board.setPit(13,board.getPit(13) + captured);
            }
            if (!checkExtraTurn(lastIndex)) nextPlayer();
        }
    }

    private boolean checkExtraTurn(int lastIndex){
        return lastIndex == 6 || lastIndex == 13;
    }

    private boolean checkCapture(int lastIndex, int[] prevBoard, Player p){
        if (lastIndex < 6 && p.number == 1)
            if (prevBoard[lastIndex] == 0 && board.getPit(board.oppositePit(lastIndex)) != 0) return true;
        if (lastIndex > 6 && lastIndex < 13 && p.number == 2)
            if (prevBoard[lastIndex] == 0 && board.getPit(board.oppositePit(lastIndex)) != 0) return true;
        return false;
    }

    private boolean checkWin(Player p){
        int[] currentBoard = board.getBoard();
        boolean winFlag = true;
        if (p.number == 1){
            for (int i = 0; i < 6; i++){
                if (currentBoard[i] != 0) winFlag = false;
            }
        }
        if (p.number == 2){
            for (int i = 7; i < 13; i++){
                if (currentBoard[i] != 0) winFlag = false;
            }
        }
        return winFlag;
    }

    private void nextPlayer(){
        if (current == p1) current = p2;
        else current = p1;
    }

    private void sweepRemaining(){
        int sumP1 = 0, sumP2 = 0;
        for (int i = 0; i < 6; i++){ sumP1 += board.getPit(i); board.setPit(i,0); }
        for (int i = 7; i < 13; i++){ sumP2 += board.getPit(i); board.setPit(i,0); }
        board.setPit(6, board.getPit(6) + sumP1);
        board.setPit(13, board.getPit(13) + sumP2);
    }
}
