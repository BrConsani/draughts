package br.unesp.draughts;

import javax.swing.JFrame;

import br.unesp.draughts.components.GameBoard;

public class App extends JFrame {
    public App() {
        setTitle("Draughts");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().add(new GameBoard());
    }

    public static void main(final String[] args) {
        final JFrame frame = new App();
        frame.pack();
        frame.setVisible(true);
    }
}