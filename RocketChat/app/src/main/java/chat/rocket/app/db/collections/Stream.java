package chat.rocket.app.db.collections;

import com.google.gson.JsonElement;

import java.util.List;

/**
 * Created by julio on 27/11/15.
 */
public abstract class Stream {
    protected List<JsonElement> args;
    protected String userId;
    protected String subscriptionId;

    public abstract void parseArgs();
}
