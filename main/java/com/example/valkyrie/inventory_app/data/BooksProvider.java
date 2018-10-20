package com.example.valkyrie.inventory_app.data;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.valkyrie.inventory_app.data.BooksContract.BooksEntry;

public class BooksProvider extends ContentProvider{
    public static final String LOG_TAG = BooksProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 200;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS,BOOKS);
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS +"/#", BOOK_ID);
    }
    private BooksDbHelper mDbHelper;
    @Override
    public boolean onCreate() {
        mDbHelper = new BooksDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database= mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BooksEntry._ID +"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BooksEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
                default:
                   throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return BooksEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BooksEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return insertBooks (uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertBooks(Uri uri, ContentValues values){
      String title = values.getAsString(BooksEntry.COLUMN_PRODUCT_NAME);
      if (title==null) {
          throw new IllegalArgumentException("Books require a name");
      }
        String supplier_name = values.getAsString(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (title==null) {
            throw new IllegalArgumentException("Books require a supplier name");
        }
        Integer price = values.getAsInteger(BooksEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Books require a supplier name");
        }
        Integer quantity = values.getAsInteger(BooksEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Books require quantity");
        }
        Integer supplier_phone = values.getAsInteger(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplier_phone != null && supplier_phone < 0) {
            throw new IllegalArgumentException("Books require phone number of supplier");
        }
        SQLiteDatabase database= mDbHelper.getWritableDatabase();
        long id = database.insert(BooksEntry.TABLE_NAME,null,values);
        if (id==-1){
        Log.e(LOG_TAG, "Failed to insert row for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
            return ContentUris.withAppendedId(uri,id);
        }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS: rowsDeleted = database.delete(BooksEntry.TABLE_NAME,selection,selectionArgs);
            break;
            case BOOK_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return updateBook (uri, contentValues,selection,selectionArgs);
            case BOOK_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateBook(Uri uri, ContentValues values, String selection, String [] selectionArgs){
        if (values.containsKey(BooksEntry.COLUMN_PRODUCT_NAME)){
            String title = values.getAsString(BooksEntry.COLUMN_PRODUCT_NAME);
                if (title == null){
                    throw new IllegalArgumentException("Book requires a title");
                }
            }
        if (values.containsKey(BooksEntry.COLUMN_SUPPLIER_NAME)){
            String supplier_name = values.getAsString(BooksEntry.COLUMN_SUPPLIER_NAME);
            if (supplier_name == null){
                throw new IllegalArgumentException("Book requires a name of supplier");
            }
            }
        if (values.containsKey(BooksEntry.COLUMN_QUANTITY)){
            Integer quantity = values.getAsInteger(BooksEntry.COLUMN_QUANTITY);
            if (quantity == null){
            throw new IllegalArgumentException("Book requires quantity");
            }
            }
        if (values.containsKey(BooksEntry.COLUMN_PRICE)){
            Integer price = values.getAsInteger(BooksEntry.COLUMN_PRICE);
            if (price == null){
                throw new IllegalArgumentException("Book requires price");
            }
        }
        if (values.containsKey(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER)){
            Integer number = values.getAsInteger(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (number == null){
                throw new IllegalArgumentException("Book requires phone number of supplier");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BooksEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}









