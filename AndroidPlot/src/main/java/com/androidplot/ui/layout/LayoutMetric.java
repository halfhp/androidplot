package com.androidplot.ui.layout;



abstract class LayoutMetric<LayoutType extends Enum> {

    private LayoutType layoutType;
    /*
    public enum LayoutType {
        ABSOLUTE,
        RELATIVE
    }
    */

    //private LayoutType layoutType;
    private float value;
    //private float y;

    public LayoutMetric(float value, LayoutType layoutType) {
        validatePair(value, layoutType);
        set(value, layoutType);
        //setLayoutType(layoutType);
        //setValue(value);
        //setLayoutType(layoutType);
    }

    protected abstract void validatePair(float value, LayoutType layoutType);

    public void set(float value, LayoutType layoutType) {
        validatePair(value, layoutType);
        this.value = value;
        this.layoutType = layoutType;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        validatePair(value, layoutType);
        this.value = value;
    }

    public abstract float getPixelValue(float size);

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        validatePair(value, layoutType);
        this.layoutType = layoutType;
    }
}
