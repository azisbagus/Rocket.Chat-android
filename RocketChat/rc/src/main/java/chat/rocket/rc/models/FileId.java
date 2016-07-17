package chat.rocket.rc.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by julio on 21/11/15.
 */
public class FileId {
    @SerializedName("_id")
    private String id;

    public FileId(String fileid) {
        id = fileid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
