package com.example.dharmajyoti.Model;

public class User
{
    String id;
    String username;
    String imageurl;
    String email;
    String password;
    String mobile;
    public User(String id, String username, String imageurl, String email, String password, String mobile) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.email = email;
        this.password = password;
        this.mobile=mobile;
    }

    public String getmobile() {
        return mobile;
    }

    public void setmobile(String mobile) {
        this.mobile = mobile;
    }

    public User()
    {

    }
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
