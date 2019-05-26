package com.example.capstone;

public class Pair<L, R> {
    L left;
    R right;

    public Pair(){ }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<L, R>(left, right);
    }

    public void setLR(L left, R right){
        this.left = left;
        this.right = right;
    }
}