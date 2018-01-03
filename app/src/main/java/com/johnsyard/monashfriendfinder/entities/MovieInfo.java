package com.johnsyard.monashfriendfinder.entities;

import android.graphics.Bitmap;

/**
 * This class is used to carry movie info
 */
public class MovieInfo{
    private String description;

    private Bitmap image;

    public MovieInfo(){
        this.image = null;
        this.description = "";
    }
    public MovieInfo(String description, Bitmap bitmap){
        this.image = bitmap;
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}