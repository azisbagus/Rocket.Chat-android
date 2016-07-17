package chat.rocket.app.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import chat.rocket.app.db.DBManager;
import chat.rocket.app.db.util.ContentValuables;
import chat.rocket.app.db.util.TableBuilder;
import chat.rocket.rc.models.UsernameId;

import static chat.rocket.app.db.util.TableBuilder.TEXT;

/**
 * Created by julio on 29/11/15.
 */
public class UsernameIdDAO extends UsernameId implements ContentValuables {
    public static final String TABLE_NAME = "username";

    public static final String COLUMN_DOCUMENT_ID = "document_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_USERNAME_ID = "username_id";

    private String documentID;

    public UsernameIdDAO(Cursor cursor) {
        documentID = cursor.getString(cursor.getColumnIndex(COLUMN_DOCUMENT_ID));
        id = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME_ID));
        username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
    }

    public static String createTableString() throws Exception {
        return getCreateTableString(TABLE_NAME).toString();
    }

    protected static TableBuilder getCreateTableString(String tableName) throws Exception {
        TableBuilder tb = new TableBuilder(tableName);
        tb.setPrimaryKey(COLUMN_DOCUMENT_ID, TEXT, false);
        tb.addColumn(COLUMN_USERNAME, TEXT, true);
        tb.addColumn(COLUMN_USERNAME_ID, TEXT, true);
        //tb.addFK(COLUMN_DOCUMENT_ID, TEXT, MessageDAO.TABLE_NAME, MessageDAO.COLUMN_DOCUMENT_ID, CASCADE, CASCADE);
        return tb;
    }

    public UsernameIdDAO(String documentID, UsernameId usernameId) {
        super(usernameId.getId(), usernameId.getUsername());
        this.documentID = documentID;
    }

    public void insert() {
        DBManager.getInstance().insert(TABLE_NAME, this, null);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOCUMENT_ID, documentID);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_USERNAME_ID, id);
        return values;
    }

    public static UsernameId get(String msgId) {
        Cursor cursor = DBManager.getInstance().query(TABLE_NAME, null, COLUMN_DOCUMENT_ID + "=?", new String[]{msgId});
        UsernameId usernameId = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    usernameId = new UsernameIdDAO(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return usernameId;
    }
}
