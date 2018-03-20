package kg.azat.azat.model;

import java.io.Serializable;

public class User implements Serializable
{
    private String id = "";
    private String api_key = "";
    private String userName = "";
    private String name = "";
    private String phone = "";
    private String email = "";
    private String avatarUrl = "";
    private boolean isActivated = false;

    public User() {
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String _client_key) {
        this.api_key = _client_key;
    }

    public String getUserName() { return userName;}

    public void setUserName(String _userName)
    {
        this.userName = _userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String _name)
    {
        this.name = _name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String _phone) {
        this.phone = _phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String _email) {
        this.email = _email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String _avatarUrl) {
        this.avatarUrl = _avatarUrl;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean _act)
    {
        this.isActivated = _act;
    }

}
