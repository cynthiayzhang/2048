package org.cis1200.twentyfortyeight;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GamePlay {

    //States of the games
    public enum GameState {
        CONTINUE,
        WIN,
        LOSE;
    };

    //Arrow directions
    public enum Arrow {
        LEFT,
        RIGHT,
        UP,
        DOWN;
    };

    //Multi-threading, parallel processes rows and columns because
    //either rows or columns are independent
    //Extends class thread, must implement run
    //Threads determine if current board has a move
    public class Movable extends Thread {
        //Keyword atomic = thread safe
        private static AtomicBoolean movableFlag = new AtomicBoolean(false);
        private int[] arr; //1D array (single row or column)

        Movable(int[] a) {
            arr = a;
        }

        public void run() {
            //No data or 1 value, return
            if (arr == null || arr.length < 2) {
                return;
            }

            //If neighboring values are equal, then the numbers can still move
            //Stops at first instance where there is another move
            for (int i = 1; i < arr.length; i++) {
                if (arr[i] == arr[i - 1]) {
                    movableFlag.set(true);
                    return;
                }
            }
        }
        //
        public static AtomicBoolean getMovableFlag() {
            AtomicBoolean copy = new AtomicBoolean(movableFlag.get());
            return copy;
        }

        public static void setMovableFlag(boolean b) {
            movableFlag.set(b);
        }
    }


    //Array which maps an index to a power of 2 (faster than map)
    private int[] scores = {0, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048};
    private int gridSize;
    private int[][] tiles; //from 0 to 11
    private GameState state = GameState.CONTINUE;

    public int[][] tiles() {
        return tiles;
    }

    //Loading tiles
    public void setTiles(int[][] arr) {
        tiles = arr;
        state = GameState.CONTINUE;
    }

    public GameState state() {
        return state;
    }

    // this is the function of checking movable flag using single thread
    public boolean singleThreadMovableChecking() {
        if (gridSize < 2) {
            return false;
        }
        for (int i = 0; i < gridSize; i++) {
            for (int j = 1; j < gridSize; j++) {
                // check row
                if (tiles[i][j] == tiles[i][j - 1]) {
                    return true;
                }
                // check col
                if (tiles[j][i] == tiles[j - 1][i]) {
                    return true;
                }
            }
        }

        return false;
    }

    //Constructor
    //Takes in grid size
    public GamePlay(int size) {
        gridSize = size;
        state = GameState.CONTINUE;


        tiles = new int[gridSize][gridSize];

        // generating first two tiles
        Random rand = new Random();
        int upperbound = gridSize * gridSize;
        int idx1 = rand.nextInt(upperbound);
        int idx2 = rand.nextInt(upperbound);
        while (idx2 == idx1) {
            idx2 = rand.nextInt(upperbound);
        }

        int x1 = idx1 / gridSize;
        int y1 = idx1 - x1 * gridSize;
        tiles[x1][y1] = 1;
        int x2 = idx2 / gridSize;
        int y2 = idx2 - x2 * gridSize;
        tiles[x2][y2] = 1;
    }

    public int move(Arrow dir) {
        int score = 0;
        int index = 0;
        /* New values are put onto here to save a copy of the old ones,
        * * Until all intended moves for the arrow key are done */
        int[][] tiles2 = new int[gridSize][gridSize];

        /* Moves all tiles to the intended direction,
         * Combines if two same values are next to each other,
         * Left arrow pressed */
        if (dir == Arrow.LEFT) {
            for (int x = 0; x < gridSize; x++) {
                for (int y = 0; y < gridSize; y++) {
                    if (tiles[x][y] != 0) {
                        if (tiles[x][y] == tiles2[x][index]) {
                            tiles2[x][index] = tiles2[x][index] + 1;
                            score += scores[tiles2[x][index]];
                            index++;
                        } else {
                            if (tiles2[x][index] != 0) {
                                index++;
                            }
                            tiles2[x][index] = tiles[x][y];
                        }
                    }
                }
                index = 0;
            }
        } else if (dir == Arrow.RIGHT) { //Right arrow pressed
            index = gridSize - 1;

            for (int x = 0; x < gridSize; x++) {
                for (int y = gridSize - 1; y >= 0; y--) {
                    if (tiles[x][y] != 0) {
                        if (tiles[x][y] == tiles2[x][index]) {
                            tiles2[x][index] = tiles2[x][index] + 1;
                            score += scores[tiles2[x][index]];
                            index--;
                        } else {
                            if (tiles2[x][index] != 0) {
                                index--;
                            }
                            tiles2[x][index] = tiles[x][y];
                        }
                    }
                }
                index = gridSize - 1;
            }
        } else if (dir == Arrow.UP) { //Up arrow pressed
            for (int y = 0; y < gridSize; y++) {
                for (int x = 0; x < gridSize; x++) {
                    if (tiles[x][y] != 0) {
                        if (tiles[x][y] == tiles2[index][y]) {
                            tiles2[index][y] = tiles2[index][y] + 1;
                            score += scores[tiles2[index][y]];
                            index++;
                        } else {
                            if (tiles2[index][y] != 0) {
                                index++;
                            }
                            tiles2[index][y] = tiles[x][y];
                        }
                    }
                }
                index = 0;
            }
        } else if (dir == Arrow.DOWN) {  //Down arrow pressed
            index = gridSize - 1;

            for (int y = 0; y < gridSize; y++) {
                for (int x = gridSize - 1; x > -1; x--) {
                    if (tiles[x][y] != 0) {
                        if (tiles[x][y] == tiles2[index][y]) {
                            tiles2[index][y] = tiles2[index][y] + 1;
                            score += scores[tiles2[index][y]];
                            index--;
                        } else {
                            if (tiles2[index][y] != 0) {
                                index--;
                            }
                            tiles2[index][y] = tiles[x][y];
                        }
                    }
                }
                index = gridSize - 1;
            }
        }

        tiles = tiles2;

        //Randomly populates a 2 or 4
        ArrayList<Integer> x0s = new ArrayList<Integer>();
        ArrayList<Integer> y0s = new ArrayList<Integer>();
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                if (tiles[x][y] == 0) {
                    x0s.add(x);
                    y0s.add(y);
                }

                if (tiles[x][y] == 11) { //if 2048, then win
                    state = GameState.WIN;
                }
            }
        }

        if (state == GameState.WIN) {
            return score;
        }
        //randomly generates number
        Random rand = new Random();
        int upperbound = 10;
        int val = rand.nextInt(upperbound);
        if (val != 2) { //10% of time generates a 4
            val = 1; //90% of time generates a 2
        }
        upperbound = x0s.size();

        //places val on the board
        if (upperbound > 0) {
            int idx = rand.nextInt(upperbound);
            int x = x0s.get(idx);
            int y = y0s.get(idx);

            tiles[x][y] = val;
        } else if (state != GameState.WIN) {
            /* 2 cases: LOSE, or CONTINUE
            here is the single thread version of checking movable
            if (SingleThreadMovableChecking()) {
            state = GameState.CONTINUE; }
            else { state = GameState.LOSE; }
            */

            // here is the multi thread version
            // both row and columns, row + col size
            Movable[] checkers = new Movable[2 * gridSize];

            for (int i = 0; i < gridSize; i++) {
                if (Movable.movableFlag.get()) { //true = more moves
                    //do not need to check other row and columns if there is still a move
                    state = GameState.CONTINUE;
                    return score;
                }

                //Multi-threading to checks if still moves

                //row
                checkers[i * 2] = new Movable(tiles[i]);
                checkers[i * 2].start();

                //construct 1D col array for multi-threading
                int[] a = new int[gridSize];
                for (int j = 0; j < gridSize; j++) {
                    a[j] = tiles[j][i];
                }

                if (Movable.movableFlag.get()) { //true = more moves
                    state = GameState.CONTINUE;
                    return score;
                }

                //col
                checkers[i * 2 + 1] = new Movable(a);
                checkers[i * 2 + 1].start();
            }

            for (int i = 0; i < gridSize * 2; i++) {
                try {
                    if (checkers[i] != null) { //cannot be null to call join function
                        if (checkers[i].isInterrupted()) {
                            //Only display thread isInterrupted message,
                            // do not propagated the exception by the project requirements
                            System.out.println("A thread is interrupted");
                        }
                        checkers[i].join(); //wait for other threads to finish
                    }
                } catch (Exception ex) {
                    System.out.println("Exception has been caught" + ex);
                }
            }
            if (Movable.movableFlag.get()) {
                state = GameState.CONTINUE;
            } else {
                state = GameState.LOSE; //all threads finish checking, all movableFlag = false
            }
        }

        return score;
    }
}
