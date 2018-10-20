package com.example.valkyrie.inventory_app;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.valkyrie.inventory_app.data.BooksDbHelper;import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.valkyrie.inventory_app.data.BooksContract.BooksEntry;
import com.example.valkyrie.inventory_app.data.BooksDbHelper;

    public class BooksCatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private BooksDbHelper mDbHelper;
    private static final int BOOKS_LOADER=0;
    BookCursorAdapter mCursorAdapter;
    private BooksDbHelper dbHelper = new BooksDbHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton)  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent booksIntent = new Intent(BooksCatalogActivity.this, BooksEditorActivity.class);
                startActivity(booksIntent);
            }
        });
        ListView bookListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent booksIntent = new Intent(BooksCatalogActivity.this,BooksEditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BooksEntry.CONTENT_URI,id);
                booksIntent.setData(currentBookUri);
                startActivity(booksIntent);
            }
        });
        getLoaderManager().initLoader(BOOKS_LOADER, null, this);
    }
    private void insertBooks() {

    ContentValues values = new ContentValues();
    values.put(BooksEntry.COLUMN_PRODUCT_NAME, "Ritter");
    values.put(BooksEntry.COLUMN_PRICE, 1);
    values.put(BooksEntry.COLUMN_QUANTITY, 1);
    values.put(BooksEntry.COLUMN_SUPPLIER_NAME ,"Bau!");
    values.put(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER , 1);
    Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, values);
    }
    private void deleteAllBooks(){
        int rowsDeleted = getContentResolver().delete(BooksEntry.CONTENT_URI, null, null);
        Log.v("BooksCatalogActivity", rowsDeleted + " rows deleted from pet database");
    }
        @Override
        protected void onStart() {
            super.onStart();
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_books_order:
                deleteAllBooks();
                Toast.makeText(this, R.string.delete_successfully,
                        Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_PRODUCT_NAME,
                BooksEntry.COLUMN_PRICE,
                BooksEntry.COLUMN_QUANTITY };


        return new CursorLoader(this,
                BooksEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    mCursorAdapter.swapCursor(null);
    }

    public void Booksale (long product, int quantities) {

            if (quantities >= 1) {
                quantities--;
                Uri updateUri = ContentUris.withAppendedId(BooksEntry.CONTENT_URI, product);
                ContentValues values = new ContentValues();
                values.put(BooksEntry.COLUMN_QUANTITY, quantities);
                int rowsUpdated = getContentResolver().update(
                        updateUri,
                        values,
                        null,
                        null);
                if (rowsUpdated == 1) {
                    Toast.makeText(this, R.string.sale, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.sale_failed, Toast.LENGTH_SHORT).show();
                }

            } else {

                Toast.makeText(this, R.string.out_of_stock, Toast.LENGTH_LONG).show();
            }
        }
    }
