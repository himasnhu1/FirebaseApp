package com.example.firebaseapp.Model;

public class Users {

    private String id;
    private String username;
    private String imageURL;
    private String status;


    //Constructors;
    //Alt + Insert

    public Users(){
    }

    public Users(String id, String username, String imageURL,String status) {
        this.id = id;
        this.status = status;
        this.username = username;
        this.imageURL = imageURL;
    }
    // Getters and Setters

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
