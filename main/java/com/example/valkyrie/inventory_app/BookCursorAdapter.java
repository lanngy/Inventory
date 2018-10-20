package com.example.valkyrie.inventory_app;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.valkyrie.inventory_app.data.BooksContract;
import com.example.valkyrie.inventory_app.data.BooksContract.BooksEntry;

public class BookCursorAdapter extends CursorAdapter {
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView titleTextView = (TextView) view.findViewById(R.id.booktitle);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_book);
        TextView inOrderTextView = (TextView) view.findViewById(R.id.quantity_in_stock);

        String BookTitle = cursor.getString(cursor.getColumnIndexOrThrow(BooksEntry.COLUMN_PRODUCT_NAME));
        String BookinOrder = cursor.getString(cursor.getColumnIndexOrThrow(BooksEntry.COLUMN_QUANTITY));
        String BookPrice = cursor.getString(cursor.getColumnIndexOrThrow(BooksEntry.COLUMN_PRICE));

        final long id = cursor.getInt(cursor.getColumnIndexOrThrow(BooksEntry._ID));
        final int quantityTextView = Integer.parseInt(BookinOrder);

        titleTextView.setText(BookTitle);
        inOrderTextView.setText(BookinOrder);
        priceTextView.setText(BookPrice);

        view.findViewById(R.id.sale_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BooksCatalogActivity mainActivity = (BooksCatalogActivity) context;
                mainActivity.Booksale(id, quantityTextView);
            }
        });

        }
    }

