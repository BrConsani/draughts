package br.unesp.draughts.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import java.lang.Math;

public class GameBoard extends JLayeredPane implements MouseListener, MouseMotionListener {

    private static final int BOARD_SIZE = 10;

    private final JComponent[][] pieces = new JComponent[BOARD_SIZE][BOARD_SIZE];
    private JLabel board;
    private Piece selectedPiece;
    private boolean isWhiteTurn = true;

    public GameBoard() {
        setPreferredSize(new Dimension(640, 640));
        initializeBoard();
        revalidateBoard();
    }

    private void initializeBoard() {
        try {
            final BufferedImage boardImage = ImageIO.read(new File("res/board.jpg"));
            board = new JLabel(new ImageIcon(boardImage));

            add(board, JLayeredPane.DEFAULT_LAYER);

            addMouseListener(this);
            addMouseMotionListener(this);

            board.addMouseListener(this);
            board.addMouseMotionListener(this);

            board.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
            board.setPreferredSize(new Dimension(640, 640));
            board.setBounds(0, 0, 640, 640);
        } catch (final IOException e) {
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                pieces[i][j] = new JLabel();
                if (i == 1 || i == 3) {
                    if (j % 2 == 0) {
                        pieces[i][j] = new Piece(false);
                    }
                } else if (i == 0 || i == 2) {
                    if (j % 2 != 0) {
                        pieces[i][j] = new Piece(false);
                    }

                } else if (i == 7 || i == 9) {
                    if (j % 2 == 0) {
                        pieces[i][j] = new Piece(true);
                    }

                } else if (i == 6 || i == 8) {
                    if (j % 2 != 0) {
                        pieces[i][j] = new Piece(true);

                    }
                }
            }
        }
    }

    private void revalidateBoard() {
        board.removeAll();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board.add(pieces[i][j]);
            }
        }
        board.revalidate();
    }

    private void movePieceToPosition(final Piece piece, final Component position) {
        List<Point> allowedPositions = getAllowedPositions(piece);
        Point toGoPosition = getComponentPosition(position);

        if (allowedPositions.contains(toGoPosition)) {
            Point piecePosition = getComponentPosition(piece);

            pieces[piecePosition.x][piecePosition.y] = new JLabel();
            pieces[toGoPosition.x][toGoPosition.y] = piece;

            if (Math.abs(piecePosition.x - toGoPosition.x) == 2 && Math.abs(piecePosition.y - toGoPosition.y) == 2) {
                int pieceXPosition = piecePosition.x + (toGoPosition.x - piecePosition.x) / 2;
                int pieceYPosition = piecePosition.y + (toGoPosition.y - piecePosition.y) / 2;

                pieces[pieceXPosition][pieceYPosition] = new JLabel();
            }

            checkIfPieceWasPromoted(piece, toGoPosition);

            switchTurn();
        }
    }

    private Point getComponentPosition(final Component piece) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (pieces[i][j] == piece) {
                    return new Point(i, j);
                }
            }
        }
        return new Point(0, 0);
    }

    private boolean hasPieceInPosition(Point point) {
        return pieces[point.x][point.y] instanceof Piece;
    }

    private boolean pieceInPositionIsWhite(Point point) {
        if (!(pieces[point.x][point.y] instanceof Piece)) {
            return false;
        }

        return ((Piece) pieces[point.x][point.y]).isWhite;
    }

    private List<Point> getAllowedPositions(final Piece piece) {
        final Point piecePosition = getComponentPosition(piece);

        List<Point> allowedPoints = new ArrayList<Point>();

        if (piece.isWhite) {
            if (piecePosition.x == 0) {
                return allowedPoints;
            }

            if (piecePosition.y > 0) {
                Point positionToGo = new Point(piecePosition.x - 1, piecePosition.y - 1);
                if (!hasPieceInPosition(positionToGo)) {
                    allowedPoints.add(positionToGo);
                } else if (piecePosition.y > 1 && piecePosition.x > 1 && !pieceInPositionIsWhite(positionToGo)) {
                    positionToGo = new Point(piecePosition.x - 2, piecePosition.y - 2);
                    if (!hasPieceInPosition(positionToGo)) {
                        allowedPoints.add(positionToGo);
                    }
                }
            }

            if (piecePosition.y != (BOARD_SIZE - 1)) {
                Point positionToGo = new Point(piecePosition.x - 1, piecePosition.y + 1);
                if (!hasPieceInPosition(positionToGo)) {
                    allowedPoints.add(positionToGo);
                } else if (piecePosition.y != (BOARD_SIZE - 2) && piecePosition.x > 1
                        && !pieceInPositionIsWhite(positionToGo)) {
                    positionToGo = new Point(piecePosition.x - 2, piecePosition.y + 2);
                    if (!hasPieceInPosition(positionToGo)) {
                        allowedPoints.add(positionToGo);
                    }
                }
            }

            return allowedPoints;
        } else {
            if (piecePosition.x == (BOARD_SIZE - 1)) {
                return allowedPoints;
            }

            if (piecePosition.y > 0) {
                Point positionToGo = new Point(piecePosition.x + 1, piecePosition.y - 1);
                if (!hasPieceInPosition(positionToGo)) {
                    allowedPoints.add(positionToGo);
                } else if (piecePosition.y > 1 && piecePosition.x < (BOARD_SIZE - 2)
                        && pieceInPositionIsWhite(positionToGo)) {
                    positionToGo = new Point(piecePosition.x + 2, piecePosition.y - 2);
                    if (!hasPieceInPosition(positionToGo)) {
                        allowedPoints.add(positionToGo);
                    }
                }
            }

            if (piecePosition.y != (BOARD_SIZE - 1)) {
                Point positionToGo = new Point(piecePosition.x + 1, piecePosition.y + 1);
                if (!hasPieceInPosition(positionToGo)) {
                    allowedPoints.add(positionToGo);
                } else if (piecePosition.x < (BOARD_SIZE - 2) && piecePosition.y < (BOARD_SIZE - 2)
                        && pieceInPositionIsWhite(positionToGo)) {
                    positionToGo = new Point(piecePosition.x + 2, piecePosition.y + 2);
                    if (!hasPieceInPosition(positionToGo)) {
                        allowedPoints.add(positionToGo);
                    }
                }
            }

            return allowedPoints;
        }
    }

    private void showAllowedPoints(List<Point> points) {
        for (Point point : points) {
            pieces[point.x][point.y] = new AllowedPosition();
        }
        revalidateBoard();
    }

    private void removeAllowedPoints() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (pieces[i][j] instanceof AllowedPosition) {
                    pieces[i][j] = new JLabel();
                }
            }
        }
        revalidateBoard();
    }

    private void switchTurn() {
        isWhiteTurn = !isWhiteTurn;
    }

    private void checkIfPieceWasPromoted(final Piece piece, final Point position) {
        if (piece.isWhite && position.x == 0) {
            piece.promoveToKing();
        } else if (!piece.isWhite && position.x == (BOARD_SIZE - 1)) {
            piece.promoveToKing();
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (selectedPiece == null) {
            return;
        }

        selectedPiece.setLocation(e.getX() - selectedPiece.getWidth() / 2, e.getY() - selectedPiece.getHeight() / 2);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        selectedPiece = null;

        final Component piece = board.findComponentAt(e.getX(), e.getY());

        if (!(piece instanceof Piece)) {
            return;
        }

        if (((Piece) piece).isWhite != isWhiteTurn) {
            return;
        }

        selectedPiece = (Piece) piece;

        selectedPiece.setLocation(e.getX() - selectedPiece.getWidth() / 2, e.getY() - selectedPiece.getHeight() / 2);
        selectedPiece.setSize(selectedPiece.getWidth(), selectedPiece.getHeight());

        List<Point> points = getAllowedPositions(selectedPiece);
        showAllowedPoints(points);

        int index = Arrays.asList(board.getComponents()).indexOf(selectedPiece);
        board.add(new JLabel(), index);
        add(selectedPiece, JLayeredPane.DRAG_LAYER);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (selectedPiece == null) {
            return;
        }

        final Component positionToGo = board.findComponentAt(e.getX(), e.getY());

        if (positionToGo != board) {
            movePieceToPosition(selectedPiece, positionToGo);
        }

        removeAllowedPoints();

        revalidateBoard();
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }
}
