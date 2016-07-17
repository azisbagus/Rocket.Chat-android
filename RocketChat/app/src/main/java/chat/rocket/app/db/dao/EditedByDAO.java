package chat.rocket.app.db.dao;

import android.database.Cursor;

import chat.rocket.app.db.util.ContentValuables;
import chat.rocket.rc.models.UsernameId;

/**
 * Created by julio on 29/11/15.
 */
public class EditedByDAO extends UsernameIdDAO implements ContentValuables {
    public static final String TABLE_NAME = "edited_by";

    public EditedByDAO(Cursor cursor) {
        super(cursor);
    }

    public EditedByDAO(String msgId, UsernameId usernameId) {
        super(msgId, usernameId);
    }

    public static String createTableString() throws Exception {
        return getCreateTableString(TABLE_NAME).toString();
    }

}
