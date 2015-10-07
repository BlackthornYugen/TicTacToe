package com.example.jsteel.tictactoe;
/**
 *  MainActivity.java
 *  The main app activity
 *  Revision History
 *  John M. Steel, 2015-09-26: Created
 *  John M. Steel, 2015-10-06: Added an ENHANCEMENT on line 41 that will allow you to share a game
 *      that has been completed or is in progress with all your friends =)
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final int MASK_ALL_BUTTONS      = 0b111111111;
    GameInstance game               = new GameInstance();
    List<Button> ticTacToeButtons;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                Toast.makeText(MainActivity.this, R.string.aboutText, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, game.toString());
                startActivity(Intent.createChooser(intent, "Share via"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putSerializable("game", game);
        } catch (Exception ex) {
            String message = String.format("Failed to save game state:\n%s", ex.getMessage());
            Log.e("exception", message, ex);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            game = (GameInstance) savedInstanceState.get("game");
            modifyButtons(game.getO(), "O", null, false); // Remember Os
            modifyButtons(game.getX(), "X", null, false); // Remember Xs
            int win = game.getWinningCondition();
            if (win > 0) {
                victory(game.getTurn() % 2 == 0 ? "O" : "X");
            } else if (game.getTurn() >= game.MAP_SIZE) {
                draw();
            }
        } catch (Exception ex) {
            String message = String.format("Failed to restore game state:\n%s", ex.getMessage());
            Log.e("exception", message, ex);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            // Create game instance
            Button btnNewGame = (Button) findViewById(R.id.btnNewGame);

            ticTacToeButtons = Arrays.asList(
                    (Button) findViewById(R.id.btn0), (Button) findViewById(R.id.btn1), (Button) findViewById(R.id.btn2),
                    (Button) findViewById(R.id.btn3), (Button) findViewById(R.id.btn4), (Button) findViewById(R.id.btn5),
                    (Button) findViewById(R.id.btn6), (Button) findViewById(R.id.btn7), (Button) findViewById(R.id.btn8)
            );

            btnNewGame.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MainActivity.this.game = new GameInstance();
                    modifyButtons(MASK_ALL_BUTTONS, "", true, true);
                }
            });
        } catch (Exception ex) {
            String message = String.format("Failed to create Main Activity:\n%s", ex.getMessage());
            Log.e("exception", message, ex);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }

        try {
            for (final Button button : ticTacToeButtons) {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
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
                        } catch (Exception ex) {
                            String message = String.format("Failed to perform move:\n%s", ex.getMessage());
                            Log.e("exception", message, ex);
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            String message = String.format("Failed to set click listener:\n%s", ex.getMessage());
            Log.e("exception", message, ex);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void victory(String player) {
        Toast.makeText(this, String.format(getString(R.string.somebodyWon), player), Toast.LENGTH_SHORT).show();
        modifyButtons(~game.getWinningCondition(), null, false, false);
    }

    private void draw(){
        Toast.makeText(this, R.string.everyoneLost, Toast.LENGTH_SHORT).show();
        modifyButtons(MASK_ALL_BUTTONS, null, false, false);
    }

    /**
     * Modify the properties of buttons matching a mask
     * @param mask      The buttons to apply modifications to (7 == top 3 buttons 0b000000111
     * @param text      If specified, this will replace the text of matched buttons
     * @param enabled   If specified, this will set the enabled state of matched buttons
     * @param clickable If specified, this will set the clickable state of matched buttons
     */
    private void modifyButtons(int mask, String text, Boolean enabled, Boolean clickable){
        try {
            for (int i = 0; i < ticTacToeButtons.size(); i++) {
                if (GameInstance.checkFlag(i, mask)) {
                    Button btn = ticTacToeButtons.get(i);
                    if(clickable != null) btn.setClickable(clickable);
                    if(enabled != null)   btn.setEnabled(enabled);
                    if(text != null)      btn.setText(text);
                }
            }
        } catch (Exception ex) {
            String message = String.format("Failed to modify buttons:\n%s", ex.getMessage());
            Log.e("exception", message, ex);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
