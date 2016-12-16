package com.androidplot.xy;

/**
 * Wraps an existing {@link XYSeries} allowing easy scaling of that series' xy values.
 */
public class ScalingXYSeries implements XYSeries {

    private double scale;
    private XYSeries series;
    private Mode mode;

    public enum Mode {
        X_ONLY,
        Y_ONLY,
        X_AND_Y
    }

    /**
     *
     * @param series The {@link XYSeries} to be scaled
     * @param scale The initial scale to be applied
     * @param mode Determines which axis (or both) to which scaling will be applied.
     */
    public ScalingXYSeries(XYSeries series, double scale, Mode mode) {
        this.series = series;
        this.scale = scale;
        this.mode = mode;
    }

    @Override
    public String getTitle() {
        return series.getTitle();
    }

    @Override
    public int size() {
        return series.size();
    }

    @Override
    public Number getX(int index) {
        Number x = series.getX(index);
        if(mode == Mode.X_ONLY || mode == Mode.X_AND_Y) {
            return x == null ? null : x.doubleValue() * scale;
        } else {
            return x;
        }
    }

    @Override
    public Number getY(int index) {
        Number y = series.getY(index);
        if(mode == Mode.Y_ONLY || mode == Mode.X_AND_Y) {
            return y == null ? null : y.doubleValue() * scale;
        } else {
            return y;
        }
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
