package chat.rocket.app.db.collections;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import chat.rocket.rc.enumerations.NotifyActionType;
import chat.rocket.models.NotifyRoom;
import chat.rocket.models.RtcMedia;

/**
 * Created by julio on 27/11/15.
 */
public class StreamNotifyRoom extends Stream {
    public static final String COLLECTION_NAME = "stream-notify-room";
    private NotifyRoom notifyRoom;

    public void parseArgs() {
        if (args != null) {
            notifyRoom = new NotifyRoom();
            if (args.size() > 0) {
                String aux = args.get(0).getAsString();
                String[] arr = aux.split("/");
                notifyRoom.setRid(arr[0]);
                notifyRoom.setAction(NotifyActionType.fromType(arr[1]));
            }
            if (args.size() > 1) {
                if (NotifyActionType.TYPING.equals(notifyRoom.getAction())) {
                    notifyRoom.setUsername(args.get(1).getAsString());
                } else if (NotifyActionType.DELETE_MESSAGE.equals(notifyRoom.getAction())) {
                    notifyRoom.setId(args.get(1).getAsJsonObject().get("_id").getAsString());
                }
            }
            if (args.size() > 2) {
                if (NotifyActionType.WEB_RTC.equals(notifyRoom.getAction())) {
                    JsonObject jsonMediaContainer = args.get(2).getAsJsonObject();
                    if (jsonMediaContainer.has("media")) {
                        JsonObject jsonMedia = jsonMediaContainer.get("media").getAsJsonObject();
                        RtcMedia media = new RtcMedia();
                        if (jsonMedia.has("audio")) {
                            media.setAudio(jsonMedia.get("audio").getAsBoolean());
                        }
                        if (jsonMedia.has("video")) {
                            media.setVideo(jsonMedia.get("video").getAsBoolean());
                        }

                        if (jsonMediaContainer.has("from")) {
                            media.setFrom(jsonMediaContainer.get("from").getAsString());
                        }
                        if (jsonMediaContainer.has("remoteConnections")) {
                            List<String> remotes = new ArrayList<>();
                            Iterator<JsonElement> iterator = jsonMediaContainer.get("remoteConnections").getAsJsonArray().iterator();
                            while (iterator.hasNext()) {
                                remotes.add(iterator.next().getAsString());
                            }
                            media.setRemoteConnections(remotes);
                        }
                        notifyRoom.setMedia(media);
                    }
                } else {
                    notifyRoom.setHappening(args.get(2).getAsBoolean());
                }
            }
        }
    }

    public NotifyRoom getNotifyRoom() {
        return notifyRoom;
    }

}
