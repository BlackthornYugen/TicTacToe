package com.example.jsteel.tictactoe;

import java.io.Serializable;

/**
 * Created by black on 2015-09-25.
 */
public class GameInstance implements Serializable{
    public static final int MAP_SIZE = 9;
    public static final int PLAYERS = 2;

    public GameInstance (int turn, int x, int o, int winningCondition) {
        setTurn(turn);
        setX(x);
        setO(o);
        setWinningCondition(winningCondition);
    }

    public GameInstance () {

    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    private int turn = 0;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    private int x = 0b000000000;

    public int getO() {
        return o;
    }

    public void setO(int o) {
        this.o = o;
    }

    private int o = 0b000000000;
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

    public void setWinningCondition(int winningCondition) {
        this.winningCondition = winningCondition;
    }

    private int winningCondition;

    /**
     * Process a turn
     * @param position The position to make the move at
     * @return
     */
    public boolean processTurn(int position) {
        position = (int) Math.pow(2, position); // binary position
        if (turn++ % PLAYERS == 0) {
            x |= position;
            return isPlayerVictorious(x);
        } else {
            o |= position;
            return isPlayerVictorious(o);
        }
    }

    private boolean isPlayerVictorious(int player) {
        for (int c : victoryConditions) {
            if ((c & player) == c) {
                winningCondition = c;
                return true;
            }
        }
        return false;
    }

    public int getWinningCondition() {
        return winningCondition;
    }
}
