package com.androidplot.xy;

import java.util.*;

/**
 * Created by halfhp on 9/17/16.
 */
public class BubbleSeries implements XYSeries {

    private List<Number> xVals;
    private List<Number> yVals;
    private List<Number> zVals;
    private String title;

    /**
     *
     * @param interleavedValues Interleaved values ordered as x, y, z; total size must be a multiple of 3.
     */
    public BubbleSeries(Number... interleavedValues) {
        if(interleavedValues == null || interleavedValues.length % 3 > 0) {
            throw new RuntimeException("BubbleSeries interleave array length must be a non-zero multiple of 3.");
        }

        xVals = new ArrayList<>();
        yVals = new ArrayList<>();
        zVals = new ArrayList<>();
        for(int i = 0; i < interleavedValues.length; i+=3) {
            xVals.add(interleavedValues[i]);
            yVals.add(interleavedValues[i+1]);
            zVals.add(interleavedValues[i+2]);
        }
    }

    public BubbleSeries(List<Number> yVals, List<Number> zVals, String title) {
        this.yVals = yVals;
        this.zVals = zVals;
        this.title = title;
        // populate x with iVals:
        this.xVals = new ArrayList<>(zVals.size());
        for(int i = 0; i < zVals.size(); i++) {
            this.xVals.add(i);
        }
    }

    public BubbleSeries(List<Number> xVals, List<Number> yVals, List<Number> zVals, String title) {
        this.xVals = xVals;
        this.yVals = yVals;
        this.zVals = zVals;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int size() {
        return xVals.size();
    }

    @Override
    public Number getX(int index) {
        return xVals.get(index);
    }

    @Override
    public Number getY(int index) {
        return yVals.get(index);
    }

    public Number getZ(int index) {
        return zVals.get(index);
    }

    public List<Number> getZVals() {
        return zVals;
    }


}
