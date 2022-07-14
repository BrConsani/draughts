package br.unesp.draughts.components;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Piece extends JLabel {

    public final boolean isWhite;
    public boolean isKing;

    Piece(final boolean isWhite) {
        this.isWhite = isWhite;
        this.isKing = false;
        try {
            final BufferedImage image = ImageIO.read(new File(isWhite ? "res/white_piece.png" : "res/black_piece.png"));
            setIcon(new ImageIcon(image));
            setHorizontalAlignment(SwingConstants.CENTER);
        } catch (final IOException e) {
        }
    }

    public void promoveToKing() {
        isKing = true;
        try {
            BufferedImage image = ImageIO
                    .read(new File(isWhite ? "res/white_king_piece.png" : "res/black_king_piece.png"));
            setIcon(new ImageIcon(image));
        } catch (IOException e) {
        }
    }
}
