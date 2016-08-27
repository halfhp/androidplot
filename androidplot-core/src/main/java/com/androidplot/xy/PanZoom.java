package com.androidplot.xy;

import android.graphics.*;
import android.view.*;

import java.util.*;

/**
 * Enables basic pan/zoom touch behavior for an {@link XYPlot}.
 * TODO: zoom using dynamic center point
 * TODO: stretch both mode
 */
public class PanZoom implements View.OnTouchListener {

    private static final float MIN_DIST_2_FING = 5f;

    private XYPlot plot;
    private Pan pan;
    private Zoom zoom;
    private boolean isEnabled = true;

    private DragState dragState = DragState.NONE;
    private float minXLimit = Float.MAX_VALUE;
    private float maxXLimit = Float.MAX_VALUE;
    private float minYLimit = Float.MAX_VALUE;
    private float maxYLimit = Float.MAX_VALUE;
    private float lastMinX = Float.MAX_VALUE;
    private float lastMaxX = Float.MAX_VALUE;
    private float lastMinY = Float.MAX_VALUE;
    private float lastMaxY = Float.MAX_VALUE;
    private PointF firstFingerPos;

    // rectangle created by the space between two fingers
    private RectF dist;
    private boolean mCalledBySelf;
    private View.OnTouchListener delegate;

    // Definition of the touch states
    protected enum DragState
    {
        NONE,
        ONE_FINGER,
        TWO_FINGERS
    }

    public enum Pan {
        HORIZONTAL,
        VERTICAL,
        BOTH
    }

    public enum Zoom {
        /**
         * Zoom on the horizontal axis only
         */
        STRETCH_HORIZONTAL,

        /**
         * Zoom on the vertical axis only
         */
        STRETCH_VERTICAL,

        /**
         * Zoom on the vertical axis by the vertical distance between each finger, while zooming
         * on the horizontal axis by the horizantal distance between each finger.
         */
        STRETCH_BOTH,

        /**
         * Zoom each axis by the same amount, specifically the total distance between each finger.
         */
        SCALE
    }

    protected PanZoom(XYPlot plot, Pan pan, Zoom zoom) {
        this.plot = plot;
        this.pan = pan;
        this.zoom = zoom;
    }

    /**
     * Convenience method for enabling pan/zoom behavior on an instance of {@link XYPlot}, using
     * a default behavior of {@link Pan#BOTH} and {@link Zoom#SCALE}.
     * Use {@link PanZoom#attach(XYPlot, Pan, Zoom)} for finer grain control of this behavior.
     * @param plot
     * @return
     */
    public static PanZoom attach(XYPlot plot) {
        return attach(plot, Pan.BOTH, Zoom.SCALE);
    }

