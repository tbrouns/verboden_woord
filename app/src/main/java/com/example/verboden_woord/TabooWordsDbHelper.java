package com.example.verboden_woord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TabooWordsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "taboo_words.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;

    public TabooWordsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE taboo_words (guess_word TEXT, taboo_word_1 TEXT, taboo_word_2 TEXT, taboo_word_3 TEXT, taboo_word_4 TEXT, taboo_word_5 TEXT)");

        try {
            // Open the file containing the words
            InputStream inputStream = context.getAssets().open("words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            // Read each line of the file
            while ((line = reader.readLine()) != null) {
                // Split the line into the guess word and taboo words
                String[] words = line.split(",");
                String guessWord = words[0];
                String tabooWord1 = words[1];
                String tabooWord2 = words[2];
                String tabooWord3 = words[3];
                String tabooWord4 = words[4];
                String tabooWord5 = words[5];

                // Insert the words into the database
                ContentValues values = new ContentValues();
                values.put("guess_word", guessWord);
                values.put("taboo_word_1", tabooWord1);
                values.put("taboo_word_2", tabooWord2);
                values.put("taboo_word_3", tabooWord3);
                values.put("taboo_word_4", tabooWord4);
                values.put("taboo_word_5", tabooWord5);
                db.insert("taboo_words", null, values);
            }

            reader.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e("TabooWordsDbHelper", "Error reading words file", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade logic goes here
    }

    public List<String> getTabooWordsForGuessWord(String guessWord) {
        SQLiteDatabase database = getReadableDatabase();
        String[] columns = {"taboo_word_1", "taboo_word_2", "taboo_word_3", "taboo_word_4", "taboo_word_5"};
        String selection = "guess_word = ?";
        String[] selectionArgs = {guessWord};
        Cursor cursor = database.query("taboo_words", columns, selection, selectionArgs, null, null, null);
        List<String> tabooWords = new ArrayList<>();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                tabooWords.add(cursor.getString(i));
            }
        }
        cursor.close();
        return tabooWords;
    }

    public String getRandomGuessWord() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT guess_word FROM taboo_words ORDER BY RANDOM() LIMIT 1", null);
        String guessWord = null;
        if (cursor.moveToFirst()) {
            guessWord = cursor.getString(0);
        }
        cursor.close();
        return guessWord;
    }

}
