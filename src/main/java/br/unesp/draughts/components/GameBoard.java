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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        List<Point> movePoints = new ArrayList<Point>();
        List<Point> capturePoints = new ArrayList<Point>();

        if (piece.isWhite) {
            boolean canMoveUp = piecePosition.x - 1 > 0;

            if (canMoveUp) {
                Point leftPoint = new Point(piecePosition.x - 1, piecePosition.y - 1);

                if (pointIsInBoard(leftPoint)) {
                    if (hasPieceInPosition(leftPoint)
                            && !pieceInPositionIsWhite(leftPoint)) {
                        leftPoint = new Point(piecePosition.x - 2, piecePosition.y - 2);
                        if (pointIsInBoard(leftPoint) && !hasPieceInPosition(leftPoint)) {
                            capturePoints.add(leftPoint);
                        }
                    } else if (!hasPieceInPosition(leftPoint)) {
                        movePoints.add(leftPoint);
                    }
                }

                Point rightPoint = new Point(piecePosition.x - 1, piecePosition.y + 1);

                if (pointIsInBoard(rightPoint)) {
                    if (hasPieceInPosition(rightPoint)
                            && !pieceInPositionIsWhite(rightPoint)) {
                        rightPoint = new Point(piecePosition.x - 2, piecePosition.y + 2);
                        if (pointIsInBoard(rightPoint) && !hasPieceInPosition(rightPoint)) {
                            capturePoints.add(rightPoint);
                        }
                    } else if (!hasPieceInPosition(rightPoint)) {
                        movePoints.add(rightPoint);
                    }
                }
            }

            boolean canMoveBottom = piecePosition.x + 1 < (BOARD_SIZE - 1);

            if (canMoveBottom) {
                Point leftPoint = new Point(piecePosition.x + 1, piecePosition.y - 1);

                if (pointIsInBoard(leftPoint)) {
                    if (hasPieceInPosition(leftPoint) && !pieceInPositionIsWhite(leftPoint)) {
                        leftPoint = new Point(piecePosition.x + 2, piecePosition.y - 2);
                        if (pointIsInBoard(leftPoint) && !hasPieceInPosition(leftPoint)) {
                            capturePoints.add(leftPoint);
                        }
                    } else if (!hasPieceInPosition(leftPoint) && piece.isKing) {
                        movePoints.add(leftPoint);
                    }
                }

                Point rightPoint = new Point(piecePosition.x + 1, piecePosition.y + 1);

                if (pointIsInBoard(rightPoint)) {
                    if (hasPieceInPosition(rightPoint) && !pieceInPositionIsWhite(rightPoint)) {
                        rightPoint = new Point(piecePosition.x + 2, piecePosition.y + 2);
                        if (pointIsInBoard(rightPoint) && !hasPieceInPosition(rightPoint)) {
                            capturePoints.add(rightPoint);
                        }
                    } else if (!hasPieceInPosition(rightPoint) && piece.isKing) {
                        movePoints.add(rightPoint);
                    }
                }
            }

            return capturePoints.isEmpty() ? movePoints : capturePoints;
        } else {
            boolean canMoveUp = piecePosition.x - 1 > 0;

            if (canMoveUp) {
                Point leftPoint = new Point(piecePosition.x - 1, piecePosition.y - 1);

                if (pointIsInBoard(leftPoint)) {
                    if (hasPieceInPosition(leftPoint) && pieceInPositionIsWhite(leftPoint)) {
                        leftPoint = new Point(piecePosition.x - 2, piecePosition.y - 2);
                        if (pointIsInBoard(leftPoint) && !hasPieceInPosition(leftPoint)) {
                            capturePoints.add(leftPoint);
                        }
                    } else if (!hasPieceInPosition(leftPoint) && piece.isKing) {
                        movePoints.add(leftPoint);
                    }
                }

                Point rightPoint = new Point(piecePosition.x - 1, piecePosition.y + 1);

                if (pointIsInBoard(rightPoint)) {
                    if (hasPieceInPosition(rightPoint) && pieceInPositionIsWhite(rightPoint)) {
                        rightPoint = new Point(piecePosition.x - 2, piecePosition.y + 2);
                        if (pointIsInBoard(rightPoint) && !hasPieceInPosition(rightPoint)) {
                            capturePoints.add(rightPoint);
                        }
                    } else if (!hasPieceInPosition(rightPoint) && piece.isKing) {
                        movePoints.add(rightPoint);
                    }
                }
            }

            boolean canMoveBottom = piecePosition.x + 1 < (BOARD_SIZE - 1);

            if (canMoveBottom) {
                Point leftPoint = new Point(piecePosition.x + 1, piecePosition.y - 1);

                if (pointIsInBoard(leftPoint)) {
                    if (hasPieceInPosition(leftPoint) && pieceInPositionIsWhite(leftPoint)) {
                        leftPoint = new Point(piecePosition.x + 2, piecePosition.y - 2);
                        if (pointIsInBoard(leftPoint) && !hasPieceInPosition(leftPoint)) {
                            capturePoints.add(leftPoint);
                        }
                    } else if (!hasPieceInPosition(leftPoint)) {
                        movePoints.add(leftPoint);
                    }
                }

                Point rightPoint = new Point(piecePosition.x + 1, piecePosition.y + 1);

                if (pointIsInBoard(rightPoint)) {
                    if (hasPieceInPosition(rightPoint) && pieceInPositionIsWhite(rightPoint)) {
                        rightPoint = new Point(piecePosition.x + 2, piecePosition.y + 2);
                        if (pointIsInBoard(rightPoint) && !hasPieceInPosition(rightPoint)) {
                            capturePoints.add(rightPoint);
                        }
                    } else if (!hasPieceInPosition(rightPoint)) {
                        movePoints.add(rightPoint);
                    }
                }
            }

            return capturePoints.isEmpty() ? movePoints : capturePoints;
        }
    }

    private boolean pointIsCaptureMove(Point piecePosition, Point toGoPosition) {
        return Math.abs(piecePosition.x - toGoPosition.x) == 2 && Math.abs(piecePosition.y - toGoPosition.y) == 2;
    }

    private List<Piece> piecesWhoCanCapture(boolean isWhite) {
        Set<Piece> piecesWhoCanCapture = new HashSet<Piece>();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (!(pieces[i][j] instanceof Piece)) {
                    continue;
                }

                Piece piece = (Piece) pieces[i][j];

                if (piece.isWhite != isWhite) {
                    continue;
                }

                Point piecePosition = getComponentPosition(piece);

                List<Point> allowedPositions = getAllowedPositions(piece);

                for (Point point : allowedPositions) {
                    if (pointIsCaptureMove(piecePosition, point)) {
                        piecesWhoCanCapture.add(piece);
                    }
                }
            }
        }

        return new ArrayList<>(piecesWhoCanCapture);
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

    private boolean pointIsInBoard(Point point) {
        if (point.x < 0 || point.x > (BOARD_SIZE - 1) || point.y < 0 || point.y > (BOARD_SIZE - 1)) {
            return false;
        }
        return true;
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

        List<Piece> piecesWhoCanCapture = piecesWhoCanCapture(isWhiteTurn);

        if (!piecesWhoCanCapture.isEmpty() && !piecesWhoCanCapture.contains(piece)) {
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
