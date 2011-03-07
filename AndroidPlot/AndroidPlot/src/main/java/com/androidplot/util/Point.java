package com.androidplot.util;

@Deprecated
public class Point {
    private float x;
    private float y;

    public Point() {
        this(0, 0);
    }

    public Point(float x, float y) {
        set(x, y);
    }

    public Point(Point rhs) {
        set(rhs);
    }

    public void add(Point rhs) {
        //Point result = new Point();
        setX(getX() + rhs.getX());
        setY(getY() + rhs.getY());
    }

    public void sub(Point rhs) {
        //Point result = new Point();
        setX(getX() - rhs.getX());
        setY(getY() - rhs.getY());
        //return result;
    }

    public void set(Point rhs) {
        set(rhs.getX(), rhs.getY());
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