    public static PanZoom attach(XYPlot plot, Pan pan, Zoom zoom) {
        PanZoom pz = new PanZoom(plot, pan, zoom);
        plot.setOnTouchListener(pz);
        return pz;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    protected void setDomainBoundaries(final Number lowerBoundary, final BoundaryMode lowerBoundaryMode,
            final Number upperBoundary, final BoundaryMode upperBoundaryMode) {
        plot.setDomainBoundaries(lowerBoundary, lowerBoundaryMode, upperBoundary, upperBoundaryMode);
        if(mCalledBySelf) {
            mCalledBySelf = false;
        } else {
            minXLimit = lowerBoundaryMode == BoundaryMode.FIXED ?
                        lowerBoundary.floatValue() : plot.getCalculatedMinX().floatValue();
            maxXLimit = upperBoundaryMode == BoundaryMode.FIXED ?
                        upperBoundary.floatValue() : plot.getCalculatedMaxX().floatValue();
            lastMinX = minXLimit;
            lastMaxX = maxXLimit;
        }
    }

    protected void setRangeBoundaries(final Number lowerBoundary, final BoundaryMode lowerBoundaryMode,
            final Number upperBoundary, final BoundaryMode upperBoundaryMode) {
        plot.setRangeBoundaries(lowerBoundary, lowerBoundaryMode, upperBoundary, upperBoundaryMode);
        if(mCalledBySelf) {
            mCalledBySelf = false;
        } else {
            minYLimit = lowerBoundaryMode == BoundaryMode.FIXED ?
                        lowerBoundary.floatValue() : plot.getCalculatedMinY().floatValue();
            maxYLimit = upperBoundaryMode == BoundaryMode.FIXED ?
                        upperBoundary.floatValue() : plot.getCalculatedMaxY().floatValue();
            lastMinY = minYLimit;
            lastMaxY = maxYLimit;
        }
    }

    protected void setDomainBoundaries(final Number lowerBoundary,
            final Number upperBoundary, final BoundaryMode mode) {
        plot.setDomainBoundaries(lowerBoundary, upperBoundary, mode);
        if(mCalledBySelf) {
            mCalledBySelf = false;
        } else {
            minXLimit = mode == BoundaryMode.FIXED ?
                        lowerBoundary.floatValue() : plot.getCalculatedMinX().floatValue();
            maxXLimit = mode == BoundaryMode.FIXED ?
                        upperBoundary.floatValue() : plot.getCalculatedMaxX().floatValue();
            lastMinX = minXLimit;
            lastMaxX = maxXLimit;
        }
    }

    protected synchronized void setRangeBoundaries(final Number lowerBoundary,
            final Number upperBoundary, final BoundaryMode mode) {
        plot.setRangeBoundaries(lowerBoundary, upperBoundary, mode);
        if(mCalledBySelf) {
            mCalledBySelf = false;
        } else {
            minYLimit = mode == BoundaryMode.FIXED ?
                        lowerBoundary.floatValue() : plot.getCalculatedMinY().floatValue();
            maxYLimit = mode == BoundaryMode.FIXED ?
                        upperBoundary.floatValue() : plot.getCalculatedMaxY().floatValue();
            lastMinY = minYLimit;
            lastMaxY = maxYLimit;
        }
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
        boolean isConsumed = false;
        if(delegate != null) {
            isConsumed = delegate.onTouch(view, event);
        }
        if(isEnabled() && !isConsumed) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: // start gesture
                    firstFingerPos = new PointF(event.getX(), event.getY());
                    dragState = DragState.ONE_FINGER;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN: // second finger
                {
                    dist = getDistance(event);
                    // the distance check is done to avoid false alarms
                    if (dist.width() > MIN_DIST_2_FING || dist.width() < -MIN_DIST_2_FING) {
                        dragState = DragState.TWO_FINGERS;
                    }
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP: // end zoom
                    dragState = DragState.NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (dragState == DragState.ONE_FINGER) {
                        pan(event);
                    } else if (dragState == DragState.TWO_FINGERS) {
                        zoom(event);
                    }
                    break;
            }
        }
        // we're forced to consume the event here as not consuming it will prevent future calls:
        return true;
    }

    /**
     * Calculates the distance between two finger motion events.
     * @param evt
     * @return
     */
    private RectF getDistance(final MotionEvent evt) {
        float left;
        float right;
        float top;
        float bottom;
        if(evt.getX(0) > evt.getX(1)) {
            left = evt.getX(1);
            right = evt.getX(0);
        } else {
            left = evt.getX(0);
            right = evt.getX(1);
        }

        if(evt.getY(0) > evt.getY(1)) {
            top = evt.getY(1);
            bottom = evt.getY(0);
        } else {
            top = evt.getY(0);
            bottom = evt.getY(1);
        }

        return new RectF(left, top, right, bottom);
    }

    private float getMinXLimit() {
        if(minXLimit == Float.MAX_VALUE) {
            minXLimit = plot.getCalculatedMinX().floatValue();
            lastMinX = minXLimit;
        }
        return minXLimit;
    }

    protected float getMaxXLimit() {
        if(maxXLimit == Float.MAX_VALUE) {
            maxXLimit = plot.getCalculatedMaxX().floatValue();
            lastMaxX = maxXLimit;
        }
        return maxXLimit;
    }

