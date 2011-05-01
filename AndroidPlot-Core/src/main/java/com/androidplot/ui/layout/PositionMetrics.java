package com.androidplot.ui.layout;

public class PositionMetrics implements Comparable<PositionMetrics> {

    private XPositionMetric xPositionMetric;
    private YPositionMetric yPositionMetric;
    private AnchorPosition anchor;
    private float layerDepth;

    public PositionMetrics(float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle, AnchorPosition anchor) {
        setxPositionMetric(new XPositionMetric(x, xLayoutStyle));
        setyPositionMetric(new YPositionMetric(y, yLayoutStyle));
        setAnchor(anchor);

    }


    public XPositionMetric getxPositionMetric() {
        return xPositionMetric;
    }

    public void setxPositionMetric(XPositionMetric xPositionMetric) {
        this.xPositionMetric = xPositionMetric;
    }

    public YPositionMetric getyPositionMetric() {
        return yPositionMetric;
    }

    public void setyPositionMetric(YPositionMetric yPositionMetric) {
        this.yPositionMetric = yPositionMetric;
    }

    public AnchorPosition getAnchor() {
        return anchor;
    }

    public void setAnchor(AnchorPosition anchor) {
        this.anchor = anchor;
    }

    @Override
    public int compareTo(PositionMetrics o) {
        if(this.layerDepth < o.layerDepth) {
            return -1;
        } else if(this.layerDepth == o.layerDepth) {
            return 0;
        } else {
            return 1;
        }
    }
}
