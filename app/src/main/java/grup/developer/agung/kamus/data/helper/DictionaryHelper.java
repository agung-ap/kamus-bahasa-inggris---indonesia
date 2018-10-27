package grup.developer.agung.kamus.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import grup.developer.agung.kamus.data.model.DictionaryModel;

public class DictionaryHelper {
    private static String ENGLISH = DatabaseHelper.TABLE_ENGLISH;
    private static String INDONESIA = DatabaseHelper.TABLE_INDONESIA;

    private Context context;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public DictionaryHelper(Context context) {
        this.context = context;
    }

    public DictionaryHelper open(){
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();

        return this;
    }

    public void close(){
        databaseHelper.close();
    }

    //search data by query
    public Cursor searchByName(String query, boolean isEnglish){
        String databaseTable = isEnglish ? ENGLISH : INDONESIA;

        return database.rawQuery("SELECT * FROM " + databaseTable +
                " WHERE " + DatabaseHelper.FIELD_WORD + " LIKE '%" + query.trim() + "%'", null);
    }

    public Cursor queryAllData(boolean isEnglish) {
        String databaseTable = isEnglish ? ENGLISH : INDONESIA;

        return database.rawQuery("SELECT * FROM " + databaseTable + " ORDER BY " + DatabaseHelper.FIELD_ID + " ASC", null);
    }

    //get data by name
    public ArrayList<DictionaryModel> getDataByName(String search , boolean isEnglish){
        DictionaryModel dictionaryModel;

        ArrayList<DictionaryModel> arrayList = new ArrayList<>();
        Cursor cursor = searchByName(search, isEnglish);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                dictionaryModel = new DictionaryModel();
                dictionaryModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.FIELD_ID)));
                dictionaryModel.setWord(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FIELD_WORD)));
                dictionaryModel.setTranslate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FIELD_TRANSLATE)));
                arrayList.add(dictionaryModel);

                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    //get data
    public String getData(String search, boolean isEnglish) {
        String result = "";
        Cursor cursor = searchByName(search, isEnglish);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            result = cursor.getString(2);
            for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                result = cursor.getString(2);
            }
        }
        cursor.close();
        return result;
    }

    public ArrayList<DictionaryModel> getAllData(boolean isEnglish) {
        DictionaryModel dictionaryModel;

        ArrayList<DictionaryModel> arrayList = new ArrayList<>();
        Cursor cursor = queryAllData(isEnglish);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                dictionaryModel = new DictionaryModel();
                dictionaryModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.FIELD_ID)));
                dictionaryModel.setWord(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FIELD_WORD)));
                dictionaryModel.setTranslate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FIELD_TRANSLATE)));

                arrayList.add(dictionaryModel);

                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }
    //insert data
    public long insert(DictionaryModel dictionaryModel, boolean isEnglish) {
        String databaseTable = isEnglish ? ENGLISH : INDONESIA;

        ContentValues initialValues = new ContentValues();
        initialValues.put(DatabaseHelper.FIELD_WORD, dictionaryModel.getWord());
        initialValues.put(DatabaseHelper.FIELD_TRANSLATE, dictionaryModel.getTranslate());

        return database.insert(databaseTable, null, initialValues);
    }

    public void insertTransaction(ArrayList<DictionaryModel> dictionaryModels, boolean isEnglish) {
        String databaseTable = isEnglish ? ENGLISH : INDONESIA;

        String sql = "INSERT INTO " + databaseTable + " (" +
                DatabaseHelper.FIELD_WORD + ", " +
                DatabaseHelper.FIELD_TRANSLATE + ") VALUES (?, ?)";

        database.beginTransaction();

        SQLiteStatement stmt = database.compileStatement(sql);
        
        for (int i = 0; i < dictionaryModels.size(); i++) {
            stmt.bindString(1, dictionaryModels.get(i).getWord());
            stmt.bindString(2, dictionaryModels.get(i).getTranslate());
            stmt.execute();
            stmt.clearBindings();
        }

        database.setTransactionSuccessful();
        database.endTransaction();
    }
    //update data from database
    public void update(DictionaryModel dictionaryModel, boolean isEnglish) {
        String databaseTable = isEnglish ? ENGLISH : INDONESIA;

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.FIELD_WORD, dictionaryModel.getWord());
        values.put(DatabaseHelper.FIELD_TRANSLATE, dictionaryModel.getTranslate());

        database.update(databaseTable, values, DatabaseHelper.FIELD_ID + "= '" + dictionaryModel.getId() + "'", null);
    }
    //delete data from database
    public void delete(int id, boolean isEnglish) {
        String DATABASE_TABLE = isEnglish ? ENGLISH : INDONESIA;

        database.delete(DATABASE_TABLE, DatabaseHelper.FIELD_ID + " = '" + id + "'", null);
    }

}
