package chat.rocket.models;

import java.util.List;

/**
 * Created by julio on 03/12/15.
 */
public class RtcMedia {
    private boolean audio;
    private boolean video;
    private List<String> remoteConnections;
    private String from;

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public List<String> getRemoteConnections() {
        return remoteConnections;
    }

    public void setRemoteConnections(List<String> remoteConnections) {
        this.remoteConnections = remoteConnections;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
