package chat.rocket.rc.models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by julio on 18/11/15.
 */
public class UsernameId {
    @SerializedName("_id")
    protected String id;
    @SerializedName("username")
    protected String username;

    public UsernameId() {
    }

    public UsernameId(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
