package com.example.preeckle.ui.register;

public class User {
    String username, nameFull, bio, gender, birthdayDate, profileImage;

    public User(){}

    public User(String username, String nameFull, String bio, String gender, String birthdayDate, String profileImage) {
        this.username = username;
        this.nameFull = nameFull;
        this.bio = bio;
        this.gender = gender;
        this.birthdayDate = birthdayDate;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNameFull() {
        return nameFull;
    }

    public void setNameFull(String nameFull) {
        this.nameFull = nameFull;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(String birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
