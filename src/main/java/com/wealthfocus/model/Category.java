package com.wealthfocus.model;

public class Category {
    private String id;
    private String name;
    private boolean isDefault;
    private String userId;

    public Category() {}
    public Category(String id, String name, boolean isDefault, String userId) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
