package com.example.valkyrie.inventory_app.data;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BooksContract {
    private BooksContract () {}
    public static final String CONTENT_AUTHORITY="com.example.valkyrie.inventory_app";
    public static final Uri BASE_URI_CONTENT=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_BOOKS="books";
    public static final class BooksEntry implements BaseColumns{
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_URI_CONTENT,PATH_BOOKS);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/" + CONTENT_AUTHORITY +"/"+PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY +"/"+PATH_BOOKS;
    public final static String TABLE_NAME ="books";
    public final static String _ID = BaseColumns._ID;
    public final static String COLUMN_PRODUCT_NAME ="name";
    public final static String COLUMN_PRICE ="price";
    public final static String COLUMN_QUANTITY ="quantity";
    public final static String COLUMN_SUPPLIER_NAME ="supplier_name";
    public final static String COLUMN_SUPPLIER_PHONE_NUMBER ="phone_number";
}
}

