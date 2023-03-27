package com.example.verboden_woord;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TabooWordsDbHelper dbHelper;

    int beg = 1, end = 5;
    private TextView wordTextView;
    private TextView wordsTextView;
    private String[] currentWords = {"GUESS_WORD", "TABOO_WORD1", "TABOO_WORD2", "TABOO_WORD3", "TABOO_WORD4", "TABOO_WORD5"};

    // Generic method to get subarray of a non-primitive array
    // between specified indices
    public static <T> T[] subArray(T[] array, int beg, int end) {
        return Arrays.copyOfRange(array, beg, end + 1);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the database
        dbHelper = new TabooWordsDbHelper(this);

        // Set layout stuff
        setContentView(R.layout.activity_main);
        wordTextView = findViewById(R.id.wordTextView);
        wordsTextView = findViewById(R.id.wordsTextView);
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            setNewWords()
        });

        setNewWords()
    }

    private void setNewWords() {
        // Get a random guess word from the database
        String guessWord = getRandomGuessWord();
        // Get the taboo words for the guess word from the database
        List<String> tabooWords = dbHelper.getTabooWordsForGuessWord(guessWord);
        // Update TextViews
        wordTextView.setText(guessWord);
        String tabooWordsConcat = concatString(tabooWords);
        wordsTextView.setText(tabooWordsConcat);
    }

    private String concatString(String[] stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stringArray.length; i++) {
            stringBuilder.append(stringArray[i]);
            stringBuilder.append("\n\n");
        }
        return stringBuilder.toString();
    }

    private String getRandomGuessWord() {
        // Get a random guess word from the database
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query("taboo_words", new String[]{"guess_word"}, null, null, null, null, "RANDOM()", "1");
        cursor.moveToFirst();
        String guessWord = cursor.getString(cursor.getColumnIndex("guess_word"));
        cursor.close();
        return guessWord;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}


