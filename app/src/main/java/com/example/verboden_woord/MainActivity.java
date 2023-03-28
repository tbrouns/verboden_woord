package com.example.verboden_woord;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TabooWordsDbHelper dbHelper = new TabooWordsDbHelper(this);

    private TextView wordTextView;
    private TextView wordsTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout stuff
        setContentView(R.layout.activity_main);
        wordTextView = findViewById(R.id.wordTextView);
        wordsTextView = findViewById(R.id.wordsTextView);
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            setNewWords();
        });

        setNewWords();
    }

    private void setNewWords() {
        // Get a random guess word from the database
        String guessWord = getRandomGuessWord();
        // Get the taboo words for the guess word from the database
        List<String> tabooWords = dbHelper.getTabooWordsForGuessWord(guessWord);
        // Update TextViews
        wordTextView.setText(guessWord);
        String tabooWordsConcat = String.join("\n\n", tabooWords);
        wordsTextView.setText(tabooWordsConcat);
    }

    private String getRandomGuessWord() {
        TabooWordsDbHelper dbHelper = new TabooWordsDbHelper(this);
        String guessWord = dbHelper.getRandomGuessWord();
        return guessWord;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}


