package com.frame.testlibrary.Bean;


import android.graphics.Path;


public class ProvinceItem {
    /**
     * Color_change=0;默认阴影GREEN
     * Color_change=1；默认点击BLUE
     * Color_change=2；默认设置故障RED
     * path svg路径
     * name 自定义的svg名称
     * fillColor 填充颜色
     * strokeColor 边框颜色
     */
    private Path path;
    private String name;
    private String fillColor;
    private String strokeColor;

    private int Color_change;

    public int getColor_change() {
        return Color_change;
    }

    public int setColor_change(int color_change) {
        Color_change = color_change;
        return color_change;
    }


    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public Path getPath()
    {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
