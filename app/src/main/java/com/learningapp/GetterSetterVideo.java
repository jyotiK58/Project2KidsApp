package com.learningapp;

public class GetterSetterVideo {
    private String id;
    private String title;
    private String videoUrl;
    private String imageUrl;

    public GetterSetterVideo(String id, String title, String videoUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}