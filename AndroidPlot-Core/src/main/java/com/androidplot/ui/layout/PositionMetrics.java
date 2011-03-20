package com.androidplot.ui.layout;

public class PositionMetrics implements Comparable<PositionMetrics> {

    private XMetric xMetric;
    private YMetric yMetric;
    private AnchorPosition anchor;
    private float layerDepth;

    /*
    public PositionMetrics(float lastColumn, XLayoutStyle xLayoutType, float lastRow, YLayoutStyle yLayoutType) {
        this(lastColumn, xLayoutType, lastRow, yLayoutType, AnchorPosition.LEFT_TOP);
    }
    */

    public PositionMetrics(float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle, AnchorPosition anchor) {
        setxMetric(new XMetric(x, xLayoutStyle));
        setyMetric(new YMetric(y, yLayoutStyle));
        setAnchor(anchor);

    }


    public XMetric getxMetric() {
        return xMetric;
    }

    public void setxMetric(XMetric xMetric) {
        this.xMetric = xMetric;
    }

    public YMetric getyMetric() {
        return yMetric;
    }

    public void setyMetric(YMetric yMetric) {
        this.yMetric = yMetric;
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
