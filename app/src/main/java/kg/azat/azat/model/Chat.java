package kg.azat.azat.model;

import java.io.Serializable;

/**
 * Created by Nur on 14/08/16.
 */
public class Chat implements Serializable {
    String id, name, lastMessage, timestamp, post_url="";
    int unreadCount;

    public Chat() {
    }

    public Chat(String id, String name, String lastMessage, String timestamp, String _post_url, int unreadCount) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.post_url = _post_url;
        this.unreadCount = unreadCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPost_url() {
        return post_url;
    }

    public void setPost_url(String url) {
        this.post_url = url;
    }
}
