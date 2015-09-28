package com.example.jsteel.tictactoe;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final int BUTTON_COLOUR_DEFAULT = Color.WHITE;
    final int BUTTON_COLOUR_DARK    = Color.DKGRAY;
    final int MASK_ALL_BUTTONS      = 0b111111111;
    GameInstance game               = new GameInstance();
    List<Button> ticTacToeButtons;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("game", game);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        game = (GameInstance) savedInstanceState.get("game");
        modifyButtons(game.getO(), "O", null, false); // Remember Os
        modifyButtons(game.getX(), "X", null, false); // Remember Xs
        int win = game.getWinningCondition();
        if (win > 0) {
            victory(game.getTurn() % 2 == 0 ? "O" : "X");
        } else if (game.getTurn() >= game.MAP_SIZE) {
            draw();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnNewGame = (Button) findViewById(R.id.btnNewGame);

        ticTacToeButtons = Arrays.asList(
                (Button) findViewById(R.id.btn0), (Button) findViewById(R.id.btn1), (Button) findViewById(R.id.btn2),
                (Button) findViewById(R.id.btn3), (Button) findViewById(R.id.btn4), (Button) findViewById(R.id.btn5),
                (Button) findViewById(R.id.btn6), (Button) findViewById(R.id.btn7), (Button) findViewById(R.id.btn8)
        );

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.game = new GameInstance();
                modifyButtons(MASK_ALL_BUTTONS, "", BUTTON_COLOUR_DEFAULT, true);
            }
        });

        for (final Button button : ticTacToeButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Button clickedButton = (Button) v;
                    clickedButton.setClickable(false);
                    String activePlayer = MainActivity.this.game.getTurn() % 2 == 0 ? "X" : "O";
                    clickedButton.setText(activePlayer);
                    boolean victory = MainActivity.this.game.processTurn(ticTacToeButtons.indexOf(v));
                    if (victory) {
                        victory(activePlayer);
                    } else if (game.getTurn() >= game.MAP_SIZE) {
                        draw();
                    }
                }
            });
        }
    }

    private void victory(String player) {
        Toast.makeText(this, String.format(getString(R.string.somebodyWon), player), Toast.LENGTH_SHORT).show();
        modifyButtons(~game.getWinningCondition(), null, BUTTON_COLOUR_DARK, false);
    }

    private void draw(){
        Toast.makeText(this, R.string.everyoneLost, Toast.LENGTH_SHORT).show();
        modifyButtons(game.getO(), getString(R.string.oLost), Color.GREEN, false);
        modifyButtons(game.getX(), getString(R.string.xLost), Color.RED, false);
    }

    /**
     * Modify the properties of buttons matching a mask
     * @param mask      The buttons to apply modifications to (7 == top 3 buttons 0b000000111
     * @param text      If specified, this will replace the text of matched buttons
     * @param colour    If specified, this will replace the colour of matched buttons
     * @param clickable If specified, this will set the clickable state of matched buttons
     */
    private void modifyButtons(int mask, String text, Integer colour, Boolean clickable){
        for (int i = 0; i < ticTacToeButtons.size(); i++) {
            if (checkFlag(i, mask)) {
                Button btn = ticTacToeButtons.get(i);
                if(clickable != null) btn.setClickable(clickable);
                if(colour != null)    btn.setBackgroundColor(colour);
                if(text != null)      btn.setText(text);
            }
        }
    }

    /**
     * Convert i to a power of 2 and check it against a flag
     * @param i The int to be convereted to a flag (0 == 1, 1 == 1, 2 == 4...)
     * @param mask Check if flag is contained in this mask
     * @return returns true if flag matches mask
     */
    private boolean checkFlag(int i, int mask) {
        i = (int) Math.pow(2, i);
        return (mask & i) > 0;
    }
}
