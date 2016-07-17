package chat.rocket.rc.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import chat.rocket.rc.models.Channel;

/**
 * Created by julio on 18/11/15.
 */
public class Channels {
    @SerializedName("channels")
    private List<Channel> channels;

    public List<Channel> getChannels() {
        return channels;
    }
}
