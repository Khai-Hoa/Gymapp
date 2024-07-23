package com.example.gym_manager.model;

public class User {
    private String userId;
    private String email;
    private String password;
    private String url_avatar;
    private String name_gym;

    public User() {
    }



    public User(String email, String password) {

        this.email = email;
        this.password = password;
    }

    public String getUrl_avatar() {
        return url_avatar;
    }

    public void setUrl_avatar(String url_avatar) {
        this.url_avatar = url_avatar;
    }

    public String getName_gym() {
        return name_gym;
    }

    public void setName_gym(String name_gym) {
        this.name_gym = name_gym;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
