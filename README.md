# 2048
2048 code for CIS 1200 final project


=: Core Concepts :=

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D arrays
  2048 is played on a square board
  (I will have varying sizes from 4x4 to 8x8 that the player can choose to customize).
  I plan on using 2D arrays to store the tile data of the board and reflect the game board.
  The 2D array size is the chosen grid dimension by the chosen grid size dimension (4x4 to 8x8)
  with integer values from (0 to 11) corresponding to the indices of an array
  {0, 2, 4, 8,16, 32, 64, 128, 256, 512, 1024, 2048}.
  Note that the 0 means the empty tile.
  The 2D array will spawn a new tile (either 1 or 2)
  every time the arrow button is pressed and
  move the tiles in the corresponding direction if the board is not full and
  still has moves left before an 11 is reached.
  This array will be constantly updated with every move.
  The array starts with all 0s and two either 2 or 4,
  and ends when all values in the array are from 1 to 10 with no moves left, or an 11 is reached.

  2. Collections
  I will store each of the moves that occur during a game in Stack,
  a class in Java’s collection framework so that a user can undo and redo moves.
  Stack extends Vector which implements List which extends Collection.

  To add game board data, I will “push” it onto the stack.
  To retrieve the previous game board data, I will “pop” it from the stack.
  I choose to use the stack because of its “first in, last out” property,
  where the first data that is pushed into the stack will be the last one to be popped out.
  There will be two stacks, one for undo, and one for redo.
  Every game board data will be stored to the undo stack.
  Once the undo button is pressed, the current data is pushed to the redo stack.

  3. File I/O
  My 2048 game implementation will use File I/O to store the game data
  so the user can quit a game in the middle and reopen and continue to play it at a later time.
  The game board will have a new game button, and save game button.
  When the “save” button is pressed, the current data of the 2D array and
  the current score will be saved to the user’s chosen location.
  Whenever a player wants to load a saved game,
  my game will read the file and parse the serializable object data
  so the user can continue to play that game.
  The high score is also saved to a text file and will be updated
  whenever the user’s score is higher than the current high score.

  4. JUnit testable component
  My main class in the game is gamePlay, which is graphic independent.
  I will create unit tests that take in a 2D array with numbers,
  make a move in a certain direction, and update the game data accordingly,
  then verify the result against the expectation.
  I will design this functionality such that I can test it with JUnit.
  I will test that the 2D array is updated correctly and if the user reaches the 2048 tile,
  still has moves left, or fails when all tiles are filled up.
  I will also test that numbers are combined correctly,
  for instance if there are 3 or 4 of the same values in a row or column.
  I will also test that the score is calculated correctly.

  5. (Alternative/Bonus) An advanced topic
  I plan on incorporating the advanced topic - concurrency in my game.
  After each move on the game board,
  the updated 2D game board data must be checked to decide the game state - win, lose or continue.
  The traditional single thread method needs to loop through every row and column
  to check if the game board is still movable.
  Because the checking of each row or column is independent,
  I plan to use multiple threads to run the checking for each row or column
  so that the checking can save approximately 2*grid size of time.
  I plan to use an AtomicBoolean type of flag to reflect the result of the checking of movable.
  The AtomicBoolean is thread-safe so that it prevents race conditions/deadlock/livelock.


=: Your Implementation :=


- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
Class GameFrame:
This class is the graphic starting page of the game.
It describes who makes the game and how to play the game.
It provides user with the options of setting the grid size with text field input or radio buttons.
At this page user can start a new game by clicking “New Game” button or
continue with a previously saved game by clicking “Load Game” button.
The code handles the synchronization between the grid size text field and radio buttons.
Once the button “Load Game” or “New Game” is clicked,
an instance of GameBoard will be created and user can start a game.

Class Game:
This class contains the main method run to start and run the game.
It initializes the runnable game class of RunTwentyfortyeight and runs it.

Class RunTwentyfortyeight:
This class calls the gameFrame class that specifies the frame and widgets of the GUI.
It is created to implement runnable for the project requirement.

Class CameContent:
This class implements java.io.Serializable.
It encapsulates the data of a gameboard, including the current score,
the grid size and the values of the 2D grid/array.
Saving or loading a game can be simply done with
ObjectInputStream.writeObject or ObjectOutputStream.readObject method.

Class GameBoard:
This class defines graphic content of a game (or called game board) and all the event handling.
It displays each grid image (cookie) corresponding to its value of the current game board and
user’s current score (My Cookie Coins) and the best score (Best Cookie Coins).
It provides buttons for user to undo or redo a step, save a game or start a new game.
The code provides functions to handle the key actions.
Once user press an arrow key, the code calls corresponding GamePlay move,
then it checks the updated game state.
If there is any grid reaching value 2048 it displays “You Win” gif;
if the grids are full and no more move can go and no grid reaches 2048 it display “Game Over” gif;
otherwise, the game will continue.
After every move the graphic content of the game board and scores will be updated.
Undo and redo are implemented with Collection Stack.
After every move the stacks are updated correspondingly.
Clicking “Undo”/”Redo” button will load the last state in the stacks and
refresh the graphic content of the game board and scores.
Once my current score is updated, it compares with the best score.
If my current score is higher than the best score, the best score will be re-set to this higher value.
The best score is also stored in a text format file for tracking the all-time best score.

Class GamePlay:
This class is responsible for the computations of game board move.
It gets the new values of the grids, the score gained from the move,
the state (Win, Lose or Continue) after the move.
The functions include merging of the grids of the 2D array,
randomly generating new grid values at certain locations, game state checking.
In order to save time we use multi-threading concurrence method for game state checking,
which can save approximately 2*grid size of time.

Class GameFrameTest:
This class contains the JUnit testable components and tests the state,
combining of the tiles, and score. It has 3 tests for the states of the game,
which are the win, continue, and losing states.
It also has 2 tests for the edge cases of combining the tiles after a key is pressed;
the 2 tests are 3 of the same value in a row, and 4 of the same value in a row.
The last test is to check that the score is calculated correctly.
