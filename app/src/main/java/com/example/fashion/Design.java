package com.example.fashion;

public class Design {
    private String name;
    private String desc;
    private String designer;
    private String style;

    public Design() {
    }

    public String getName() {
        return name;
    }

    public Design(String name, String desc, String designer, String style) {
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
}
