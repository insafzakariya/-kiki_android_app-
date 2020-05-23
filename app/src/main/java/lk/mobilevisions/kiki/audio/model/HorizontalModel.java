package lk.mobilevisions.kiki.audio.model;

public class HorizontalModel {
    public enum ItemType {
        ONE_ITEM, TWO_ITEM,THREE_ITEM;
    }
    private String name, description;
    private ItemType type;
    private int drawableIndex;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawableIndex() {
        return drawableIndex;
    }

    public void setDrawableIndex(int drawableIndex) {
        this.drawableIndex = drawableIndex;
    }

    public String getDescription() {
        return description;
    }
    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
