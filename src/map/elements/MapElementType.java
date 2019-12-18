package map.elements;

public enum MapElementType {
    ANIMAL("/zwierzak.png"),
    ANIMALS("/zwierzaki.png"),
    GRASS("/grass2.png");

    private String url;

    MapElementType(String envUrl) {
        this.url = envUrl;
    }

    public String getUrl() {
        return url;
    }
}
