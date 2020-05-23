package lk.mobilevisions.kiki.audio.model;

import java.util.ArrayList;

public class VerticalModel {
    public enum ItemType {
        ONE_ITEM, TWO_ITEM,THREE_ITEM;
    }
    private String title;
    private ItemType type;
    private ArrayList<HorizontalModel> arrayList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
    public ArrayList<HorizontalModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<HorizontalModel> arrayList) {
        this.arrayList = arrayList;
    }
}
