package chat.rocket.rc;

import chat.rocket.rc.enumerations.ChannelType;
import meteor.operations.Meteor;
import meteor.operations.ResultListener;
import meteor.operations.Subscription;

/**
 * Created by julio on 19/11/15.
 */
public class RocketSubscriptions {
    private final Meteor mMeteor;

    public RocketSubscriptions(Meteor meteor) {
        mMeteor = meteor;
    }

    public Subscription userData(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("userData", null, listener), mMeteor);
    }

    public Subscription loginServiceConfiguration(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("meteor.loginServiceConfiguration", null, listener), mMeteor);
    }

    public Subscription settings(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("settings", null, listener), mMeteor);
    }

    public Subscription streamNotifyAll(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("stream-notify-all", null, listener), mMeteor);
    }

    public Subscription streamNotifyRoom(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("stream-notify-room", null, listener), mMeteor);
    }

    public Subscription streamNotifyUser(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("stream-notify-user", null, listener), mMeteor);
    }

    public Subscription roles(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("_roles", null, listener), mMeteor);
    }

    public Subscription streamMessages(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("stream-messages", null, listener), mMeteor);
    }

    public Subscription permissions(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("permissions", null, listener), mMeteor);
    }

    public Subscription subscription(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("subscription", null, listener), mMeteor);
    }

    public Subscription activeUsers(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("activeUsers", null, listener), mMeteor);
    }

    public Subscription adminSettings(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("admin-settings", null, listener), mMeteor);
    }

    public Subscription room(String roomName, ChannelType type, ResultListener listener) {
        return new Subscription(mMeteor.subscribe("room", new Object[]{type.type() + roomName}, listener), mMeteor);
    }

    public Subscription roomFiles(String rid, ResultListener listener) {
        return new Subscription(mMeteor.subscribe("roomFiles", new Object[]{rid}, listener), mMeteor);
    }

    public Subscription fullUserData(String filter, int limit, ResultListener listener) {
        return new Subscription(mMeteor.subscribe("fullUserData", new Object[]{filter, limit}, listener), mMeteor);
    }

    public Subscription filteredUsers(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("filteredUsers", new Object[]{}, listener), mMeteor);
    }

    public Subscription channelAutocomplete(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("channelAutocomplete", null, listener), mMeteor);
    }

    public Subscription starredMessages(String rid, ResultListener listener) {
        return new Subscription(mMeteor.subscribe("starredMessages", new Object[]{rid}, listener), mMeteor);
    }

    public Subscription pinnedMessages(String rid, ResultListener listener) {
        return new Subscription(mMeteor.subscribe("pinnedMessages", new Object[]{rid}, listener), mMeteor);
    }

    public Subscription meteorAutoupdateClientVersions(ResultListener listener) {
        return new Subscription(mMeteor.subscribe("meteor_autoupdate_clientVersions", null, listener), mMeteor);
    }
}
