package lk.mobilevisions.kiki.audio.model;

public class SampleDataItem {

    private final int id;
    private final String name;
    private final String price;
    private final int image;

    public SampleDataItem(int id, String name, String price, int image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getImage() {
        return image;
    }
}
