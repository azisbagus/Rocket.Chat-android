package chat.rocket.app.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import chat.rocket.app.db.util.AsyncQueryHandler;
import chat.rocket.app.db.util.ContentValuables;


public class DBManager {
    private static DBManager sInstance;

    private Context mCtx;

    public void init(Context context) {
        mCtx = context;
    }

    public static DBManager getInstance() {
        if (null == sInstance) {
            sInstance = new DBManager();
        }
        return sInstance;
    }

    private DBManager() {

    }

    public boolean isEmpty(String table) {
        return isEmpty(table, null, null);
    }

    public boolean isEmpty(String table, String column, String value) {
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(DBContentProvider.ONE_ROW_LIMIT).appendPath(table).build();
        StringBuilder selection = new StringBuilder();
        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(column)) {
            selection.append(column).append("=?");
            if (!TextUtils.isEmpty(value)) {
                selectionArgs = new String[]{value};
            }
        }
        Cursor cursor = mCtx.getContentResolver().query(uri, null, selection.toString(), selectionArgs, null);
        if (cursor != null) {
            boolean isEmpty = cursor.getCount() < 1;
            cursor.close();
            return isEmpty;
        }
        return false;
    }

    @SuppressLint("NewApi")
    public static String getNormalizedString(String s) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            return Normalizer.normalize(s, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        } else {
            return s;
        }
    }

    public String ANDBuilder(String[] selection) {

        String selectionArgs = new String();
        int stopPoint = selection.length;

        for (int i = 0; i < stopPoint; i++) {
            selectionArgs += "" + selection[i] + " = ? AND ";
        }
        if (selectionArgs.length() > 5) {
            selectionArgs = selectionArgs.substring(0, selectionArgs.length() - 5);
        }
        return selectionArgs;
    }

    public String ORBuilder(String[] selection) {
        String selectionArgs = new String();
        int stopPoint = selection.length;

        for (int i = 0; i < stopPoint; i++) {
            selectionArgs += "" + selection[i] + " = ? OR ";
        }

        if (selectionArgs.length() > 4) {
            selectionArgs = selectionArgs.substring(0, selectionArgs.length() - 4);
        }
        return selectionArgs;
    }

    public Cursor query(String table, String[] projection, String selection, String[] selectionArgs) {
        return query(table, projection, selection, selectionArgs, null);
    }

    public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();
        return query(uri, projection, selection, selectionArgs, orderBy);
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        Cursor cursor = mCtx.getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public long update(String table, ContentValuables cvalues, String selection, String[] selectionArgs) {
        long id = -1;
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();
        new AsyncQueryHandler(mCtx.getContentResolver()) {
        }.startUpdate(2, null, uri, cvalues, selection, selectionArgs);
        return id;
    }


    public void delete(String table, String selection, String[] selectionArgs) {
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();
        new AsyncQueryHandler(mCtx.getContentResolver()) {
        }.startDelete(-1, null, uri, selection, selectionArgs);
    }

    public void deleteAll(String table) {
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();
        new AsyncQueryHandler(mCtx.getContentResolver()) {
        }.startDelete(-1, null, uri, null, null);
    }

    public interface OnInsert {
        public void onInsertComplete();
    }

    public void insert(String table, ContentValuables values, OnInsert onInsert) {
        long id = -1;
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();
        new AsyncQueryHandler(mCtx.getContentResolver()) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                super.onInsertComplete(token, cookie, uri);
                if (onInsert != null) {
                    onInsert.onInsertComplete();
                }
            }
        }.startInsert(0, null, uri, values);
    }

    public Loader<Cursor> getLoader(String table) {
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();
        return new CursorLoader(mCtx, uri, null, null, null, null);
    }

    public Loader<Cursor> getLoader(Uri uri, String[] projection, String selection, String[] selecionArgs, String sortBy) {
        return new CursorLoader(mCtx, uri, projection, selection, selecionArgs, sortBy);
    }

    public Loader<Cursor> getLoader(String table, String[] projection, String selection, String[] selecionArgs, String sortBy) {
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();
        return new CursorLoader(mCtx, uri, projection, selection, selecionArgs, sortBy);
    }

    public void bulkInsert(String table, ContentValuables[] values) {
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();
        new AsyncQueryHandler(mCtx.getContentResolver()) {
        }.startBulkInsert(1, null, uri, values);
    }
}