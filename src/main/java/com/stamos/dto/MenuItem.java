package com.stamos.dto;

public class MenuItem {
    private String label;
    private String url;
    private String icon;
    private boolean active;

    public MenuItem() {}

    public MenuItem(String label, String url, String icon) {
        this.label = label;
        this.url = url;
        this.icon = icon;
        this.active = false;
    }

    public MenuItem(String label, String url, String icon, boolean active) {
        this.label = label;
        this.url = url;
        this.icon = icon;
        this.active = active;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}