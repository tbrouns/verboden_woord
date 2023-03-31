package com.tbrouns.verboden_woord;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // For storing user data
    private SharedPreferences sharedPreferences;

    // We use a hashmap to keep track of which words have already shown
    private HashSet<String> excludedGuessWords = new HashSet<>();

    // Initialize database of words
    TabooWordsDbHelper dbHelper = new TabooWordsDbHelper(this);

    // View to show the guess word
    private TextView wordTextView;

    // View for the taboo words
    private TextView wordsTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        // Load excluded guess words from SharedPreferences
        Set<String> excludedGuessWordsSet = sharedPreferences.getStringSet("excluded_guess_words", new HashSet<>());
        excludedGuessWords.addAll(excludedGuessWordsSet);

        // Set layout stuff
        setContentView(R.layout.activity_main);
        wordTextView = findViewById(R.id.wordTextView);
        wordsTextView = findViewById(R.id.wordsTextView);

        // Create button
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            setNewWords();
        });

        // Initialize the words
        setNewWords();
    }

    private void setNewWords() {
        // Get a random guess word from the database
        String guessWord = getRandomGuessWord();
        // Get the taboo words for the guess word from the database
        List<String> tabooWords = dbHelper.getTabooWordsForGuessWord(guessWord);
        String tabooWordsConcat = String.join("\n\n", tabooWords);
        // Substitute `_`
        guessWord = guessWord.replace('_', '\'');
        tabooWordsConcat = tabooWordsConcat.replace('_', '\'');
        // Update TextViews
        wordTextView.setText(guessWord);
        wordsTextView.setText(tabooWordsConcat);
    }

    private String getRandomGuessWord() {
        TabooWordsDbHelper dbHelper = new TabooWordsDbHelper(this);

        String guessWord;
        while (true) {
            // Get a new guess word
            guessWord = dbHelper.getRandomGuessWord(excludedGuessWords.toArray(new String[excludedGuessWords.size()]));
            if (guessWord == null) {
                // If no new guess word can be found ...
                // ... reset the excluded guess words set
                excludedGuessWords.clear();
            } else {
                break;
            }
        }

        // Add the new guess word to the excluded set
        excludedGuessWords.add(guessWord);

        // Save excluded guess words to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("excluded_guess_words", excludedGuessWords);
        editor.apply();

        return guessWord;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}