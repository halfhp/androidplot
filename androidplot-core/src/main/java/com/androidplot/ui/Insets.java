// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

/**
 * A set of insets for a rect space.
 */
public class Insets {

    private float top;
    private float bottom;
    private float left;
    private float right;

    public Insets() {}

    public Insets(float top, float bottom, float left, float right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }
}
