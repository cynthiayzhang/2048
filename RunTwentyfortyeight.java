package org.cis1200.twentyfortyeight;
import javax.swing.*;

public class RunTwentyfortyeight implements Runnable {
    public void run() {
        final JFrame frame = new GameFrame();
        frame.setVisible(true);
    }
}
