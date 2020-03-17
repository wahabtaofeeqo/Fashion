package com.example.fashion;

import android.net.Uri;

public class Style {
    private String name;
    private String desc;
    private String designer;
    private String style;
    private Uri uri;

    public Style() {
    }

    public String getName() {
        return name;
    }

    public Style(String name, String desc, String designer, String style) {
        this.name = name;
        this.desc = desc;
        this.designer = designer;
        this.style = style;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesigner() {
        return designer;
    }

    public void setDesigner(String designer) {
        this.designer = designer;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
    public Uri getUri() {
        return this.uri;
    }
}
