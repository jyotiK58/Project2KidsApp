package com.learningapp;

public class GetterSetterVideo {
    private String id;
    private String title;
    private String videoUrl;
    private String imageUrl;

    // Default constructor
    public GetterSetterVideo() {
    }

    // Constructor with parameters
    public GetterSetterVideo(String id, String title, String videoUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
    }

    // Getter for ID
    public String getId() {
        return id;
    }

    // Setter for ID
    public void setId(String id) {
        this.id = id;
    }

    // Getter for Title
    public String getTitle() {
        return title;
    }

    // Setter for Title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for Video URL
    public String getVideoUrl() {
        return videoUrl;
    }

    // Setter for Video URL
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    // Getter for Image URL
    public String getImageUrl() {
        return imageUrl;
    }

    // Setter for Image URL
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Override toString() method for debugging
    @Override
    public String toString() {
        return "GetterSetterVideo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
