/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package antichess;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Game {
    private boolean gameRunning = true;
    private Player whitePlayer = null;
    private Player blackPlayer = null;
    private Object whitePlayerOptions;
    private Object blackPlayerOptions;
    private Board currentBoard;
    private HumanBoard currentHumanBoard;

    public Game(int whitePlayerType, Object whitePlayerOptions, int blackPlayerType, Object blackPlayerOptions) {
        System.out.print("Would you like to play chess 960? (y/n): ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean gameIs960;
        String input = "";
        try {
            input = br.readLine();
            while(!input.toLowerCase().trim().equals("n") && !input.toLowerCase().trim().equals("y")) {
                input = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(input.toLowerCase().trim().equals("n")) {
            gameIs960 = false;
        }
        else {
            gameIs960 = true;
        }


        currentBoard = new Board(gameIs960);
        currentHumanBoard = new HumanBoard(currentBoard, Definitions.FRAME_SIZE);

        switch (whitePlayerType) {
            case Definitions.HUMAN_PLAYER:
                whitePlayer = new HumanPlayer(currentBoard, currentHumanBoard, Definitions.WHITE);
                break;
            case Definitions.AI_PLAYER:
                whitePlayer = new AIPlayer(Definitions.WHITE, whitePlayerOptions);
                break;
            case Definitions.NETWORK_PLAYER:
                //
                break;
        }

        switch (blackPlayerType) {
            case Definitions.HUMAN_PLAYER:
                blackPlayer = new HumanPlayer(currentBoard, currentHumanBoard, Definitions.BLACK);
                break;
            case Definitions.AI_PLAYER:
                blackPlayer = new AIPlayer(Definitions.BLACK, blackPlayerOptions);
                break;
            case Definitions.NETWORK_PLAYER:
                //
                break;
        }
    }

    public int runGame() {
        Player currentPlayer = whitePlayer;
        Player otherPlayer = blackPlayer;
        int moves = 0;
        int end = Definitions.NO_WIN;

        Move nextMove = null;

        currentHumanBoard.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                currentHumanBoard.setVisible(false);
            }
        });

        currentHumanBoard.setSize(Definitions.FRAME_SIZE, Definitions.FRAME_SIZE);
        currentHumanBoard.setVisible(true);

        while (gameRunning) {
            currentBoard.isFinished(currentPlayer.getPlayerColour());

            currentBoard.generateMoves(currentPlayer.getPlayerColour());
            if (currentBoard.canMove()) {
                moves++;
                nextMove = currentPlayer.getMove();

                currentBoard.makeMove(nextMove);
                otherPlayer.sendMove(nextMove);
                currentHumanBoard.repaint();
                currentHumanBoard.setVisible(true);
            }

            end = currentBoard.isFinished(currentPlayer.getPlayerColour());

            switch (end) {
                case Definitions.LOCKED_STALEMATE:
                    System.out.println("Neither of you can move so its stalemate!");
                    gameRunning = false;
                    break;
                case Definitions.DERIVED_STALEMATE:
                    System.out.println("Neither of you can win so its stalemate!");
                    gameRunning = false;
                    break;
                case Definitions.WHITE_WINS:
                    System.out.println("White wins!");
                    gameRunning = false;
                    break;
                case Definitions.BLACK_WINS:
                    System.out.println("Black wins!");
                    gameRunning = false;
                    break;
                default:
            }

            if (currentPlayer == whitePlayer) {
                currentPlayer = blackPlayer;
                otherPlayer = whitePlayer;
            } else {
                currentPlayer = whitePlayer;
                otherPlayer = blackPlayer;
            }
        }
        System.out.println("The game took " + moves + " moves.");
        return end;
    }
}
