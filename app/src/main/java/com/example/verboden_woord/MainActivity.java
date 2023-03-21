package com.example.verboden_woord;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);

        wordTextView = findViewById(R.id.wordTextView);
        wordsTextView = findViewById(R.id.wordsTextView);
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            // Generate new word and set of words
            currentWords = generateRandomWords();

            // Update TextViews
            wordTextView.setText(currentWords[0]);
            setTabooWords(currentWords);
        });

        // Set default word and set of words
        // TODO: extract function
        wordTextView.setText(currentWords[0]);
        setTabooWords(currentWords);
    }

    private void setTabooWords(String[] currentWords) {
        String[] tabooWords = subArray(currentWords, beg, end);
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

    private String[] generateRandomWords() {
        // TODO: Implement random words generation logic
        return new String[]{"NEW GUESS WORD", "NEW WORD1", "NEW WORD2", "NEW WORD3", "NEW WORD4", "NEW WORD5"};
    }
}


