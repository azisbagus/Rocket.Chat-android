package chat.rocket.rc.models;

import java.util.List;

import chat.rocket.rc.models.Channel;
import chat.rocket.rc.models.Message;
import chat.rocket.rc.models.UsernameId;

/**
 * Created by julio on 21/11/15.
 */
public class MessageSearchResults {
    private List<Message> messages;
    private List<UsernameId> users;
    private List<Channel> channels;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<UsernameId> getUsers() {
        return users;
    }

    public void setUsers(List<UsernameId> users) {
        this.users = users;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }
}
