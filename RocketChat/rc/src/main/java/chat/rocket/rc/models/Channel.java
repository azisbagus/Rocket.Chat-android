package chat.rocket.rc.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import chat.rocket.rc.enumerations.ChannelType;


/**
 * Created by julio on 18/11/15.
 */
public class Channel {
    @SerializedName("_id")
    private String id;
    @SerializedName("ts")
    private TimeStamp ts;
    @SerializedName("t")
    private ChannelType type;
    @SerializedName("name")
    private String name;
    @SerializedName("usernames")
    private List<String> usernames;
    private long msgs;
    @SerializedName("u")
    private UsernameId usernameId;
    @SerializedName("default")
    private boolean defaultRoom;
    private TimeStamp lm;
    private boolean archived;

    public TimeStamp getTs() {
        return ts;
    }

    public void setTs(TimeStamp ts) {
        this.ts = ts;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public long getMsgs() {
        return msgs;
    }

    public void setMsgs(long msgs) {
        this.msgs = msgs;
    }

    public boolean isDefaultRoom() {
        return defaultRoom;
    }

    public void setDefaultRoom(boolean defaultRoom) {
        this.defaultRoom = defaultRoom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UsernameId getUsernameId() {
        return usernameId;
    }

    public void setUsernameId(UsernameId usernameId) {
        this.usernameId = usernameId;
    }

    public TimeStamp getLm() {
        return lm;
    }

    public void setLm(TimeStamp lm) {
        this.lm = lm;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
