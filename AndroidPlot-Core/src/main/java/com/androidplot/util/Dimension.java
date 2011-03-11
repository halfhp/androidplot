package com.androidplot.util;

@Deprecated // use RectF instead.
public class Dimension {
    private float width;
    private float height;

    public Dimension(float width, float height) {
        set(width, height);
    }

    public Dimension(Dimension dimension) {
        set(dimension);
    }

    public void set(Dimension dimension) {
        set(dimension.getWidth(), dimension.getHeight());
    }

    public void set(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
