package com.androidplot.ui.layout;

public class SizeMetrics {
    private SizeMetric heightMetric;
    private SizeMetric widthMetric;

    public SizeMetrics(float height, SizeLayoutType heightLayoutType, float width, SizeLayoutType widthLayoutType) {
        heightMetric = new SizeMetric(height, heightLayoutType);
        widthMetric = new SizeMetric(width, widthLayoutType);
    }

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
