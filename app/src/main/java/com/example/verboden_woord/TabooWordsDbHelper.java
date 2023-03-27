public class TabooWordsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "taboo_words.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "taboo_words";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_GUESS_WORD = "guess_word";
    private static final String COLUMN_TABOO_WORDS = "taboo_words";

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_GUESS_WORD + " TEXT, " +
            COLUMN_TABOO_WORDS + " TEXT);";

    private static final String INSERT_WORD_QUERY = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_GUESS_WORD + ", " + COLUMN_TABOO_WORDS + ") VALUES (?, ?)";

    private SQLiteDatabase database;

    public TabooWordsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);

        // Read taboo words from file in assets folder and insert them into the database
        try {
            InputStream inputStream = getContext().getAssets().open("taboo_words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(",");
                if (words.length == 6) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COLUMN_GUESS_WORD, words[0]);
                    contentValues.put(COLUMN_TABOO_WORDS, words[1] + "," + words[2] + "," + words[3] + "," + words[4] + "," + words[5]);
                    db.insert(TABLE_NAME, null, contentValues);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle upgrades to the database schema
    }

    public List<String> getTabooWordsForGuessWord(String guessWord) {
        List<String> tabooWords = new ArrayList<>();

        database = getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_TABOO_WORDS}, COLUMN_GUESS_WORD + "=?", new String[]{guessWord}, null, null, null);
        if (cursor.moveToFirst()) {
            String tabooWordsString = cursor.getString(cursor.getColumnIndex(COLUMN_TABOO_WORDS));
            tabooWords = Arrays.asList(tabooWordsString.split(","));
        }
        cursor.close();

        return tabooWords;
    }
}
