package chat.rocket.app.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.rocket.app.db.DBManager;
import chat.rocket.app.db.util.ContentValuables;
import chat.rocket.app.db.util.TableBuilder;
import chat.rocket.app.utils.Util;
import chat.rocket.rc.models.UrlParts;

import static chat.rocket.app.db.util.TableBuilder.TEXT;

/**
 * Created by julio on 29/11/15.
 */
public class UrlPartsDAO extends UrlParts implements ContentValuables {
    public static final String TABLE_NAME = "url_parts";

    public static final String COLUMN_DOCUMENT_ID = "msg_id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_META = "meta";
    public static final String COLUMN_HEADERS = "headers";
    public static final String COLUMN_PARSED_URL = "parsed_url";
    private final String documentID;

    public UrlPartsDAO(Cursor cursor) {
        documentID = cursor.getString(cursor.getColumnIndex(COLUMN_DOCUMENT_ID));
        url = cursor.getString(cursor.getColumnIndex(COLUMN_URL));

        String m = cursor.getString(cursor.getColumnIndex(COLUMN_META));
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        if (m != null) {
            meta = Util.GSON.fromJson(m, type);
        }

        String h = cursor.getString(cursor.getColumnIndex(COLUMN_HEADERS));
        if (h != null) {
            headers = Util.GSON.fromJson(h, type);
        }

        String p = cursor.getString(cursor.getColumnIndex(COLUMN_PARSED_URL));
        if (p != null) {
            parsedUrl = Util.GSON.fromJson(p, type);
        }

    }

    public static String createTableString() throws Exception {
        TableBuilder tb = new TableBuilder(TABLE_NAME);
        tb.setPrimaryKey(new String[]{COLUMN_URL, COLUMN_DOCUMENT_ID}, new String[]{TEXT, TEXT});
        tb.addColumn(COLUMN_HEADERS, TEXT, false);
        tb.addColumn(COLUMN_META, TEXT, false);
        tb.addColumn(COLUMN_PARSED_URL, TEXT, false);
        //tb.addFK(COLUMN_DOCUMENT_ID, TEXT, MessageDAO.TABLE_NAME, MessageDAO.COLUMN_DOCUMENT_ID, CASCADE, CASCADE);
        return tb.toString();
    }

    public UrlPartsDAO(String documentID, UrlParts urls) {
        super(urls.getUrl(), urls.getMeta(), urls.getHeaders(), urls.getParsedUrl());
        this.documentID = documentID;
    }

    public static List<UrlParts> get(String documentID) {
        List<UrlParts> list = new ArrayList<>();
        Cursor cursor = DBManager.getInstance().query(TABLE_NAME, null, COLUMN_DOCUMENT_ID + "=?", new String[]{documentID});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    list.add(new UrlPartsDAO(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    public void insert() {

    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOCUMENT_ID, documentID);
        values.put(COLUMN_URL, url);
        values.put(COLUMN_META, Util.GSON.toJson(meta));
        values.put(COLUMN_HEADERS, Util.GSON.toJson(headers));
        values.put(COLUMN_PARSED_URL, Util.GSON.toJson(parsedUrl));
        return values;
    }
}
