package com.example.firebaseimage;

public class DB_Model {
    public String imageName;
    public String imageUrl;
    public DB_Model(){

    }

    // Constructor Class...
    public DB_Model(String imageName, String imageUrl) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    // Getter and Setter Class...
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
