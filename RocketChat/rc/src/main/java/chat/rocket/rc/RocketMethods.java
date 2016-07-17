package chat.rocket.rc;

import java.util.HashMap;
import java.util.Map;

import chat.rocket.rc.listeners.AddUserToRoomListener;
import chat.rocket.rc.listeners.ArchiveRoomListener;
import chat.rocket.rc.listeners.CanAccessRoomListener;
import chat.rocket.rc.listeners.ChannelsListListener;
import chat.rocket.rc.listeners.CreateChannelListener;
import chat.rocket.rc.listeners.CreateDirectMessageListener;
import chat.rocket.rc.listeners.CreatePrivateGroupListener;
import chat.rocket.rc.listeners.DeleteMessageListener;
import chat.rocket.rc.listeners.EraseRoomListener;
import chat.rocket.rc.listeners.FileUploadListener;
import chat.rocket.rc.listeners.GetTotalChannelsListener;
import chat.rocket.rc.listeners.HideRoomListener;
import chat.rocket.rc.listeners.JoinRoomListener;
import chat.rocket.rc.listeners.LeaveRoomListener;
import chat.rocket.rc.listeners.LoadHistoryListener;
import chat.rocket.rc.listeners.LoginListener;
import chat.rocket.rc.listeners.OpenRoomListener;
import chat.rocket.rc.listeners.ReadMessagesListener;
import chat.rocket.rc.listeners.ResetAvatarListener;
import chat.rocket.rc.listeners.SaveRoomNameListener;
import chat.rocket.rc.listeners.SendConfirmationEmailListener;
import chat.rocket.rc.listeners.SendForgotPasswordEmailListener;
import chat.rocket.rc.listeners.SendMessageListener;
import chat.rocket.rc.listeners.UpdateMessageListener;
import chat.rocket.rc.models.Message;
import chat.rocket.rc.models.TimeStamp;
import meteor.operations.Meteor;
import meteor.operations.MeteorException;
import meteor.operations.ResultListener;

/**
 * Created by julio on 19/11/15.
 */
public class RocketMethods {
    private Meteor mMeteor;

    public RocketMethods(Meteor meteor) {
        mMeteor = meteor;
    }

