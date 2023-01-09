package org.cis1200.twentyfortyeight;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class GameFrame extends JFrame {

    private JPanel contentPane;
    private JTextField textFieldGridSize;
    private int gridSize = 4;

    /**
     * Launch the application.
     */

    /*
    public static void main(String[] args) {
        //Runnable game = new org.cis1200.twentyfourtyeight.RunYourGame();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Create an instance of Game class
                    GameFrame gameFrame = new GameFrame();
                    // Display the game setting window
                    gameFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    */

    /**
     * Create the frame.
     */
    public GameFrame() {
        setResizable(false);
        // Display on the top of the window
        setTitle("Cookie Coin 2048");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(300, 200, 480, 360);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        //Main display
        JLabel lblAuthor = new JLabel("This game is design by Cynthia Zhang for CIS 120");
        lblAuthor.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
        lblAuthor.setBounds(37,6,410, 16);
        lblAuthor.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblAuthor);

        JLabel lblHowToPlay1 = new JLabel("HOW TO PLAY: ");
        lblHowToPlay1.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        lblHowToPlay1.setBounds(37,59,149, 16);
        contentPane.add(lblHowToPlay1);

        // Use html to display multiple lines within label box (the width is set/fixed)
        JLabel lblHowToPlay2 = new JLabel("<html>&emsp;Use your arrow keys to move the tiles. " +
                "When two tiles with the same number touch, they merge into one. " +
                "When a tile reaches 2048 you win!</html>");
        lblHowToPlay2.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
        lblHowToPlay2.setBounds(37, 80, 410, 87);
        contentPane.add(lblHowToPlay2);

        // Only allow from 4x4 up to 8X8, otherwise,
        // the screen becomes too busy with too many grids/tiles
        JLabel lblGridSize = new JLabel("Grid Size ( 4 to 8 ):");
        lblGridSize.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
        lblGridSize.setBounds(37, 179, 155, 27);
        contentPane.add(lblGridSize);

        //Text box for user to input grid size
        textFieldGridSize = new JTextField();
        textFieldGridSize.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldGridSize.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
        textFieldGridSize.setText("4"); // Set default to 4X4
        textFieldGridSize.setBounds(216, 179, 31, 26);
        contentPane.add(textFieldGridSize);
        textFieldGridSize.setColumns(10);

        //Radio buttons of grid size
        JRadioButton rdbtn4 = new JRadioButton("4 X 4");
        JRadioButton rdbtn5 = new JRadioButton("5 X 5");
        JRadioButton rdbtn6 = new JRadioButton("6 X 6");
        JRadioButton rdbtn7 = new JRadioButton("7 X 7");
        JRadioButton rdbtn8 = new JRadioButton("8 X 8");

        // Pop up a window to open a saved game
        JButton btnLoadGame = new JButton("Load Game");
        btnLoadGame.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnLoadGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Load a game"); //Window title
                //Gets the current directory of the user
                String dir = System.getProperty("user.dir");
                //Sets fileChoose to current directory
                fileChooser.setCurrentDirectory(new File(dir));
                //0 if open, 1 if cancel
                int result = fileChooser.showOpenDialog(contentPane);
                //Opens file, when "open button is clicked", equals to 0
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    try {
                        FileInputStream fileIn = new FileInputStream(selectedFile);
                        ObjectInputStream in = new ObjectInputStream(fileIn);
                        //Loads in saved content as GameContent
                        GameContent myGame = (GameContent)in.readObject();
                        in.close();
                        fileIn.close();

                        setVisible(false);
                        dispose();

                        //Converts GameContent to a new GameBoard
                        GameBoard gameBoard = new GameBoard(myGame.gridSize(),
                                myGame.score(), myGame.tiles());
                        gameBoard.setVisible(true);

                        //Exceptions, prevents crashes
                    } catch (IOException ex) {
                        System.out.println("Error while loading game content");
                        ex.printStackTrace();
                    } catch (ClassNotFoundException c) {
                        System.out.println("GameContent class not found");
                        c.printStackTrace();
                    }
                }
            }
        });
        btnLoadGame.setBounds(347, 179, 100, 50);
        contentPane.add(btnLoadGame);

        // Start a new game.
        // When pressing down this button,
        // refresh the radio buttons according to the text of grid size
        JButton btnNewGame = new JButton("New Game");
        btnNewGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String strSize = textFieldGridSize.getText();
                gridSize = Integer.parseInt(strSize);
                // Radio button: when a valid 4-8 number is inputed to the text box,
                // and new game button is pressed down,
                // radio button is activated to reflect number
                rdbtn4.setSelected(false);
                rdbtn5.setSelected(false);
                rdbtn6.setSelected(false);
                rdbtn7.setSelected(false);
                rdbtn8.setSelected(false);

                if (gridSize == 4) {
                    rdbtn4.setSelected(true);
                } else if (gridSize == 5) {
                    rdbtn5.setSelected(true);
                } else if (gridSize == 6) {
                    rdbtn6.setSelected(true);
                } else if (gridSize == 7) {
                    rdbtn7.setSelected(true);
                } else if (gridSize == 8) {
                    rdbtn8.setSelected(true);
                }

                contentPane.updateUI();
            }
        });

        //New game button
        btnNewGame.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String strSize = textFieldGridSize.getText();
                if (strSize.compareTo("4") == 0 ||
                        strSize.compareTo("5") == 0 ||
                        strSize.compareTo("6") == 0 ||
                        strSize.compareTo("7") == 0 ||
                        strSize.compareTo("8") == 0)  {
                    try {
                        setVisible(false);
                        dispose();

                        GameBoard gameBoard = new GameBoard(gridSize, 0, null);
                        gameBoard.setVisible(true);

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(contentPane, "Invalid grid size! " +
                            "Please enter a number between 4 and 8",
                            "Invalide Grid Size",
                            JOptionPane.ERROR_MESSAGE);
                    textFieldGridSize.setText("4");
                    rdbtn4.setSelected(true);
                    rdbtn5.setSelected(false);
                    rdbtn6.setSelected(false);
                    rdbtn7.setSelected(false);
                    rdbtn8.setSelected(false);
                }
            }
        });
        btnNewGame.setBounds(347, 252, 100, 50);
        contentPane.add(btnNewGame);

        //Press "4" Radio Button
        rdbtn4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rdbtn4.setSelected(true);
                rdbtn5.setSelected(false);
                rdbtn6.setSelected(false);
                rdbtn7.setSelected(false);
                rdbtn8.setSelected(false);
                textFieldGridSize.setText("4");
            }
        });
        rdbtn4.setBounds(33, 220, 141, 23);
        rdbtn4.setSelected(true);
        contentPane.add(rdbtn4);

        //Press "5" Radio Button
        rdbtn5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rdbtn4.setSelected(false);
                rdbtn5.setSelected(true);
                rdbtn6.setSelected(false);
                rdbtn7.setSelected(false);
                rdbtn8.setSelected(false);
                textFieldGridSize.setText("5");
            }
        });
        rdbtn5.setBounds(167, 220, 141, 23);
        contentPane.add(rdbtn5);

        //Press "6" Radio Button
        rdbtn6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rdbtn4.setSelected(false);
                rdbtn5.setSelected(false);
                rdbtn6.setSelected(true);
                rdbtn7.setSelected(false);
                rdbtn8.setSelected(false);
                textFieldGridSize.setText("6");
            }
        });
        rdbtn6.setBounds(33, 250, 141, 23);
        contentPane.add(rdbtn6);

        //Press "7" Radio Button
        rdbtn7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rdbtn4.setSelected(false);
                rdbtn5.setSelected(false);
                rdbtn6.setSelected(false);
                rdbtn7.setSelected(true);
                rdbtn8.setSelected(false);
                textFieldGridSize.setText("7");
            }
        });
        rdbtn7.setBounds(167, 250, 141, 23);
        contentPane.add(rdbtn7);

        //Press "8" Radio Button
        rdbtn8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rdbtn4.setSelected(false);
                rdbtn5.setSelected(false);
                rdbtn6.setSelected(false);
                rdbtn7.setSelected(false);
                rdbtn8.setSelected(true);
                textFieldGridSize.setText("8");
            }
        });
        rdbtn8.setBounds(33, 280, 141, 23);
        contentPane.add(rdbtn8);
    }
}

