package com.example.dharmajyoti.Model;

public class Customer
{
    String userid;
    String username;
    String image_url;
    String email;
    String password;
    String mobile;

    public Customer(){}
    public Customer(String userid, String username, String image_url, String email, String password, String mobile) {
        this.userid = userid;
        this.username = username;
        this.image_url = image_url;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
