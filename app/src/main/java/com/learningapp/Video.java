public class Video {
    private String id;
    private String title;
    private String videoUrl;
    private String imageUrl;  // Add this field

    // Constructor
    public Video(String id, String title, String videoUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;  // Initialize the imageUrl
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImageUrl() {  // Add this getter
        return imageUrl;
    }
}