    protected float getMinYLimit() {
        if(minYLimit == Float.MAX_VALUE) {
            minYLimit = plot.getCalculatedMinY().floatValue();
            lastMinY = minYLimit;
        }
        return minYLimit;
    }

    protected float getMaxYLimit() {
        if(maxYLimit == Float.MAX_VALUE) {
            maxYLimit = plot.getCalculatedMaxY().floatValue();
            lastMaxY = maxYLimit;
        }
        return maxYLimit;
    }

    protected float getLastMinX() {
        if(lastMinX == Float.MAX_VALUE) {
            lastMinX = plot.getCalculatedMinX().floatValue();
        }
        return lastMinX;
    }

    protected float getLastMaxX() {
        if(lastMaxX == Float.MAX_VALUE) {
            lastMaxX = plot.getCalculatedMaxX().floatValue();
        }
        return lastMaxX;
    }

    protected float getLastMinY() {
        if(lastMinY == Float.MAX_VALUE) {
            lastMinY = plot.getCalculatedMinY().floatValue();
        }
        return lastMinY;
    }

    private float getLastMaxY() {
        if(lastMaxY == Float.MAX_VALUE) {
            lastMaxY = plot.getCalculatedMaxY().floatValue();
        }
        return lastMaxY;
    }

    protected void pan(final MotionEvent motionEvent) {
        final PointF oldFirstFinger = firstFingerPos; //save old position of finger
        firstFingerPos = new PointF(motionEvent.getX(), motionEvent.getY()); //update finger position
        PointF newX = new PointF();
        if(EnumSet.of(Pan.HORIZONTAL, Pan.BOTH).contains(pan)) {
            calculatePan(oldFirstFinger, newX, true);
            mCalledBySelf = true;
            setDomainBoundaries(newX.x, newX.y, BoundaryMode.FIXED);
            lastMinX = newX.x;
            lastMaxX = newX.y;
        }
        if(EnumSet.of(Pan.VERTICAL, Pan.BOTH).contains(pan)) {
            calculatePan(oldFirstFinger, newX, false);
            mCalledBySelf = true;
            setRangeBoundaries(newX.x, newX.y, BoundaryMode.FIXED);
            lastMinY = newX.x;
            lastMaxY = newX.y;
        }
        plot.redraw();
    }

    protected void calculatePan(final PointF oldFirstFinger, PointF newX, final boolean horizontal) {
        final float offset;
        // multiply the absolute finger movement for a factor.
        // the factor is dependent on the calculated min and max
        if(horizontal) {
            newX.x = getLastMinX();
            newX.y = getLastMaxX();
            offset = (oldFirstFinger.x - firstFingerPos.x) * ((newX.y - newX.x) / plot.getWidth());
        } else {
            newX.x = getLastMinY();
            newX.y = getLastMaxY();
            offset = -(oldFirstFinger.y - firstFingerPos.y) * ((newX.y - newX.x) / plot.getHeight());
        }
        // move the calculated offset
        newX.x = newX.x + offset;
        newX.y = newX.y + offset;

        //get the distance between max and min
        final float diff = newX.y - newX.x;

        //check if we reached the limit of panning
        if(horizontal) {
            if(newX.x < getMinXLimit()) {
                newX.x = getMinXLimit();
                newX.y = newX.x + diff;
            }
            if(newX.y > getMaxXLimit()) {
                newX.y = getMaxXLimit();
                newX.x = newX.y - diff;
            }
        } else {
            if(newX.x < getMinYLimit()) {
                newX.x = getMinYLimit();
                newX.y = newX.x + diff;
            }
            if(newX.y > getMaxYLimit()) {
                newX.y = getMaxYLimit();
                newX.x = newX.y - diff;
            }
        }
    }

    protected boolean isValidScale(float scale) {
        if(Float.isInfinite(scale) || Float.isNaN(scale) || scale > -0.001 && scale < 0.001) {
            return false;
        }
        return true;
    }

