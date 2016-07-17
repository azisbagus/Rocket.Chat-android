package chat.rocket.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import chat.rocket.rc.enumerations.ChannelType;
import chat.rocket.rc.models.TimeStamp;

/**
 * Created by julio on 24/11/15.
 */
public class RcSubscription implements Serializable {
    protected boolean alert;
    protected TimeStamp ls;
    protected String name;
    protected boolean open;
    protected String rid;
    @SerializedName("t")
    protected ChannelType type;
    protected TimeStamp ts;
    protected int unread;
    @SerializedName("f")
    protected boolean favorited;

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean hasAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public TimeStamp getLs() {
        return ls;
    }

    public void setLs(TimeStamp ls) {
        this.ls = ls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public TimeStamp getTs() {
        return ts;
    }

    public void setTs(TimeStamp ts) {
        this.ts = ts;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public String getFormattedName() {
        String prefix = "#";
        if (ChannelType.DIRECT.equals(type)) {
            prefix = "@";
        }
        return prefix + name;
    }
}
