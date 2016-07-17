package chat.rocket.rc.enumerations;

import com.google.gson.annotations.SerializedName;

/**
 * Created by julio on 29/11/15.
 */
public enum ChannelType {
    @SerializedName("c")
    CHANNEL("c"),

    @SerializedName("p")
    PRIVATE("p"),

    @SerializedName("d")
    DIRECT("d");

    private final String type;

    ChannelType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
