package com.androidplot.ui.layout;

/**
 * Encapsulates a sizing algorithm and an associated value.
 *
 * The available algorithms list are stored in the {@link SizeLayoutType} enumeration.
 *
 */
public class SizeMetric extends LayoutMetric<SizeLayoutType> {

    public SizeMetric(float value, SizeLayoutType layoutType) {
        super(value, layoutType);
    }

    protected void validatePair(float value, SizeLayoutType layoutType) {
        switch(layoutType) {
            case RELATIVE:
                if(value < 0 || value > 1) {
                    throw new IllegalArgumentException("SizeMetric Relative and Hybrid layout values must be within the range of 0 to 1.");
                }
            case ABSOLUTE:
            case FILL:
            default:
                break;
        }
    }

    @Override
    public float getPixelValue(float size) {
        //switch(layoutType)
        switch(getLayoutType()) {
            case ABSOLUTE:
                return getValue();
            case RELATIVE:
                return getValue() * size;
            case FILL:
                return size - getValue();
            default:
                throw new IllegalArgumentException("Unsupported LayoutType: " + this.getLayoutType());
        }
    }

}
