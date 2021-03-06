package antichess;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.imageio.*;
import java.io.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.geom.*;

public class HumanBoard extends Frame {
    private double squareSize;
    private BufferedImage wTile;
    private BufferedImage bTile;
    //Testing mouse clicking stuff. MCS
    public Move mouseClick;
    //TEST THING
    protected int firstX;
    protected int firstY;
    protected boolean secondClick;
    protected boolean returnMove;
    protected boolean refreshBoard;
    //
    private Board currentBoard;

    private BufferedImage[][] pieceImages;

    public HumanBoard(Board currentBoard, int frameSize) {
        this.currentBoard = currentBoard;

        mouseClick = null;
        secondClick = false;
        returnMove = false;
        refreshBoard = false;
        firstX = -1;
        firstY = -1;
        MouseClickListener listener = new MouseClickListener();
        this.addMouseListener(listener);
        //MA - set square size
        squareSize = frameSize / 10;

        try {
            wTile = ImageIO.read(getClass().getClassLoader().getResource("images/tile_white.png"));
        } catch (IOException ioe) {
            System.exit(-1);
        }
        try {
            bTile = ImageIO.read(getClass().getClassLoader().getResource("images/tile_black.png"));
        } catch (IOException ioe) {
            System.exit(-1);
        }

        pieceImages = new BufferedImage[2][6];
        try {
            for (int colour : Definitions.COLOURS) {
                for (int piece : Definitions.PIECE_NUMBERS) {
                    pieceImages[colour][piece] = ImageIO.read(getClass().getClassLoader().getResource("images/" + Definitions.COLOUR_FILE_NAMES[colour] + "_" + Definitions.PIECE_FILE_NAMES[piece] + ".png"));
                }
            }
        } catch (IOException ioe) {
            System.exit(-1);
        }
    }

    public Move getMove() throws InterruptedException {
        while (true) {
            if (refreshBoard == true) {
                this.repaint();
                refreshBoard = false;
            }
            if (returnMove == true) {
                break;
            }
            Thread.sleep(10);
        }
        returnMove = false;
        return mouseClick;
    }

    //Testing mouse clicking stuff. MCS
    private class MouseClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            int x = event.getX() / 60 - 1;
            int y = 9 - event.getY() / 60 - 1;
            //System.out.println(x);
            //System.out.println(y);

            if (secondClick == true) {
                //do stuff if it is the second click
                //System.out.println("Working on the second click");
                secondClick = false;
                if (firstX != x || firstY != y) {
                    mouseClick = new Move(firstX, firstY, x, y);
                    firstX = -1;
                    firstY = -1;
                    refreshBoard = true;
                    returnMove = true;
                } else {
                    firstX = -1;
                    firstY = -1;
                    refreshBoard = true;
                }
            } else {
                //do stuff if it is the first click
                firstX = x;
                firstY = y;
                secondClick = true;
                refreshBoard = true;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D ga = (Graphics2D) g;
        for (int i = 7; i >= 0; i--) {
            for (int j = 7; j >= 0; j--) {
                double leftEdge = squareSize * (i + 1);
                double topEdge = squareSize * (8 - j);

                if ((i + j) % 2 == 1) {
                    ga.drawImage(wTile, null, (int) leftEdge, (int) topEdge);

                } else {
                    ga.drawImage(bTile, null, (int) leftEdge, (int) topEdge);
                }

                if (currentBoard.squares[i][j] != null) {
                    Piece piece = currentBoard.squares[i][j];
                    ga.drawImage(pieceImages[piece.colour][piece.pieceType], null, (int) leftEdge + 10, (int) topEdge + 10);
                }
            }
        }
        if (firstX != -1) {
            Rectangle2D.Double highlightedSquare = new Rectangle2D.Double((firstX + 1) * 60, (8 - firstY) * 60, 60, 60);
            ga.setColor(Color.RED);
            ga.draw(highlightedSquare);
        }
    }
}
