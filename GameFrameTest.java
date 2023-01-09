package org.cis1200;

import org.cis1200.twentyfortyeight.GamePlay;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * You can use this file (and others) to test your
 * implementation.
 */

public class GameFrameTest {
    private GamePlay gamePlay = new GamePlay(4);

    /**
     * Test case to test lose game.
     * Current game board is full with numbers 1 - 10
     * which are all different from each neighbor.
     * So that no tiles can be merged at any direction.
     * Any move should make the game lose.
     * In this test case, we just try moving left.
     * Any other direction will generate the same result.
     */

    @Test
    public void testLoseCase() {
        int[][] arr = {{4, 1, 2, 1}, {5, 4, 1, 2}, {6, 5, 2, 3}, {2, 8, 3, 5}};
        gamePlay.setTiles(arr);
        gamePlay.move(GamePlay.Arrow.LEFT);
        Assertions.assertEquals(GamePlay.GameState.LOSE, gamePlay.state());

    }

    /**
     * Test case to test the game to be continue.
     * Current game board is full with numbers 1 - 10,
     * with 2 neighboring 4s on top of each other.
     * A vertical move will merge the two 4s into a 5,
     * which is less than 11.
     * So that the game can continue.
     * We just tested moving down, but the logic is the same for all directions.
     */

    @Test
    public void testContinueCase() {
        int[][] arr = {{4, 1, 2, 1}, {5, 4, 1, 2}, {6, 5, 3, 4}, {2, 8, 5, 4}};

        gamePlay.setTiles(arr);
        gamePlay.move(GamePlay.Arrow.DOWN);
        Assertions.assertEquals(GamePlay.GameState.CONTINUE, gamePlay.state());
    }

    /**
     * Test case to test win game.
     * Current game board is full with numbers 0 - 10, with 2 neighboring 10s on top of each other.
     * A vertical move will merge the two 10s into an 11, the winning number whose score is 2048.
     * We just tested moving up, but the logic is the same for all directions.
     */

    @Test
    public void testWinCase() {
        int[][] arr = {{10, 1, 2, 1}, {10, 4, 1, 2}, {6, 5, 2, 3}, {2, 8, 3, 5}};

        gamePlay.setTiles(arr);
        gamePlay.move(GamePlay.Arrow.UP);
        Assertions.assertEquals(GamePlay.GameState.WIN, gamePlay.state());
    }

    /**
     * Test case to test one edge case.
     * One row has 3 consecutive integers of the same value.
     * A horizontal move can only merge the two tiles at the end of the row
     * and the third one's value should remain the same.
     * We cannot compare the whole arrays after the move,
     * because a 1 or 2 will be randomly added to an empty tile.
     */

    @Test
    public void testEdgeCase1() {
        int[][] arr = {{0, 2, 2, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};

        gamePlay.setTiles(arr);
        gamePlay.move(GamePlay.Arrow.RIGHT);

        assertEquals(2, gamePlay.tiles()[0][2]);
        assertEquals(3, gamePlay.tiles()[0][3]);

    }

    /**
     * Test case to test another edge case.
     * One row has 4 consecutive integers of the same value.
     * We expect the first two tiles to merge and the last two tiles to merge also.
     */

    @Test
    public void testEdgeCase2() {
        int[][] arr = {{2, 2, 2, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};

        gamePlay.setTiles(arr);
        gamePlay.move(GamePlay.Arrow.RIGHT);

        assertEquals(3, gamePlay.tiles()[0][2]);
        assertEquals(3, gamePlay.tiles()[0][3]);

        //One more right move will cause the two new tiles to merge
        gamePlay.move(GamePlay.Arrow.RIGHT);

        assertEquals(4, gamePlay.tiles()[0][3]);
    }

    /**
     * Test case to test the score earned from each move.
     * Tile with value 3 has a score 8.
     * Two of such tiles merging will earn a score 16.
     * Tile with value 4 has a score 16.
     * Two of such tiles merging will earn a score 32.
     * First a horizontal move should earn 2 * 16 = 32.
     * Then one more vertical move should earn 1 * 32 = 32.
     * The array should not contain 1 or 2 because
     * the first move would generate a 1 or 2 at a random title,
     * which might make the second test move score unpredictable.
     */

    @Test
    public void testEarnedScore() {
        int[][] arr = {{3, 0, 3, 0}, {0, 0, 0, 0}, {0, 3, 0, 3}, {0, 0, 0, 0}};

        gamePlay.setTiles(arr);
        int score = gamePlay.move(GamePlay.Arrow.RIGHT);

        assertEquals(32, score);

        //One more right move will cause the two new tiles to merge
        score = gamePlay.move(GamePlay.Arrow.DOWN);

        assertEquals(32, score);
    }
}
