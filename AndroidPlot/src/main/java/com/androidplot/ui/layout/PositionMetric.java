package com.androidplot.ui.layout;

public abstract class PositionMetric<LayoutType extends Enum> extends LayoutMetric<LayoutType> {

    protected enum Origin {
        FROM_BEGINING,
        FROM_CENTER,
        FROM_END
    }

    protected enum LayoutMode {
        ABSOLUTE,
        RELATIVE
    }

    public PositionMetric(float value, LayoutType layoutType) {
        super(value, layoutType);
    }

    /**
     * Throws IllegalArgumentException if there is a problem.
     * @param value
     * @param layoutMode
     * @throws IllegalArgumentException
     */
    protected static void validateValue(float value, LayoutMode layoutMode) throws IllegalArgumentException {
        switch(layoutMode) {
            case ABSOLUTE:
                break;
            case RELATIVE:
                if(value < -1 || value > 1) {
                    throw new IllegalArgumentException("Relative layout values must be within the range of -1 to 1.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown LayoutMode: " + layoutMode);
        }

    }

    protected float getAbsolutePosition(float size, Origin origin) {
        switch(origin) {
            case FROM_BEGINING:
                return getValue();
            case FROM_CENTER:
                return (size/2f) + getValue();
            case FROM_END:
                return size - getValue();
            default:
                 throw new IllegalArgumentException("Unsupported Origin: " + origin);
        }
    }

    protected float getRelativePosition(float size, Origin origin) {
        //throw new UnsupportedOperationException("Not yet implemented.");

        switch(origin) {
            case FROM_BEGINING:
                return size * getValue();
            case FROM_CENTER:
                return (size/2f) + ((size/2f) * getValue());
            case FROM_END:
                return size + (size*getValue());
            default:
                 throw new IllegalArgumentException("Unsupported Origin: " + origin);
        }

    }


}
