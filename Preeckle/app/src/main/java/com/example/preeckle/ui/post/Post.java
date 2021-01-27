package com.example.preeckle.ui.post;

public class Post {
    String postDate, postImage, postDescription, postTime;

    public Post() {}

    public Post(String postDate, String postImage, String postDescription, String postTime) {
        this.postDate = postDate;
        this.postImage = postImage;
        this.postDescription = postDescription;
        this.postTime = postTime;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
}