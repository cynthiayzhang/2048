package org.cis1200.twentyfortyeight;

public class GameContent implements java.io.Serializable {
    private int score;
    private int gridSize;
    private int[][] tiles;
    public int gridSize() {
        return gridSize;
    }
    public int score() {
        return score;
    }
    public int[][] tiles() {
        return tiles;
    }
    GameContent(int s, int size, int[][] arr) {
        score = s;
        gridSize = size;
        tiles = arr;
    }
}