    protected void zoom(final MotionEvent motionEvent) {
        final RectF oldDist = dist;
        final RectF newDist = getDistance(motionEvent);
        dist = newDist;
        RectF newRect = new RectF();

        float scaleX = 1;
        float scaleY = 1;
        switch(zoom) {
            case STRETCH_HORIZONTAL:
                scaleX = oldDist.width()  / dist.width();
                if(!isValidScale(scaleX)) {
                    return;
                }
                break;
            case STRETCH_VERTICAL:
                scaleY = oldDist.height()  / dist.height();
                if(!isValidScale(scaleY)) {
                    return;
                }
                break;
            case STRETCH_BOTH:
                scaleX = oldDist.width()  / dist.width();
                scaleY = oldDist.height()  / dist.height();
                if(!isValidScale(scaleX) || !isValidScale(scaleY)) {
                    return;
                }
                break;
            case SCALE:
                scaleX = oldDist.width() / dist.width();
                scaleY = oldDist.height()  / dist.height();

                // use the greater value to scale each axis evenly:
                if(scaleX > scaleY) {
                    scaleY = scaleX;
                } else {
                    scaleX = scaleY;
                }
                if(!isValidScale(scaleX) || !isValidScale(scaleY)) {
                    return;
                }
                break;
        }

        if(EnumSet.of(
                Zoom.STRETCH_HORIZONTAL,
                Zoom.STRETCH_BOTH,
                Zoom.SCALE).contains(zoom)) {
            calculateZoom(newRect, scaleX, true);
            mCalledBySelf = true;
            setDomainBoundaries(newRect.left, newRect.right, BoundaryMode.FIXED);
            lastMinX = newRect.left;
            lastMaxX = newRect.right;
        }
        if(EnumSet.of(
                Zoom.STRETCH_VERTICAL,
                Zoom.STRETCH_BOTH,
                Zoom.SCALE).contains(zoom)) {
            calculateZoom(newRect, scaleY, false);
            mCalledBySelf = true;
            setRangeBoundaries(newRect.top, newRect.bottom, BoundaryMode.FIXED);
            lastMinY = newRect.top;
            lastMaxY = newRect.bottom;
        }
        plot.redraw();
    }

    protected void calculateZoom(RectF newRect, float scale, boolean isHorizontal) {

        final float calcMax;
        final float span;
        if(isHorizontal) {
            calcMax = getLastMaxX();
            span = calcMax - getLastMinX();
        } else {
            calcMax = getLastMaxY();
            span = calcMax - getLastMinY();
        }

        final float midPoint = calcMax - (span / 2.0f);
        final float offset = span * scale / 2.0f;

        if(isHorizontal) {
            newRect.left = midPoint - offset;
            newRect.right = midPoint + offset;
            if(newRect.left < getMinXLimit()) {
                newRect.left = getMinXLimit();
            }
            if(newRect.right > getMaxXLimit()) {
                newRect.right = getMaxXLimit();
            }
        } else {
            newRect.top = midPoint - offset;
            newRect.bottom = midPoint + offset;
            if(newRect.top < getMinYLimit()) {
                newRect.top = getMinYLimit();
            }
            if(newRect.bottom > getMaxYLimit()) {
                newRect.bottom = getMaxYLimit();
            }
        }
    }

    public Pan getPan() {
        return pan;
    }

    public void setPan(Pan pan) {
        this.pan = pan;
    }

    public Zoom getZoom() {
        return zoom;
    }

    public void setZoom(Zoom zoom) {
        this.zoom = zoom;
    }

    public View.OnTouchListener getDelegate() {
        return delegate;
    }

    /**
     * Set a delegate to receive onTouch calls before this class does.  If the delegate wishes
     * to consume the event, it should return true, otherwise it should return false.  Returning
     * false will not prevent future onTouch events from filtering through the delegate as it normally
     * would when attaching directly to an instance of {@link View}.
     * @param delegate
     */
    public void setDelegate(View.OnTouchListener delegate) {
        this.delegate = delegate;
    }
}
