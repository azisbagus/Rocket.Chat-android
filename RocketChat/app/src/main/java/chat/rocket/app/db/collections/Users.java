package chat.rocket.app.db.collections;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import chat.rocket.app.db.dao.CollectionDAO;
import chat.rocket.app.utils.Util;

/**
 * Created by julio on 24/11/15.
 */
public class Users {

    public static final String COLLECTION_NAME = "users";

    private List<Email> emails;
    private String username;
    private Roles roles;
    private String avatarOrigin;
    private String name;
    private Services services;
    //TODO: change 'status' to enum
    private String status;
    //TODO: change 'statusConnection' to enum
    private String statusConnection;
    private int utcOffset;

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public String getAvatarOrigin() {
        return avatarOrigin;
    }

    public void setAvatarOrigin(String avatarOrigin) {
        this.avatarOrigin = avatarOrigin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusConnection() {
        return statusConnection;
    }

    public void setStatusConnection(String statusConnection) {
        this.statusConnection = statusConnection;
    }

    public int getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(int utcOffset) {
        this.utcOffset = utcOffset;
    }

    public static class Email {
        private String address;
        private boolean verified;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }
    }

    public static class Roles {
        @SerializedName("__global_roles__")
        private List<String> globalRoles;
    }

    public static class Services {
        //TODO: understand what can come inside here
    }

    public static Users getUser(String userId) {
        CollectionDAO dao = CollectionDAO.query(COLLECTION_NAME, userId);
        return Util.GSON.fromJson(dao.getNewValuesJson(), Users.class);
    }
}
