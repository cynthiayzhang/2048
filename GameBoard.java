package org.cis1200.twentyfortyeight;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.SwingConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GameBoard extends JFrame {
    private JPanel contentPane;
    private JLabel lblBestScore;
    private JTextField textFieldBestScore;
    private int bestScore = 0;
    private JLabel lblMyScore;
    private JTextField textFieldMyScore;
    private int myScore = 0;
    private JButton btnUndo;
    private JButton btnRedo;

    //Design
    private JLabel[][] aLblTiles;
    private int[][] tiles;
    private int gridSize;
    private int x0 = 80;
    private int y0 = 100;
    private int border = 10;
    private int tileSize = 120;
    private GamePlay gamePlay;
    private boolean bKeyReleased = true;

    private Stack<Integer> sUndoScore = new Stack<Integer>();
    private Stack<Integer> sRedoScore = new Stack<Integer>();
    private Stack<Integer> sUndoBestScore = new Stack<Integer>();
    private Stack<Integer> sRedoBestScore = new Stack<Integer>();
    private Stack<int[][]> sUndoTiles = new Stack<int[][]>();
    private Stack<int[][]> sRedoTiles = new Stack<int[][]>();

    //Tile graphics
    final private ImageIcon[] imgs = {new ImageIcon("images/tile0000.png"),
        new ImageIcon("images/tile0002.png"),
        new ImageIcon("images/tile0004.png"),
        new ImageIcon("images/tile0008.png"),
        new ImageIcon("images/tile0016.png"),
        new ImageIcon("images/tile0032.png"),
        new ImageIcon("images/tile0064.png"),
        new ImageIcon("images/tile0128.png"),
        new ImageIcon("images/tile0256.png"),
        new ImageIcon("images/tile0512.png"),
        new ImageIcon("images/tile1024.png"),
        new ImageIcon("images/tile2048.png")};

    private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            //response to key pressed, released, type event
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (bKeyReleased) {
                    bKeyReleased = false;

                    //stack
                    sUndoTiles.push(tiles);
                    sUndoScore.push(myScore);
                    sUndoBestScore.push(bestScore);

                    GamePlay.Movable.setMovableFlag(false);

                    //gets score, moves tiles
                    int score = 0;
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        score = gamePlay.move(GamePlay.Arrow.LEFT);
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        score = gamePlay.move(GamePlay.Arrow.RIGHT);
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        score = gamePlay.move(GamePlay.Arrow.UP);
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        score = gamePlay.move(GamePlay.Arrow.DOWN);
                    }

                    //update graphics
                    tiles = gamePlay.tiles();
                    for (int row = 0; row < gridSize; row++) {
                        for (int col = 0; col < gridSize; col++) {
                            contentPane.remove(aLblTiles[row][col]); //remove graphics
                            int idx = tiles[row][col];
                            aLblTiles[row][col].setIcon(imgs[idx]); //put new graphics
                            contentPane.add(aLblTiles[row][col]);
                        }
                    }

                    myScore += score;
                    textFieldMyScore.setText(String.valueOf(myScore));
                    //high score
                    if (myScore > bestScore) {
                        bestScore = myScore;
                        textFieldBestScore.setText(String.valueOf(bestScore));
                    }
                    //enable undo button
                    btnUndo.setEnabled(true);

                    //State checking
                    GamePlay.GameState state = gamePlay.state();

                    //if win, remove graphics, win gif
                    if (state == GamePlay.GameState.WIN) {
                        for (int row = 0; row < gridSize; row++) {
                            for (int col = 0; col < gridSize; col++) {
                                //aLblTiles[row][col].setOpaque(false);
                                contentPane.remove(aLblTiles[row][col]);
                            }
                        }

                        contentPane.remove(btnUndo);
                        contentPane.remove(btnRedo);

                        JLabel youWin = new JLabel(new ImageIcon("images/YouWin.gif"));
                        Rectangle rec = contentPane.getBounds();
                        youWin.setBounds(rec.width / 2 - 100, rec.height / 2 - 100, 200, 200);
                        youWin.setOpaque(true);
                        contentPane.add(youWin);
                    } else if (state == GamePlay.GameState.LOSE) {
                        //if lose, remove tiles, game over gif
                        for (int row = 0; row < gridSize; row++) {
                            for (int col = 0; col < gridSize; col++) {
                                //aLblTiles[row][col].setOpaque(false);
                                contentPane.remove(aLblTiles[row][col]);
                            }
                        }

                        contentPane.remove(btnUndo);
                        contentPane.remove(btnRedo);

                        JLabel youLose = new JLabel(new ImageIcon("images/GameOver.gif"));
                        Rectangle rec = contentPane.getBounds();
                        youLose.setBounds(rec.width / 2 - 240, rec.height / 2 - 61, 480, 132);
                        youLose.setOpaque(true);
                        contentPane.add(youLose);
                    }

                    contentPane.updateUI(); //updates the board
                }
                //cannot press and hold key
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                bKeyReleased = true;
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
                System.out.println();
            }
            return false;
        }
    }

    /**
     * Create the frame.
     */
    public GameBoard(int size, int score, int[][] arr) {
        setResizable(false);
        String strBestScore = "files/bestscore.txt";

        try {
            File fileBestScore = new File(strBestScore);
            if (fileBestScore.exists()) {
                Scanner reader = new Scanner(fileBestScore);
                if (reader.hasNextLine()) {
                    String bs = reader.nextLine();
                    bestScore = Integer.parseInt(bs);
                }
                reader.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading best score.");
            e.printStackTrace();
        }

        // Save best score when closing the game
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    FileWriter writer = new FileWriter(strBestScore);
                    writer.write(String.valueOf(bestScore));
                    writer.close();
                    System.out.println("Successfully wrote best score to files/bestscore.txt");
                } catch (IOException ex) {
                    System.out.println("An error occurred while saving best score.");
                    ex.printStackTrace();
                }
            }
        });

        gridSize = size;
        myScore = score;
        gamePlay = new GamePlay(gridSize);

        if (arr == null) { //for new game case
            tiles = gamePlay.tiles();
        } else { //for load game case
            tiles = arr;
            gamePlay.setTiles(tiles);
        }

        String frmTitle = "Cookie Coin 2048 ";
        frmTitle += String.valueOf(gridSize);
        frmTitle += "X";
        frmTitle += String.valueOf(gridSize);
        setTitle(frmTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //frame width and height sizes for grids
        int frmWidth = 840;
        int frmHeight = 860;

        if (gridSize == 4) {
            frmHeight = 640;
            x0 = 190;
        } else if (gridSize == 5) {
            frmHeight = 750;
            x0 = 135;
        } else if (gridSize == 7) {
            frmWidth = 950;
            frmHeight = 970;
        } else if (gridSize == 8) {
            frmWidth = 950;
            frmHeight = 1000;
            x0 = 30;
            y0 = 70;
        }

        setBounds(300, 100, frmWidth, frmHeight);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        //new game
        JButton btnNewGame = new JButton("New Game");
        btnNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    try {
                        FileWriter writer = new FileWriter(strBestScore);
                        writer.write(String.valueOf(bestScore));
                        writer.close();
                        System.out.println("Successfully wrote best score to files/bestscore.txt");
                    } catch (IOException ex) {
                        System.out.println("An error occurred while saving best score.");
                        ex.printStackTrace();
                    }

                    GameBoard gameBoard = new GameBoard(gridSize, 0, null);
                    gameBoard.setVisible(true);
                    setVisible(false); //closes old one
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });

        btnNewGame.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnNewGame.setBounds(31, 10, 100, 50);
        contentPane.add(btnNewGame);

        //save game button
        JButton btnSaveGame = new JButton("Save Game");
        btnSaveGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    GameContent myGame = new GameContent(myScore, gridSize, tiles);

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save the game");
                    String dir = System.getProperty("user.dir");
                    fileChooser.setCurrentDirectory(new File(dir));
                    int userSelection = fileChooser.showSaveDialog(contentPane);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();

                        FileOutputStream fileOut = new FileOutputStream(fileToSave);
                        ObjectOutputStream out = new ObjectOutputStream(fileOut);
                        out.writeObject(myGame);
                        out.close();
                        fileOut.close();
                    }
                } catch (IOException ex) {
                    System.out.println("Error while saving game content");
                    ex.printStackTrace();
                }
            }
        });
        btnSaveGame.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnSaveGame.setBounds(143, 10, 100, 50);
        contentPane.add(btnSaveGame);

        //undo and redo buttons, stack manipulation
        btnUndo = new JButton("<< Undo");
        btnRedo = new JButton(" Redo >>");

        btnUndo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //put current state into redo stack
                sRedoTiles.push(tiles);
                sRedoScore.push(myScore);
                sRedoBestScore.push(bestScore);
                btnRedo.setEnabled(true);

                //retrieve previous stack
                tiles = sUndoTiles.pop();
                myScore = sUndoScore.pop();
                bestScore = sUndoBestScore.pop();

                btnUndo.setEnabled(!sUndoTiles.empty());//No more steps to undo

                gamePlay.setTiles(tiles);

                for (int row = 0; row < gridSize; row++) {
                    for (int col = 0; col < gridSize; col++) {
                        contentPane.remove(aLblTiles[row][col]);
                        int idx = tiles[row][col];
                        aLblTiles[row][col].setIcon(imgs[idx]);
                        contentPane.add(aLblTiles[row][col]);
                    }
                }
                textFieldMyScore.setText(String.valueOf(myScore));
                textFieldBestScore.setText(String.valueOf(bestScore));

                contentPane.updateUI();    //updates game
            }
        });
        //aesthetics
        btnUndo.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        if (gridSize > 6) {
            btnUndo.setBounds(338, 10, 100, 50);
        } else {
            btnUndo.setBounds(283, 10, 100, 50);
        }
        btnUndo.setEnabled(!sUndoTiles.empty());

        contentPane.add(btnUndo);

        //press redo, set current state to undo stack
        btnRedo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sUndoTiles.push(tiles);
                sUndoScore.push(myScore);
                sUndoBestScore.push(bestScore);
                btnUndo.setEnabled(true);

                tiles = sRedoTiles.pop();
                myScore = sRedoScore.pop();
                bestScore = sRedoBestScore.pop();

                btnRedo.setEnabled(!sRedoTiles.empty());

                gamePlay.setTiles(tiles);

                for (int row = 0; row < gridSize; row++) {
                    for (int col = 0; col < gridSize; col++) {
                        contentPane.remove(aLblTiles[row][col]);
                        int idx = tiles[row][col];
                        aLblTiles[row][col].setIcon(imgs[idx]);
                        contentPane.add(aLblTiles[row][col]);
                    }
                }
                textFieldMyScore.setText(String.valueOf(myScore));
                textFieldBestScore.setText(String.valueOf(bestScore));

                contentPane.updateUI();
            }
        });

        //redo
        btnRedo.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        if (gridSize > 6) {
            btnRedo.setBounds(448, 10, 100, 50);
        } else {
            btnRedo.setBounds(393, 10, 100, 50);
        }
        btnRedo.setEnabled(!sRedoTiles.empty());
        contentPane.add(btnRedo);

        //current score
        lblMyScore = new JLabel("My Cookie Coins");
        lblMyScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblMyScore.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        if (gridSize > 6) {
            lblMyScore.setBounds(640, 10, 135, 20);
        } else {
            lblMyScore.setBounds(530, 10, 135, 20);
        }

        contentPane.add(lblMyScore);

        textFieldMyScore = new JTextField();
        textFieldMyScore.setEditable(false);
        textFieldMyScore.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldMyScore.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
        if (gridSize > 6) {
            textFieldMyScore.setBounds(640, 34, 135, 26);
        } else {
            textFieldMyScore.setBounds(530, 34, 135, 26);
        }
        if (myScore > 0) {
            textFieldMyScore.setText(String.valueOf(myScore));
        }

        contentPane.add(textFieldMyScore);
        textFieldMyScore.setColumns(10);

        //high score
        lblBestScore = new JLabel("Best Cookie Coins");
        lblBestScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblBestScore.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        if (gridSize > 6) {
            lblBestScore.setBounds(790, 10, 135, 20);
        } else {
            lblBestScore.setBounds(680, 10, 135, 20);
        }

        contentPane.add(lblBestScore);

        textFieldBestScore = new JTextField();
        textFieldBestScore.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldBestScore.setEditable(false);
        textFieldBestScore.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
        if (gridSize > 6) {
            textFieldBestScore.setBounds(790, 34, 135, 26);
        } else {
            textFieldBestScore.setBounds(680, 34, 135, 26);
        }
        if (bestScore > 0) {
            textFieldBestScore.setText(String.valueOf(bestScore));
        }

        contentPane.add(textFieldBestScore);
        textFieldBestScore.setColumns(10);

        //define keyboard to enable keyboard event listener
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

        //display tiles
        aLblTiles = new JLabel[gridSize][];
        for (int row = 0; row < gridSize; row++) {
            aLblTiles[row] = new JLabel[gridSize];
            for (int col = 0; col < gridSize; col++) {
                aLblTiles[row][col] = new JLabel();
                int idx = tiles[row][col];
                aLblTiles[row][col].setIcon(imgs[idx]);
                aLblTiles[row][col].setBounds(x0 + (tileSize - border) * col,
                        y0 + (tileSize - border) * row, tileSize, tileSize);
                aLblTiles[row][col].setBorder(
                        BorderFactory.createLineBorder(Color.decode("#B67721"),
                        border, true));
                contentPane.add(aLblTiles[row][col]);
            }
        }

    }

}
