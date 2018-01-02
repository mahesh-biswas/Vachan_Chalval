package com.goodinitiative.vachan_chalval;

/**
 * Created by mahesh on 11/9/2017.
 */

public class layoutView {
    int image;
    String imageUri;
    String name;
    private String charge;
    private String author,yop;
    public layoutView(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public layoutView(String imageUri, String name) {
        this.imageUri = imageUri;
        this.name = name;
    }

    public layoutView(String imageUri, String name, String charge, String author, String yop) {
        this.imageUri = imageUri;
        this.name = name;
        this.charge = charge;
        this.author = author;
        this.yop = yop;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getName() {
        return name;
    }

    public String getCharge() {
        return charge;
    }

    public String getAuthor() {
        return author;
    }

    public String getYop() {
        return yop;
    }
}
