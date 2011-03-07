package com.androidplot.xy;

public class XYStep {

    private double stepCount;
    private float stepPix;
    private double stepVal;

    public XYStep() {}

    public XYStep(double stepCount, float stepPix, double stepVal) {
        this.stepCount = stepCount;
        this.stepPix = stepPix;
        this.stepVal = stepVal;
    }

    public double getStepCount() {
        return stepCount;
    }

    public void setStepCount(double stepCount) {
        this.stepCount = stepCount;
    }

    public float getStepPix() {
        return stepPix;
    }

    public void setStepPix(float stepPix) {
        this.stepPix = stepPix;
    }

    public double getStepVal() {
        return stepVal;
    }

    public void setStepVal(double stepVal) {
        this.stepVal = stepVal;
    }
}
