package chat.rocket.app.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.Loader;

import chat.rocket.app.db.DBContentProvider;
import chat.rocket.app.db.DBManager;
import chat.rocket.app.db.util.ContentValuables;
import chat.rocket.app.db.util.TableBuilder;
import chat.rocket.rc.enumerations.MessageType;
import chat.rocket.rc.models.FileId;
import chat.rocket.rc.models.Message;
import chat.rocket.rc.models.TimeStamp;
import chat.rocket.rc.models.UrlParts;
import timber.log.Timber;

import static chat.rocket.app.db.util.TableBuilder.INTEGER;
import static chat.rocket.app.db.util.TableBuilder.ON_CONFLICT_REPLACE;
import static chat.rocket.app.db.util.TableBuilder.TEXT;

/**
 * Created by julio on 29/11/15.
 */
public class MessageDAO extends Message implements ContentValuables {
    public static final String TABLE_NAME = "messages";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DOCUMENT_ID = "document_id";
    public static final String COLUMN_MSG = "msg";
    public static final String COLUMN_RID = "rid";
    public static final String COLUMN_TS = "ts";
    public static final String COLUMN_EDITED_AT = "edited_at";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_FILE_ID = "file_id";
    public static final String COLUMN_SCORE = "score";

    public MessageDAO(Message msg) {
        super(msg.getDocumentID(), msg.getMsg(), msg.getRid(),
                msg.getTs(), msg.getType(), msg.getUsernameId(),
                msg.getFile(), msg.getUrls(), msg.getEditedAt(),
                msg.getEditedBy(), msg.getScore());
    }


    public static String createTableString() throws Exception {
        TableBuilder tb = new TableBuilder(TABLE_NAME);
        tb.setPrimaryKey(COLUMN_ID, tb.INTEGER, true);
        tb.addColumn(COLUMN_DOCUMENT_ID, TEXT, true);
        tb.addColumn(COLUMN_MSG, TEXT, false);
        tb.addColumn(COLUMN_RID, TEXT, false);
        tb.addColumn(COLUMN_TS, INTEGER, false);
        tb.addColumn(COLUMN_EDITED_AT, INTEGER, false);
        tb.addColumn(COLUMN_TYPE, TEXT, false);
        tb.addColumn(COLUMN_FILE_ID, TEXT, false);
        tb.addColumn(COLUMN_SCORE, TEXT, false);
        tb.makeUnique(new String[]{COLUMN_DOCUMENT_ID, COLUMN_RID}, ON_CONFLICT_REPLACE);
        return tb.toString();
    }


    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOCUMENT_ID, documentID);
        values.put(COLUMN_MSG, msg);
        values.put(COLUMN_RID, rid);
        values.put(COLUMN_TS, ts != null ? ts.getDate() : 0);
        values.put(COLUMN_EDITED_AT, editedAt != null ? editedAt.getDate() : 0);
        if(type == null){
            Timber.d("nullllllllllllllll");
        }
        values.put(COLUMN_TYPE, type != null ? type.name() : null);
        values.put(COLUMN_FILE_ID, file != null ? file.getId() : null);
        values.put(COLUMN_SCORE, score);

        return values;
    }

    public MessageDAO(Cursor cursor) {
        documentID = cursor.getString(cursor.getColumnIndex(COLUMN_DOCUMENT_ID));
        msg = cursor.getString(cursor.getColumnIndex(COLUMN_MSG));
        rid = cursor.getString(cursor.getColumnIndex(COLUMN_RID));

        long time = cursor.getLong(cursor.getColumnIndex(COLUMN_TS));
        if (time != 0) {
            ts = new TimeStamp(time);
        }

        String t = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
        if (t != null) {
            type = MessageType.valueOf(t);
        }

        usernameId = UsernameIdDAO.get(documentID);

        String fileId = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_ID));
        if (fileId != null) {
            file = new FileId(fileId);
        }

        urls = UrlPartsDAO.get(documentID);

        time = cursor.getLong(cursor.getColumnIndex(COLUMN_EDITED_AT));
        if (time != 0) {
            editedAt = new TimeStamp(time);
        }

        editedBy = EditedByDAO.get(documentID);

        score = cursor.getLong(cursor.getColumnIndex(COLUMN_SCORE));

    }

    public void insert() {
        DBManager.getInstance().insert(TABLE_NAME, this, null);
        new UsernameIdDAO(documentID, usernameId).insert();
        if (editedBy != null) {
            new EditedByDAO(documentID, editedBy).insert();
        }
        if (urls != null) {
            for (UrlParts url : urls) {
                //TODO: bulkInsert
                new UrlPartsDAO(documentID, url).insert();
            }
        }
    }

    public static Loader<Cursor> getLoader(String rid) {
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(MessageDAO.TABLE_NAME).build();
        return DBManager.getInstance().getLoader(uri, null, MessageDAO.COLUMN_RID + "=?", new String[]{rid}, COLUMN_TS + " ASC");
    }

    public static void remove(String documentID) {
        DBManager.getInstance().delete(TABLE_NAME, COLUMN_DOCUMENT_ID + "=?", new String[]{documentID});
    }
}
