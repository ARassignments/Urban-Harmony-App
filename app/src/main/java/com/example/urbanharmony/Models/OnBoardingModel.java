package com.example.urbanharmony.Models;

public class OnBoardingModel {
    private int image;

    public int getDarkImage() {
        return darkImage;
    }

    public void setDarkImage(int darkImage) {
        this.darkImage = darkImage;
    }

    private int darkImage;
    private String title;
    private String description;

    public int getImage(){
        return image;
    }

    public void setImage(int image){
        this.image = image;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
