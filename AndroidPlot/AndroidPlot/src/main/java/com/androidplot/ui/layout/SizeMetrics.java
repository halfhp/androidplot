package com.androidplot.ui.layout;

/**
 * Encapsulates sizing preferences associated with a Widget; how/if it scales etc.
 */
public class SizeMetrics {
    private SizeMetric heightMetric;
    private SizeMetric widthMetric;

    /**
     * Convenience constructor.  Wraps {@link #SizeMetrics(SizeMetric, SizeMetric)}.
     * @param height Height value used algorithm to calculate the height of the associated widget(s).
     * @param heightLayoutType Algorithm used to calculate the height of the associated widget(s).
     * @param width Width value used algorithm to calculate the width of the associated widget(s).
     * @param widthLayoutType Algorithm used to calculate the width of the associated widget(s).
     */
    public SizeMetrics(float height, SizeLayoutType heightLayoutType, float width, SizeLayoutType widthLayoutType) {
        heightMetric = new SizeMetric(height, heightLayoutType);
        widthMetric = new SizeMetric(width, widthLayoutType);
    }

    /**
     * Creates a new SizeMetrics instance using the specified size layout algorithm and value.
     * See {@link SizeMetric} for details on what can be passed in.
     * @param heightMetric
     * @param widthMetric
     */
    public SizeMetrics(SizeMetric heightMetric, SizeMetric widthMetric) {
        this.heightMetric = heightMetric;
        this.widthMetric = widthMetric;
    }

    public SizeMetric getHeightMetric() {
        return heightMetric;
    }

    public void setHeightMetric(SizeMetric heightMetric) {
        this.heightMetric = heightMetric;
    }

    public SizeMetric getWidthMetric() {
        return widthMetric;
    }

    public void setWidthMetric(SizeMetric widthMetric) {
        this.widthMetric = widthMetric;
    }
}
