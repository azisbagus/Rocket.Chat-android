package chat.rocket.rc.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by julio on 18/11/15.
 */
public class TimeStamp implements Serializable {
    @SerializedName("$date")
    private long date;

    public TimeStamp(long date) {
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
