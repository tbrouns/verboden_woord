package com.example.verboden_woord;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TextView wordTextView;
    private TextView wordsTextView;

    private String[] currentWords = {"Evie", "Schatje", "Leuk", "Knap", "Slim", "Lieffie"};
    int beg = 1, end = 5;

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
            String[] tabooWords = subArray(currentWords, beg, end);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < tabooWords.length; i++) {
                stringBuilder.append(tabooWords[i]);
                stringBuilder.append("\n");
            }
            String tabooWordsConcat = stringBuilder.toString();
            wordsTextView.setText(tabooWordsConcat);
        });
        // Set default word and set of words
        // TODO: extract function
        wordTextView.setText(currentWords[0]);
        String[] tabooWords = subArray(currentWords, beg, end);
        wordsTextView.setText(Arrays.toString(tabooWords));
    }

    // Generic method to get subarray of a non-primitive array
    // between specified indices
    public static<T> T[] subArray(T[] array, int beg, int end) {
        return Arrays.copyOfRange(array, beg, end + 1);
    }

    private String[] generateRandomWords() {
        // TODO: Implement random words generation logic
        return new String[]{"NEW WORD1", "NEW WORD2", "NEW WORD3", "NEW WORD4", "NEW WORD5", "NEW WORD6"};
    }
}


