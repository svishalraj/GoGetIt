package com.gogetit.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishalsanganaboina on 2/25/17.
 */
@IgnoreExtraProperties
public class Item {
    private String id;
    private String itemName;
    private boolean selected;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        Item that = (Item) obj;
        return this.getItemName().equals(that.getItemName());
    }

    @Override
    public int hashCode() {
        return this.getItemName().hashCode();
    }

    @Override
    public String toString() {
        return this.getItemName()+ " - "+this.getDescription();
    }

    public Item(String id, String itemName, boolean selected, String description) {
        this.id = id;
        this.itemName = itemName;
        this.selected = selected;
        this.description = description;
    }

    public Item() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", getId());
        result.put("itemName", getItemName());
        result.put("description", getDescription());
        result.put("selected", String.valueOf(isSelected()));
        return result;
    }
}
