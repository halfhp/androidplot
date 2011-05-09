package com.androidplot.xy;


import com.androidplot.ui.PositionMetric;

public class XPositionMetric extends PositionMetric<XLayoutStyle> {

    //private XLayoutStyle layoutType;

    public XPositionMetric(float value, XLayoutStyle layoutStyle) {
        super(value, layoutStyle);
        validatePair(value, layoutStyle);
        //this.layoutStyle = layoutStyle;
    }

    /**
     * Throws IllegalArgumentException if there is a problem.
     * @param value
     */
    protected void validatePair(float value, XLayoutStyle layoutStyle) {
        switch(layoutStyle) {
            case ABSOLUTE_FROM_LEFT:
            case ABSOLUTE_FROM_RIGHT:
            case ABSOLUTE_FROM_CENTER:
                validateValue(value, PositionMetric.LayoutMode.ABSOLUTE);
                break;
            case RELATIVE_TO_LEFT:
            case RELATIVE_TO_RIGHT:
            case RELATIVE_TO_CENTER:
                validateValue(value, PositionMetric.LayoutMode.RELATIVE);
        }
    }

    @Override
    public float getPixelValue(float size) {
        switch(getLayoutType()) {
            case ABSOLUTE_FROM_LEFT:
                return this.getAbsolutePosition(size, PositionMetric.Origin.FROM_BEGINING);
            case ABSOLUTE_FROM_RIGHT:
                return this.getAbsolutePosition(size, PositionMetric.Origin.FROM_END);
            case ABSOLUTE_FROM_CENTER:
                return this.getAbsolutePosition(size, PositionMetric.Origin.FROM_CENTER);
            case RELATIVE_TO_LEFT:
                return this.getRelativePosition(size, PositionMetric.Origin.FROM_BEGINING);
            case RELATIVE_TO_RIGHT:
                return this.getRelativePosition(size, PositionMetric.Origin.FROM_END);
            case RELATIVE_TO_CENTER:
                return this.getRelativePosition(size, PositionMetric.Origin.FROM_CENTER);
            default:
                throw new IllegalArgumentException("Unsupported LayoutType: " + this.getLayoutType());
        }
    }
}
