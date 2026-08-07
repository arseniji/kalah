package org.example;

public enum Side {
    SOUTH(0,5,6),
    NORTH(7,12,13);
    final int first,last,store;

    Side(int first, int last, int store) {
        this.first = first;
        this.last = last;
        this.store = store;
    }

    public Side opponent(){
        return this == SOUTH ? NORTH : SOUTH;
    }

    public boolean owns(int index){
        return index >= first && index <= last;
    }
}
