package com.example.valkyrie.inventory_app.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.valkyrie.inventory_app.data.BooksContract.BooksEntry;

public class BooksDbHelper extends SQLiteOpenHelper {
    public static final  String LOG_TAG = BooksDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 1;
    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE =  "CREATE TABLE " + BooksEntry.TABLE_NAME + " ("
                + BooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BooksEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_PRICE + " INTEGER, "
                + BooksEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BooksEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }
}
