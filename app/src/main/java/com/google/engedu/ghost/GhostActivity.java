/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    TextView ghostText;
    TextView gameStatus;
    SimpleDictionary simpleDictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        try{AssetManager assetManager = getAssets();
        simpleDictionary = new SimpleDictionary(assetManager.open("words.txt"));}
        catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        ghostText = findViewById(R.id.ghostText);
        gameStatus = findViewById(R.id.gameStatus);
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        Button button = findViewById(R.id.challenge);
        button.setEnabled(true);
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    public void userChallenge(View view){
        Button button = findViewById(R.id.challenge);
        button.setEnabled(true);
        String word = (String)ghostText.getText();
        if (word.length()>=4 && simpleDictionary.isWord(word)){
            gameStatus.setText("You win!");
        }
        else {
            if (simpleDictionary.getAnyWordStartingWith(word)!=null){
                gameStatus.setText("Computer wins!");
                ghostText.setText(simpleDictionary.getAnyWordStartingWith(word));
            }
            else
                gameStatus.setText("You win!");
        }

    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        label.setText("computer turn");
        String word = (String) ghostText.getText();

        if (simpleDictionary.isWord((String) ghostText.getText())){
            gameStatus.setText("Computer wins!");
            Button button = findViewById(R.id.challenge);
            button.setEnabled(false);
            return;
        }
        else{
            if (simpleDictionary.getAnyWordStartingWith(word)==null){
                gameStatus.setText("Computer wins!");
                Button button = findViewById(R.id.challenge);
                button.setEnabled(false);
                return;
            }
            else{
                String longerWord = simpleDictionary.getAnyWordStartingWith(word);
                ghostText.setText(longerWord.substring(0, ((String) ghostText.getText()).length()+1));
                userTurn=true;
                label.setText(USER_TURN);
            }
        }
        // Do computer turn stuff then make it the user's turn again
        //userTurn = true;
        //label.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char code = (char) event.getUnicodeChar();
        if (Character.isLetter(code)){
            String tv = (String) ghostText.getText();
            tv= tv+ code;
            if (simpleDictionary.isWord(tv)){
                gameStatus.setText("You lose!");
            }
            ghostText.setText(tv);
            computerTurn();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

//    public void onSaveInstanceState(Bundle bundle) {
//       // bundle.putString(TEXT_KEY, text);
//        Log.i("onSave", "onSaveInstanceState invoked");
//        super.onSaveInstanceState(bundle);
//    }


}
