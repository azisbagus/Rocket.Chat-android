package chat.rocket.rc.enumerations;

import com.google.gson.annotations.SerializedName;

/**
 * Created by julio on 27/11/15.
 */
public enum MessageType {
    @SerializedName("r")
    ROOM_NAME_CHANGED("r"),

    @SerializedName("au")
    USER_ADDED_BY("au"),

    @SerializedName("ru")
    USER_REMOVED_BY("ru"),

    @SerializedName("ul")
    USER_LEFT("ul"),

    @SerializedName("uj")
    USER_JOINED_CHANNEL("uj"),

    @SerializedName("wm")
    WELCOME("wm"),

    @SerializedName("rm")
    MESSAGE_REMOVED("rm"),

    @SerializedName("rtc")
    RENDER_MESSAGE("rtc"),

    @SerializedName("room_changed_privacy")
    ROOM_CHANGED_PRIVACE("room_changed_privacy"),

    @SerializedName("room_changed_topic")
    ROOM_CHANGED_TOPIC("room_changed_topic");
    private String id;

    MessageType(String id) {

        this.id = id;
    }
}
