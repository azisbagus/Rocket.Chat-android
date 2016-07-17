package chat.rocket.rc.models;

import chat.rocket.rc.models.TimeStamp;

/**
 * Created by julio on 19/11/15.
 */
public class Token {
    private String id;
    private String token;
    private TimeStamp tokenExpires;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TimeStamp getTokenExpires() {
        return tokenExpires;
    }

    public void setTokenExpires(TimeStamp tokenExpires) {
        this.tokenExpires = tokenExpires;
    }
}
