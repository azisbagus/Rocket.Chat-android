package chat.rocket.rc.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import chat.rocket.rc.enumerations.MessageType;

/**
 * Created by julio on 19/11/15.
 */
public class Message {

    @SerializedName("_id")
    protected String documentID;
    protected String msg;
    protected String rid;
    protected TimeStamp ts;
    @SerializedName("t")
    protected MessageType type;
    @SerializedName("u")
    protected UsernameId usernameId;
    protected FileId file;
    protected List<UrlParts> urls;
    protected TimeStamp editedAt;
    protected UsernameId editedBy;
    protected double score;

    public Message() {
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public Message(String documentID, String msg, String rid, TimeStamp ts, MessageType type, UsernameId usernameId, FileId file, List<UrlParts> urls, TimeStamp editedAt, UsernameId editedBy, double score) {
        this.documentID = documentID;
        this.msg = msg;
        this.rid = rid;
        this.ts = ts;
        this.type = type;
        this.usernameId = usernameId;
        this.file = file;
        this.urls = urls;
        this.editedAt = editedAt;
        this.editedBy = editedBy;
        this.score = score;
    }

    public String toString() {
        return "documentID:" + documentID + ", msg:" + msg;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public FileId getFile() {
        return file;
    }

    public void setFile(FileId file) {
        this.file = file;
    }

    public TimeStamp getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(TimeStamp editedAt) {
        this.editedAt = editedAt;
    }

    public UsernameId getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(UsernameId editedBy) {
        this.editedBy = editedBy;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public TimeStamp getTs() {
        return ts;
    }

    public void setTs(TimeStamp ts) {
        this.ts = ts;
    }

    public UsernameId getUsernameId() {
        return usernameId;
    }

    public void setUsernameId(UsernameId usernameId) {
        this.usernameId = usernameId;
    }

    public List<UrlParts> getUrls() {
        return urls;
    }

    public void setUrls(List<UrlParts> urls) {
        this.urls = urls;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
