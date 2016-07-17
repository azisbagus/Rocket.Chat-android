package chat.rocket.app.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v4.util.LruCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import chat.rocket.app.db.DBManager;
import chat.rocket.app.db.util.ContentValuables;
import chat.rocket.app.db.util.TableBuilder;
import chat.rocket.app.utils.Util;

/**
 * Created by julio on 22/11/15.
 */
public class CollectionDAO implements ContentValuables {
    private static LruCache<String, LruCache<String, CollectionDAO>> mCache = new LruCache<>(15);

    public static final String TABLE_NAME = "collections";
    // android internal value, to be used with cursors
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DOCUMENT_ID = "document_id";
    public static final String COLUMN_COLLECTION_NAME = "collection_name";
    public static final String COLUMN_FIELDS = "collection_value";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public CollectionDAO(Cursor cursor) {
        collectionName = cursor.getString(cursor.getColumnIndex(COLUMN_COLLECTION_NAME));
        documentID = cursor.getString(cursor.getColumnIndex(COLUMN_DOCUMENT_ID));
        newValuesJson = cursor.getString(cursor.getColumnIndex(COLUMN_FIELDS));
    }

    public static String createTableString() throws Exception {
        TableBuilder tb = new TableBuilder(TABLE_NAME);
        tb.setPrimaryKey(COLUMN_ID, TableBuilder.INTEGER, true);
        tb.addColumn(COLUMN_DOCUMENT_ID, TableBuilder.TEXT, true);
        tb.addColumn(COLUMN_COLLECTION_NAME, TableBuilder.TEXT, true);
        tb.addColumn(COLUMN_FIELDS, TableBuilder.TEXT, false);
        tb.addColumn(COLUMN_UPDATED_AT, TableBuilder.TEXT, false);

        tb.makeUnique(new String[]{COLUMN_COLLECTION_NAME, COLUMN_DOCUMENT_ID}, TableBuilder.ON_CONFLICT_REPLACE);
        return tb.toString();
    }

    protected String collectionName;
    protected String documentID;
    protected String newValuesJson;

    public String getNewValuesJson() {
        return newValuesJson;
    }

    public CollectionDAO(String collectionName, String documentID, String newValuesJson) {
        this.collectionName = collectionName;
        this.documentID = documentID;
        this.newValuesJson = newValuesJson;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOCUMENT_ID, documentID);
        values.put(COLUMN_COLLECTION_NAME, collectionName);
        values.put(COLUMN_UPDATED_AT, new Date().getTime() / 1000);
        values.put(COLUMN_FIELDS, newValuesJson);
        return values;
    }

    public static List<CollectionDAO> query(String collectionName) {
        List<CollectionDAO> list = new ArrayList<>();
        LruCache<String, CollectionDAO> cache = mCache.get(collectionName);
        if (cache != null && cache.size() > 0) {
            list.addAll(cache.snapshot().values());
            return list;
        }

        Cursor cursor = DBManager.getInstance().query(TABLE_NAME, null, COLUMN_COLLECTION_NAME + "=?", new String[]{collectionName});
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    list.add(new CollectionDAO(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    public static CollectionDAO query(String collectionName, String documentID) {
        CollectionDAO dao = null;
        LruCache<String, CollectionDAO> cache = mCache.get(collectionName);
        if (cache != null) {
            dao = cache.get(documentID);
            if (dao != null) {
                return dao;
            }
        }

        Cursor cursor = DBManager.getInstance().query(TABLE_NAME, null, COLUMN_COLLECTION_NAME + "=? AND " + COLUMN_DOCUMENT_ID + "=?", new String[]{collectionName, documentID});
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    dao = new CollectionDAO(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return dao;
    }

    public void insert() {
        insertIntoCache();
        DBManager.getInstance().insert(TABLE_NAME, this, new DBManager.OnInsert() {
            @Override
            public void onInsertComplete() {
                removeFromCache();
            }
        });
    }

    private void removeFromCache() {
        synchronized (mCache) {
            LruCache<String, CollectionDAO> cache = mCache.get(collectionName);
            if (cache != null) {
                cache.remove(documentID);
            }
        }
    }

    private void insertIntoCache() {
        synchronized (mCache) {
            LruCache<String, CollectionDAO> cache = mCache.get(collectionName);
            if (cache == null) {
                cache = new LruCache<>(50);
                mCache.put(collectionName, cache);
            }
            cache.put(documentID, this);
        }
    }

    public void update() {
        insertIntoCache();
        DBManager.getInstance().update(TABLE_NAME,
                this,
                COLUMN_COLLECTION_NAME + "=? AND " + COLUMN_DOCUMENT_ID + "=?",
                new String[]{collectionName, documentID});
    }

    public void remove() {
        mCache.remove(collectionName + documentID);

        DBManager.getInstance().delete(TABLE_NAME, COLUMN_COLLECTION_NAME + "=? AND " + COLUMN_DOCUMENT_ID + "=?",
                new String[]{collectionName, documentID});
    }

    public CollectionDAO plusUpdatedValues(String updatedValuesJson) throws JSONException {
        if (updatedValuesJson != null) {
            newValuesJson = Util.deepMerge(new JSONObject(this.newValuesJson), new JSONObject(updatedValuesJson)).toString();
        }

        return this;
    }

    public CollectionDAO lessUpdatedValues(String removedValuesJson) throws JSONException {
        if (removedValuesJson != null) {
            newValuesJson = Util.deepRemove(new JSONObject(removedValuesJson), new JSONObject(this.newValuesJson)).toString();
        }
        return this;
    }

}