    public void addUserToRoom(String rid, String username, AddUserToRoomListener listener) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("rid", rid);
        data.put("username", username);
        mMeteor.call("addUserToRoom", new Object[]{data}, listener);
    }

    public void archiveRoom(String rid, ArchiveRoomListener listener) {
        mMeteor.call("archiveRoom", new Object[]{rid}, listener);
    }

    public void canAccessRoom(String rid, String userId, CanAccessRoomListener listener) {
        mMeteor.call("canAccessRoom", new Object[]{rid, userId}, listener);
    }

    public void channelsList(ChannelsListListener listener) {
        mMeteor.call("channelsList", listener);
    }

    public void createChannel(String name, CreateChannelListener listener) {
        mMeteor.call("createChannel", new Object[]{name, new short[]{}}, listener);
    }

    public void createDirectMessage(String username, CreateDirectMessageListener listener) {
        mMeteor.call("createDirectMessage", new Object[]{username}, listener);
    }

    public void createPrivateGroup(String name, CreatePrivateGroupListener listener) {
        mMeteor.call("createPrivateGroup", new Object[]{name, new short[]{}}, listener);
    }

    public void deleteMessage(String id, DeleteMessageListener listener) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("_id", id);
        mMeteor.call("DELETE_MESSAGE", new Object[]{data}, listener);
    }

    public void deleteUser(String userId, ResultListener listener) {
        mMeteor.call("deleteUser", new Object[]{userId}, listener);
    }

    public void eraseRoom(String rid, EraseRoomListener listener) {
        mMeteor.call("eraseRoom", new Object[]{rid}, listener);
    }

    public void getAvatarSuggestion(ResultListener listener) {
        //TODO: How does it work???
    }

    public void getRoomIdByNameOrId(String rid, ResultListener listener) {
        mMeteor.call("getRoomIdByNameOrId", new Object[]{rid}, listener);
    }

    public void getTotalChannels(GetTotalChannelsListener listener) {
        mMeteor.call("getTotalChannels", listener);
    }

    public void getUsernameSuggestion(ResultListener listener) {
        mMeteor.call("getUsernameSuggestion", listener);
    }

    public void hideRoom(String rid, HideRoomListener listener) {
        mMeteor.call("hideRoom", new Object[]{rid}, listener);
    }

    public void joinRoom(String rid, JoinRoomListener listener) {
        mMeteor.call("joinRoom", new Object[]{rid}, listener);
    }

    public void leaveRoom(String rid, LeaveRoomListener listener) {
        mMeteor.call("leaveRoom", new Object[]{rid}, listener);
    }

    public void loadHistory(String rid, TimeStamp end, int limit, TimeStamp ls, LoadHistoryListener listener) {
        mMeteor.call("loadHistory", new Object[]{rid, end, limit, ls}, listener);
    }

    public void loadLocale(String locale, ResultListener listener) {
        //TODO: It loaded some js, is it a bug or by design? need to ask..
        mMeteor.call("loadLocale", new Object[]{locale}, listener);
    }

    public void loadMissedMessages(String rid, long start, ResultListener listener) {
        mMeteor.call("loadMissedMessages", new Object[]{rid, start}, listener);
    }

    public void loginWithUsername(String username, String pass, LoginListener listener) {
        mMeteor.loginWithUsername(username, pass, listener);
    }

    public void loginWithEmail(String email, String pass, LoginListener listener) {
        mMeteor.loginWithEmail(email, pass, listener);
    }

    public void logoutCleanUp(ResultListener listener) {
        //TODO: How does it work???
    }

    public void messageSearch(String rid, String text, ResultListener listener) {
        mMeteor.call("messageSearch", new Object[]{text, rid}, listener);
    }

    public void migrate(ResultListener listener) {
        //TODO: How does it work???
    }

    public void openRoom(String rid, OpenRoomListener listener) {
        mMeteor.call("openRoom", new Object[]{rid}, listener);
    }

    public void readMessages(String rid, ReadMessagesListener listener) {
        mMeteor.call("readMessages", new Object[]{rid}, listener);
    }

    public void registerUser(String name, String email, String pass, ResultListener listener) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("name", name);
        data.put("email", email);
        data.put("pass", pass);
        mMeteor.call("registerUser", new Object[]{data}, listener);
    }

    public void removeUserFromRoom(String rid, String username, ResultListener listener) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("rid", rid);
        data.put("username", username);
        mMeteor.call("removeUserFromRoom", new Object[]{data}, listener);
    }

    public void reportMessage(Message message, String description, ResultListener listener) {
        mMeteor.call("removeUserFromRoom", new Object[]{message, description}, listener);
    }

    public void resetAvatar(ResetAvatarListener listener) {
        mMeteor.call("resetAvatar", listener);
    }

    public void saveRoomName(String rid, String name, SaveRoomNameListener listener) {
        mMeteor.call("saveRoomName", new Object[]{rid, name}, listener);
    }

    public void saveUserPreferences(boolean disableNewRoomNotification,
                                    boolean disableNewMessageNotification,
                                    boolean useEmojis,
                                    boolean convertAsciiEmoji,
                                    boolean saveMobileBandwidth,
                                    boolean compactView,
                                    boolean unreadRoomsMode,
                                    boolean autoImageLoad,
                                    ResultListener listener) {

        Map<String, String> data = new HashMap<String, String>();

        data.put("disableNewRoomNotification", booleantoString(disableNewRoomNotification));
        data.put("disableNewMessageNotification", booleantoString(disableNewMessageNotification));
        data.put("useEmojis", booleantoString(useEmojis));
        data.put("convertAsciiEmoji", booleantoString(convertAsciiEmoji));
        data.put("saveMobileBandwidth", booleantoString(saveMobileBandwidth));
        data.put("compactView", booleantoString(compactView));
        data.put("unreadRoomsMode", booleantoString(unreadRoomsMode));
        data.put("autoImageLoad", booleantoString(autoImageLoad));

        mMeteor.call("saveUserPreferences", new Object[]{data}, listener);
    }

    private String booleantoString(boolean b) {
        return b ? "1" : "0";
    }

    public void setUsername(String username, ResultListener listener) {
        mMeteor.call("setUsername", new Object[]{username}, listener);
    }

    public void saveUserProfile(String language, String realname, String username, ResultListener listener) {
        Map<String, String> data = new HashMap<String, String>();

        data.put("language", language);
        data.put("realname", realname);
        data.put("username", username);

        mMeteor.call("saveUserProfile", new Object[]{data}, listener);
    }

    public void sendConfirmationEmail(String email, SendConfirmationEmailListener listener) {
        mMeteor.call("sendConfirmationEmail", new Object[]{email}, listener);
    }

    public void sendForgotPasswordEmail(String email, SendForgotPasswordEmailListener listener) {
        mMeteor.call("sendForgotPasswordEmail", new Object[]{email}, listener);
    }

    public void sendMessage(String rid, String msg, SendMessageListener listener) {
        Map<String, String> data = new HashMap<String, String>();

        data.put("rid", rid);
        data.put("msg", msg);

        mMeteor.call("sendMessage", new Object[]{data}, listener);
    }

    public void setAvatarFromService(ResultListener listener) {
        //TODO: How does it work???
    }

    public void setUserActiveStatus(String userId, String active, ResultListener listener) {
        //TODO: Check what are the allowed values to active parameter
        mMeteor.call("setUserActiveStatus", new Object[]{userId, active}, listener);
    }

    public void toogleFavorite(ResultListener listener) {
        //TODO: How does it work???
    }

    public void unarchiveRoom(String rid, ResultListener listener) {
        mMeteor.call("unarchiveRoom", new Object[]{rid}, listener);
    }

    public void updateMessage(String msgId, String rid, String msg, UpdateMessageListener listener) {
        Map<String, String> data = new HashMap<String, String>();

        data.put("_id", msgId);
        data.put("rid", rid);
        data.put("msg", msg);

        mMeteor.call("updateMessage", new Object[]{data}, listener);
    }

    public void updateUserUtcOffset(ResultListener listener) {
        //TODO: How does it work???
    }

    public void loginWithOAuth(String credentialToken, String credentialSecret, final ResultListener listener) {
        final Map<String, Object> userData = new HashMap<String, Object>();

        userData.put("credentialToken", credentialToken);
        userData.put("credentialSecret", credentialSecret);

        final Map<String, Object> authData = new HashMap<String, Object>();
        authData.put("oauth", userData);

        mMeteor.call("login", new Object[]{authData}, listener);

    }

    public void loginWithFacebook(String accessToken, long expiresIn, final LoginListener listener) {
        final Map<String, Object> userData = new HashMap<String, Object>();

        userData.put("accessToken", accessToken);
        userData.put("expiresIn", expiresIn);

        final Map<String, Object> authData = new HashMap<String, Object>();
        authData.put("authResponse", userData);
        authData.put("cordova", "true");

        mMeteor.call("login", new Object[]{authData}, listener);

    }

    public void setPushUser(ResultListener listener) {
        mMeteor.call("raix:push-setuser", new Object[]{mMeteor.getUserId()}, listener);
    }

    public void updatePush(String token, ResultListener listener) {
        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, String> tokenMap = new HashMap<String, String>();
        tokenMap.put("gcm", token);

        map.put("token", tokenMap);
        map.put("appName", "main");
        map.put("userId", mMeteor.getUserId());

        mMeteor.call("raix:push-update", new Object[]{map}, listener);
    }

    public void UserPresence(String status, String presence, ResultListener listener) {
        mMeteor.call("UserPresence:" + status, new Object[]{presence}, listener);
    }

    public void uploadFile(final String host, final String userId, final String rid,
                           final String name, final String[] strings,
                           final String type, final String extension,
                           final long size, final FileUploadListener listener) {
        final String store = "rocketchat_uploads";
        //TODO: What is this id?
        final String id = mMeteor.uniqueID().replace("-", "").substring(0, 17);

        final String media = type + "/" + extension;


        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", name);
        data.put("size", size);
        data.put("type", media);
        data.put("rid", rid);
        data.put("_id", id);
        data.put("complete", false);
        data.put("uploading", true);
        data.put("store", store);
        data.put("extension", extension);
        data.put("userId", userId);
        final float length = strings.length;
        final ResultListener ufsCompletelistener = new ResultListener() {
            @Override
            public void onSuccess(String result) {
                String filepath = "/ufs/" + store + "/" + id + "." + extension;
                String title = "*File Uploaded*: " + name + "\n" + host + filepath;
                Map<String, Object> data = new HashMap<String, Object>();

                data.put("rid", rid);
                data.put("msg", "");

                data.put("groupable", false);

                Map<String, String> file = new HashMap<String, String>();
                file.put("_id", id);
                data.put("file", file);


                Map<String, Object> attachments = new HashMap<String, Object>();

                attachments.put("title", title);
                attachments.put("title_link", filepath);
                attachments.put(type + "_url", filepath);
                attachments.put(type + "_type", media);
                attachments.put(type + "_size", size);

                data.put("attachments", new Object[]{attachments});

                mMeteor.call("sendMessage", new Object[]{data}, listener);

            }

            @Override
            public void onError(MeteorException e) {
                listener.onError(e);
            }
        };
        ResultListener ufsWriterListener = new ResultListener() {
            int index = 0;

            @Override
            public void onSuccess(String result) {
                if (index < length) {
                    listener.onProgress(index * 1.0f / length);
                    ufsWrite(strings[index], id, store, this);
                    index = index + 1;
                } else {
                    ufsComplete(id, store, ufsCompletelistener);
                }
            }

            @Override
            public void onError(MeteorException e) {
                listener.onError(e);
            }
        };

        mMeteor.insert(store, mMeteor.uniqueID().replace("-", "").substring(0, 17), data, ufsWriterListener);
    }

    public void ufsWrite(String binary, String id, String store, ResultListener listener) {
        Map<String, Object> binaryMap = new HashMap<String, Object>();
        binaryMap.put("$binary", binary);
        mMeteor.call("ufsWrite", new Object[]{binaryMap, id, store}, listener);
    }

    public void ufsComplete(String id, String store, ResultListener listener) {
        mMeteor.call("ufsComplete", new Object[]{id, store}, listener);
    }


    //TODO: understand these messages
    //{"_id":"MpFG6k5uSo2N3HmTo","token":{"gcm":"cnhWKWPokxc:APA91bF0Un80LebPUTH6Vw7I-HeZaofanrJyaopn5HA2kZF2eohkxOG-3jSlYHS-caJw_taKLH9AMfaX4O6HHdFohJrpZ35S8kyPpZVK6Z5EiUQiu4arf27jg4OpCZpCpmJM6bHagBfz"},"appName":"main","userId":"aFZWmPRqvpbZprmjj","enabled":true,"createdAt":{"$date":1450292512787},"updatedAt":{"$date":1450294151921}}
    //["{\"msg\":\"method\",\"method\":\"registerUser\",\"params\":[{\"name\":\"Júlio Cesar Bueno Cotta\",\"emailOrUsername\":\"\",\"email\":\"juliocbcotta+6@gmail.com\",\"pass\":\"google\",\"confirm-pass\":\"google\"}],\"id\":\"14\"}"]
    //["{\"msg\":\"method\",\"method\":\"joinDefaultChannels\",\"params\":[],\"id\":\"4\"}"]
    //["{\"msg\":\"method\",\"method\":\"UserPresence:setDefaultStatus\",\"params\":[\"online\"],\"id\":\"96\"}"]
    //["{\"msg\":\"method\",\"method\":\"UserPresence:away\",\"params\":[],\"id\":\"1\"}"]
    //["{\"msg\":\"method\",\"method\":\"login\",\"params\":[{\"user\":{\"username\":\"julio\"},\"password\":{\"digest\":\"bbdefa2950f49882f295b1285d4fa9dec45fc4144bfb07ee6acc68762d12c2e3\",\"algorithm\":\"sha-256\"}}],\"id\":\"3\"}"]

}
