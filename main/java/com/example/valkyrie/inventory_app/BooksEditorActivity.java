package com.example.valkyrie.inventory_app;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.valkyrie.inventory_app.data.BooksContract;
import com.example.valkyrie.inventory_app.data.BooksContract.BooksEntry;
import com.example.valkyrie.inventory_app.data.BooksDbHelper;

public class BooksEditorActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{
    private static final int INVENTORY_BOOK_LOADER= 1;
    private static final boolean SAVE = true;
    private static final boolean SAVE_ERROR = false;
    private Uri mCurrentBookUri;
    private EditText mTitleEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mPhoneEditText;
    private EditText mSupplierName;
    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        mCurrentBookUri = getIntent().getData();
        if (mCurrentBookUri == null){
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));
            getLoaderManager().initLoader(INVENTORY_BOOK_LOADER, null, this);
            }
        mPhoneEditText =(EditText) findViewById(R.id.edit_phone_number);
        mQuantityEditText =(EditText) findViewById(R.id.edit_quantity);
        mPriceEditText =(EditText) findViewById(R.id.edit_book_price);
        mSupplierName =(EditText) findViewById(R.id.edit_supplier_name);
        mTitleEditText =(EditText) findViewById(R.id.edit_book_title);
        final EditText quantityTextView = findViewById(R.id.edit_quantity);

        findViewById(R.id.decrease_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String quantityString = quantityTextView.getText().toString();
                if (TextUtils.isEmpty(quantityString) || quantityString == null) {
                    quantityString = String.valueOf(0);
                }

                int quantity = Integer.parseInt(quantityString);

                if (quantity == 0) {
                    Toast.makeText(getApplicationContext(), R.string.error_quantity, Toast.LENGTH_SHORT).show();
                } else {
                    quantity--;
                    quantityTextView.setText(String.valueOf(quantity));
                }
            }
        });

        findViewById(R.id.increase_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = quantityTextView.getText().toString();
                if (TextUtils.isEmpty(quantityString) || quantityString == null) {
                    quantityString = String.valueOf(0);
                }

                int quantity = Integer.parseInt(quantityString);

                quantity++;
                quantityTextView.setText(String.valueOf(quantity));

            }
        });

        findViewById(R.id.button_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText phoneEditText = findViewById(R.id.edit_phone_number);
                String phone = phoneEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_phone_app, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mTitleEditText.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                if (saveBook()) {
                    finish();
                } else {
                    Toast.makeText(this, R.string.data_cant_save, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.delete:
                    showDeleteConfirmationDialog();
                    return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(BooksEditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(BooksEditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
        }
    private boolean saveBook() {
        String supplierPhone = "";
        String itemName = "";
        String supplierName = "";
        int itemQuantity;
        float itemPrice;

        try {
            supplierName = mSupplierName.getText().toString().trim();
            supplierPhone = mPhoneEditText.getText().toString().trim();
            itemName = mTitleEditText.getText().toString().trim();
            itemQuantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
            itemPrice = Float.parseFloat(mPriceEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            return SAVE_ERROR;
        }

        try {

            if (TextUtils.isEmpty(itemName)) {
                throw new IllegalArgumentException(this.getResources().getString(R.string.error_product_name));
            }

            if (itemPrice <= 0) {
                throw new IllegalArgumentException(this.getResources().getString(R.string.error_price));
            }

            if (itemQuantity < 0) {
                throw new IllegalArgumentException(this.getResources().getString(R.string.error_quantity));
            }

            if (TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException(this.getResources().getString(R.string.error_supplier_name));
            }

            if (TextUtils.isEmpty(supplierPhone)) {
                throw new IllegalArgumentException(this.getResources().getString(R.string.error_phone_number));
            }
        } catch (IllegalArgumentException e) {
            return SAVE_ERROR;
        }

        BooksDbHelper dbHelper = new BooksDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BooksEntry.COLUMN_PRODUCT_NAME, itemName);
        values.put(BooksEntry.COLUMN_PRICE, itemPrice);
        values.put(BooksEntry.COLUMN_QUANTITY, itemQuantity);
        values.put(BooksEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

        Long affectedRowId;
        int rowsUpdated;
        String toastText;

        if (mCurrentBookUri == null) {
            mCurrentBookUri = getContentResolver().insert(BooksEntry.CONTENT_URI, values);
            affectedRowId = ContentUris.parseId(mCurrentBookUri);

            if (affectedRowId == -1) {
                toastText = getResources().getString(R.string.error_save);
            } else {
                toastText = getResources().getString(R.string.item_saved);
            }
        } else {
            rowsUpdated = getContentResolver().update(
                   mCurrentBookUri,
                    values,
                    null,
                    null
            );
            if (rowsUpdated != 1) {
                toastText = getResources().getString(R.string.error_save);
            } else {
                toastText = getResources().getString(R.string.item_saved);
            }
        }
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();

        db.close();

        return SAVE;
    }
    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_QUANTITY,
                BooksEntry.COLUMN_PRODUCT_NAME,
                BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                BooksEntry.COLUMN_PRICE,
                BooksEntry.COLUMN_SUPPLIER_NAME};

        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_QUANTITY);
            int namesuppColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String title = cursor.getString(titleColumnIndex);
            String namesupp = cursor.getString(namesuppColumnIndex);
            Integer price = cursor.getInt(priceColumnIndex);
            Integer quantity = cursor.getInt(quantityColumnIndex);
            Integer phone = cursor.getInt(phoneColumnIndex);

            mPriceEditText.setText(Integer.toString(price));
            mSupplierName.setText(namesupp);
            mPhoneEditText.setText(Integer.toString(phone));
            mTitleEditText.setText(title);
            mQuantityEditText.setText(Integer.toString(quantity));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleEditText.setText("");
        mQuantityEditText.setText("");
        mPhoneEditText.setText("");
        mPriceEditText.setText("");
        mSupplierName.setText("");
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deletePet() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}