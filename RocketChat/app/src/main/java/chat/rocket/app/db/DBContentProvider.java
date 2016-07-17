package chat.rocket.app.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import chat.rocket.app.BuildConfig;
import chat.rocket.app.db.dao.CollectionDAO;
import chat.rocket.app.ui.widgets.LinkfiedTextView;


public class DBContentProvider extends ContentProvider {
    public static final String ONE_ROW_LIMIT = "one_row_limit";
    public static final String ORDER_BY = " ORDER BY ";
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider/";
    public static final String RAW_QUERY = "RAW_QUERY";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String FILTER = "filter";
    public static final Uri WEB_CONTENT_URI = BASE_CONTENT_URI.buildUpon().build();
    public static final Uri USERNAME_CONTENT_URI = BASE_CONTENT_URI.buildUpon().build();
    private SQLiteHelper mSqlHelper;


    @Override
    public String getType(Uri uri) {
        //TODO: Impove the matcher
        String path = uri.getPath().replaceFirst("/", "");
        if (LinkfiedTextView.sUrlMatcher.matcher(path).matches()) {
            return "vnd.android.cursor.item/vnd." + BuildConfig.APPLICATION_ID + ".provider.text_html";
        }
        if (LinkfiedTextView.sUsernameMatcher.matcher(path).matches()) {
            return "vnd.android.cursor.item/vnd." + BuildConfig.APPLICATION_ID + ".provider.user_name";
        }
        return "vnd.android.cursor.dir/vnd." + AUTHORITY;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        String table = uri.getLastPathSegment();
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        // tweak
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                if (value.containsKey("_id") && value.getAsLong("_id") > 0) {
                    // workaround foreign keys does not work as desired with on conflict replace policy
                    if (0 == db.update(table, value, "_id=?", new String[]{value.getAsString("_id")})) {
                        db.insertOrThrow(table, null, value);
                    }
                } else {
                    db.insertWithOnConflict(table, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
        } finally {
            db.endTransaction();
        }
        return values.length;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        String table = uri.getLastPathSegment();
        int count = db.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // we know that, the last path segment ALWAYS will be the table of insertion
        String table = uri.getLastPathSegment();
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        // tweak
        db.beginTransaction();
        Uri newUri = null;
        long id = 0l;
        try {
            if (values.containsKey("_id") && values.getAsLong("_id") > 0) {
                // workaround foreign keys does not work as desired with on conflict replace policy
                if (0 == db.update(table, values, "_id=?", new String[]{values.getAsString("_id")})) {
                    id = db.insertOrThrow(table, null, values);
                } else {
                    id = 1l;
                }
            } else {
                id = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            if (id > 0) {
                newUri = ContentUris.withAppendedId(uri, id);
                db.setTransactionSuccessful();
                if (values.containsKey(CollectionDAO.COLUMN_COLLECTION_NAME)) {
                    Uri uri1 = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).
                            appendQueryParameter(FILTER, values.getAsString(CollectionDAO.COLUMN_COLLECTION_NAME)).build();
                    getContext().getContentResolver().notifyChange(uri1, null);
                }
                getContext().getContentResolver().notifyChange(uri, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        if (newUri != null) {
            return newUri;
        }
        throw new SQLException("Failed to insertOrReplace row into " + uri + ",  values:" + values.toString());
    }

    @Override
    public boolean onCreate() {
        mSqlHelper = SQLiteHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(final Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c;
        SQLiteDatabase db = mSqlHelper.getReadableDatabase();
        // we know that, the last path segment ALWAYS will be a table
        String table = uri.getLastPathSegment();
        Uri listenedUri = uri;
        // when the raw string is found in the uri it means we are using a raw sql operation using the selection and selectionArgs
        if (uri.toString().contains(RAW_QUERY)) {
            // we reconstruct the uri to remove the one raw apart to receive updates about the queried data
            listenedUri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();
            StringBuilder sql = new StringBuilder(selection);
            if (!TextUtils.isEmpty(sortOrder)) {
                // append sort order parameter
                sql.append(ORDER_BY).append(sortOrder);
            }
            // execute raw query
            c = db.rawQuery(sql.toString(), selectionArgs);

        } else if (uri.toString().contains(ONE_ROW_LIMIT)) {
            // we limit the return to one row limit
            c = db.query(false, table, projection, selection, selectionArgs, null, null, sortOrder, "1");
            // we reconstruct the uri to remove the one row limit apart to receive updates about the queried data
            listenedUri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();

        } else {
            // execute standard query
            c = db.query(false, table, projection, selection, selectionArgs, null, null, sortOrder, null);
        }

        // if the query was ok we have a cursor and configure it to listen to any change the the given uri
        if (c != null) {
            // notify listeners
            c.setNotificationUri(getContext().getContentResolver(), listenedUri);
        }

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // we know that, the last path segment ALWAYS will be the table being updated
        String table = uri.getLastPathSegment();
        // get a writable database instance
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        int count = 0;
        Uri listenedUri = uri;
        // begin a transaction
        db.beginTransaction();
        try {
            // when the raw string is found in the uri it means we are using a raw sql operation using the selection
            if (uri.toString().contains(RAW_QUERY)) {
                // we reconstruct the uri to remove the raw apart and notify and content observer listening to the specified table
                listenedUri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(table).build();
                // execute query
                db.execSQL(selection);
            } else {
                // execute standard update
                count = db.update(table, values, selection, selectionArgs);
            }
            // set transaction as successful
            db.setTransactionSuccessful();
            // notify listeners
            getContext().getContentResolver().notifyChange(listenedUri, null);

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            // end transaction
            db.endTransaction();
        }

        return count;
    }
}