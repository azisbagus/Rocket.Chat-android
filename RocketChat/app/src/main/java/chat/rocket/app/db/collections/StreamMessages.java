package chat.rocket.app.db.collections;

import chat.rocket.app.db.dao.MessageDAO;
import chat.rocket.app.utils.Util;
import chat.rocket.rc.models.Message;

/**
 * Created by julio on 27/11/15.
 */
public class StreamMessages extends Stream {
    public static final String COLLECTION_NAME = "stream-messages";
    private String rid;
    private Message msg;

    @Override
    public void parseArgs() {
        if (args != null) {
            if (args.size() > 0) {
                rid = args.get(0).getAsJsonPrimitive().getAsString();
            }

            if (args.size() > 1) {
                msg = Util.GSON.fromJson(args.get(1), Message.class);
            }
        }
    }

    public void insert() {
        if (msg == null) {
            return;
        }
        new MessageDAO(msg).insert();
    }
}
