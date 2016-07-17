package chat.rocket.models;

import java.io.Serializable;

import chat.rocket.rc.enumerations.NotifyActionType;

/**
 * Created by julio on 01/12/15.
 */
//TODO: Migrate to Parceable
public class NotifyRoom implements Serializable {

    protected String rid;
    protected NotifyActionType action;
    protected String username;
    protected boolean happening;
    private String id;

    private RtcMedia media;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public NotifyActionType getAction() {
        return action;
    }

    public void setAction(NotifyActionType action) {
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isHappening() {
        return happening;
    }

    public void setHappening(boolean happening) {
        this.happening = happening;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public RtcMedia getMedia() {
        return media;
    }

    public void setMedia(RtcMedia media) {
        this.media = media;
    }
}
