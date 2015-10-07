package com.example.jsteel.tictactoe;
/**
 *  GameInstance.java
 *  An object that handles the actual game state
 *  Revision History
 *  John M. Steel, 2015-09-26: Created
 */
import android.util.Log;
import java.io.Serializable;

public class GameInstance implements Serializable{
    public static final int MAP_SIZE = 9; // 9 squares in a map
    public static final int PLAYERS  = 2; // Two players in a game
    public static int[] victoryConditions = new int[]{
            0b111000000, // Horizontal top
            0b000111000, // Horizontal middle
            0b000000111, // Horizontal bottom
            0b100100100, // Vertical left
            0b010010010, // Vertical middle
            0b001001001, // Vertical right
            0b100010001, // Back slash
            0b001010100, // Forward slash
    };

    private int turn = 0;           // Turns starts at 0
    private int x    = 0b000000000; // Initial moves for x
    private int o    = 0b000000000; // Initial moves for o
    private int winningCondition;

    public GameInstance () {
        // Empty Constructor
    }

    public GameInstance (int turn, int x, int o, int winningCondition) {
        setTurn(turn);
        setX(x);
        setO(o);
        setWinningCondition(winningCondition);
    }

    public int  getTurn() {
        return turn;
    }
    public void setTurn(int turn) {
        this.turn = turn;
    }
    public int  getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int  getO() {
        return o;
    }
    public void setO(int o) {
        this.o = o;
    }
    public int  getWinningCondition() {
        return winningCondition;
    }
    public void setWinningCondition(int winningCondition) {
        this.winningCondition = winningCondition;
    }

    /**
     * Process a turn
     * @param position The position to make the move at
     * @return true if a player has won
     */
    public boolean processTurn(int position) {
        try {
            position = (int) Math.pow(2, position); // binary position
            if (turn++ % PLAYERS == 0) {
                x |= position;
                return isPlayerVictorious(x);
            } else {
                o |= position;
                return isPlayerVictorious(o);
            }
        } catch (Exception ex) {
            String message = String.format("Failed to process turn:\n%s", ex.getMessage());
            Log.e("exception", message, ex);
        }
        return false;
    }

    private boolean isPlayerVictorious(int player) {
        try {
            for (int c : victoryConditions) {
                if ((c & player) == c) {
                    winningCondition = c;
                    return true;
                }
            }
        } catch (Exception ex) {
            String message = String.format("Failed to check victory conditions:\n%s", ex.getMessage());
            Log.e("exception", message, ex);
        }
        return false;
    }

    @Override
    public String toString() {
        try {
            String boardShape = "" +
                    " 0  |  1  |  2\n" +
                    "--- | --- | ---\n" +
                    " 3  |  4  |  5\n" +
                    "--- | --- | ---\n" +
                    " 6  |  7  |  8\n";
            for(int i = 0; i < MAP_SIZE; i++) {
                String replacement = " ";
                if (checkFlag(i, x)) {
                    replacement = "x";
                } else if (checkFlag(i, o)) {
                    replacement = "o";
                }
                boardShape = boardShape.replace(Integer.toString(i), replacement);
            }
            return boardShape;
        } catch (Exception ex) {
            String message = String.format("Failed to generate string from gamestate:\n%s", ex.getMessage());
            Log.e("exception", message, ex);
            return super.toString(); // Return the default toString when an exception happens
        }
    }

    /**
     * Convert i to a power of 2 and check it against a flag
     * @param i The int to be converted to a flag (0 == 1, 1 == 1, 2 == 4...)
     * @param mask Check if flag is contained in this mask
     * @return returns true if flag matches mask
     */
    public static boolean checkFlag(int i, int mask) {
        try {
            i = (int) Math.pow(2, i);
            return (mask & i) > 0;
        } catch (Exception ex) {
            String message = String.format("Unable to check flag [%d] in [%s]:\n%s", i, mask, ex.getMessage());
            Log.e("exception", message, ex);
        }
        return false;
    }
}
