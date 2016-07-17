package chat.rocket.rc.enumerations;

import com.google.gson.annotations.SerializedName;

public enum NotifyActionType {
    @SerializedName("typing")
    TYPING("typing"),

    @SerializedName("deleteMessage")
    DELETE_MESSAGE("deleteMessage"),

    @SerializedName("webrtc")
    WEB_RTC("webrtc");


    private String type;

    NotifyActionType(String type) {

        this.type = type;
    }

    public static NotifyActionType fromType(String type) {
        for (NotifyActionType notifyActionType : values()) {
            if (notifyActionType.type.equals(type)) {
                return notifyActionType;
            }
        }
        return null;
    }
}
