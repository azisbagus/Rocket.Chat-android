package chat.rocket.app.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.Loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import chat.rocket.app.db.DBContentProvider;
import chat.rocket.app.db.DBManager;
import chat.rocket.app.db.util.ContentValuables;
import chat.rocket.app.db.util.TableBuilder;
import chat.rocket.models.RcSubscription;
import chat.rocket.rc.enumerations.ChannelType;
import chat.rocket.rc.models.TimeStamp;

import static chat.rocket.app.db.util.TableBuilder.INTEGER;
import static chat.rocket.app.db.util.TableBuilder.ON_CONFLICT_REPLACE;
import static chat.rocket.app.db.util.TableBuilder.TEXT;

/**
 * Created by julio on 01/12/15.
 */
public class RcSubscriptionDAO extends RcSubscription implements ContentValuables {

    public static final String COLLECTION_NAME = "rocketchat_subscription";

    public static final String TABLE_NAME = "rc_subs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ALERT = "alert";
    public static final String COLUMN_F = "f";
    public static final String COLUMN_LS = "ls";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_OPEN = "open";
    public static final String COLUMN_RID = "rid";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TS = "ts";
    public static final String COLUMN_UNREAD = "unread";
    public static final String COLUMN_DOC_ID = "doc_id";

    public static String createTableString() throws Exception {
        TableBuilder tb = new TableBuilder(TABLE_NAME);
        tb.setPrimaryKey(COLUMN_ID, INTEGER, true);
        tb.addColumn(COLUMN_ALERT, INTEGER, false);
        tb.addColumn(COLUMN_F, INTEGER, false);
        tb.addColumn(COLUMN_LS, INTEGER, false);
        tb.addColumn(COLUMN_NAME, TEXT, true);
        tb.addColumn(COLUMN_OPEN, INTEGER, false);
        tb.addColumn(COLUMN_RID, TEXT, false);
        tb.addColumn(COLUMN_TYPE, TEXT, false);
        tb.addColumn(COLUMN_TS, INTEGER, false);
        tb.addColumn(COLUMN_UNREAD, INTEGER, false);
        tb.addColumn(COLUMN_DOC_ID, TEXT, true);
        tb.makeUnique(COLUMN_RID, ON_CONFLICT_REPLACE);
        return tb.toString();
    }

    private String documentId;

    public RcSubscriptionDAO(Cursor cursor) {
        alert = cursor.getInt(cursor.getColumnIndex(COLUMN_ALERT)) > 0;
        favorited = cursor.getInt(cursor.getColumnIndex(COLUMN_F)) > 0;
        long time = cursor.getLong(cursor.getColumnIndex(COLUMN_LS));
        if (time != 0) {
            ls = new TimeStamp(time);
        }
        name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        open = cursor.getInt(cursor.getColumnIndex(COLUMN_OPEN)) > 0;
        rid = cursor.getString(cursor.getColumnIndex(COLUMN_RID));
        String t = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
        if (t != null) {
            type = ChannelType.valueOf(t);
        }
        time = cursor.getLong(cursor.getColumnIndex(COLUMN_TS));
        if (time != 0) {
            ts = new TimeStamp(time);
        }
        unread = cursor.getInt(cursor.getColumnIndex(COLUMN_UNREAD));
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ALERT, alert ? 1 : 0);
        values.put(COLUMN_F, favorited ? 1 : 0);
        values.put(COLUMN_LS, ls != null ? ls.getDate() : 0);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_OPEN, open ? 1 : 0);
        values.put(COLUMN_RID, rid);
        values.put(COLUMN_TYPE, type != null ? type.name() : null);
        values.put(COLUMN_TS, ts != null ? ts.getDate() : 0);
        values.put(COLUMN_UNREAD, unread);
        values.put(COLUMN_DOC_ID, documentId);
        return values;
    }

    public static Loader<Cursor> getLoader(ChannelType type) {
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        return DBManager.getInstance().getLoader(uri, null, COLUMN_TYPE + "=?", new String[]{type.name()}, COLUMN_UNREAD + " DESC, " + COLUMN_TS + " DESC");
    }

    public static Loader<Cursor> getLoader(boolean favorited) {
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        return DBManager.getInstance().getLoader(uri, null, COLUMN_F + "=?", new String[]{favorited ? "1" : "0"}, COLUMN_UNREAD + " DESC, " + COLUMN_LS + " DESC");
    }

    public void insert(String documentId) {
        this.documentId = documentId;
        DBManager.getInstance().insert(TABLE_NAME, this, null);
    }

    public void update(String documentId, String json) {
        this.documentId = documentId;
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        JsonObject obj = element.getAsJsonObject();

        if (obj.has("unread")) {
            unread = obj.get("unread").getAsInt();
        }
        if (obj.has("alert")) {
            alert = obj.get("alert").getAsBoolean();
        }
        DBManager.getInstance().update(TABLE_NAME, this, COLUMN_DOC_ID + "=?", new String[]{documentId});
    }

    public static RcSubscriptionDAO get(String documentID) {
        Cursor cursor = DBManager.getInstance().query(TABLE_NAME, null, COLUMN_DOC_ID + "=?", new String[]{documentID});
        RcSubscriptionDAO rcSub = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                rcSub = new RcSubscriptionDAO(cursor);
            }
            cursor.close();
        }

        return rcSub;
    }
}
