package chat.rocket.app.db.collections;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import chat.rocket.app.db.dao.CollectionDAO;
import chat.rocket.app.utils.Util;
import chat.rocket.rc.enumerations.ChannelType;
import chat.rocket.rc.models.UsernameId;

/**
 * Created by julio on 29/11/15.
 */
public class RcRoom {
    public static final String COLLECTION_NAME = "rocketchat_room";

    private String name;
    @SerializedName("t")
    private ChannelType type;
    @SerializedName("u")
    private UsernameId usernameId;
    private List<String> usernames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public UsernameId getUsernameId() {
        return usernameId;
    }

    public void setUsernameId(UsernameId usernameId) {
        this.usernameId = usernameId;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public static RcRoom getRCRoom(String rid) {
        CollectionDAO dao = CollectionDAO.query(RcRoom.COLLECTION_NAME, rid);
        RcRoom room = null;
        if (dao != null) {
            room = Util.GSON.fromJson(dao.getNewValuesJson(), RcRoom.class);
        }
        return room;
    }
}
