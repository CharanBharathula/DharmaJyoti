package com.example.dharmajyoti.Model;

public class Post
{
    private String postid,description,publisher,publisherid;

    public Post(){}

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }

    public Post(String postid, String description, String publisher, String publisherid) {
        this.postid = postid;
        this.description = description;
        this.publisher = publisher;
        this.publisherid=publisherid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
