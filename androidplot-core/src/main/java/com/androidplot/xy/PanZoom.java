package com.androidplot.xy;

import android.graphics.RectF;
import android.graphics.PointF;
import android.util.*;
import android.view.*;

import com.androidplot.*;

import java.util.*;

/**
 * Enables basic pan/zoom touch behavior for an {@link XYPlot}.
 * By default boundaries set on the associated plot will define the scroll/zoom extents as well as
 * initial state of the plot's visible area.  If you wish to specify a scrollable / zoomable area
 * that is greater than or less than the plot's boundaries, use
 * {@link #setDomainBoundaries(Number, Number)} and
 * {@link #setRangeBoundaries(Number, Number)}
 * TODO: zoom using dynamic center point
 * TODO: stretch both mode
 */
public class PanZoom implements View.OnTouchListener {

    protected static final float MIN_DIST_2_FING = 5f;
    protected static final int FIRST_FINGER = 0;
    protected static final int SECOND_FINGER = 1;

    private XYPlot plot;
    private Pan pan;
    private Zoom zoom;
    private boolean isEnabled = true;

    private DragState dragState = DragState.NONE;
    RectRegion limits = new RectRegion();
    RectRegion previousLimits = new RectRegion();
    private PointF firstFingerPos;

    // rectangle created by the space between two fingers
    private RectF fingersRect;
    private View.OnTouchListener delegate;

    // Definition of the touch states
    protected enum DragState {
        NONE,
        ONE_FINGER,
        TWO_FINGERS
    }

    public enum Pan {
        NONE,
        HORIZONTAL,
        VERTICAL,
        BOTH
    }

    public enum Zoom {

        /**
         * Comletely disable panning
         */
        NONE,

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

    /**
     * Set the boundaries by which domain pan/zoom calculations will abide; differs from an {@link XYPlot}'s boundaries
     * in that those boundaries define the plot's starting state.
     * @param lowerBoundary
     * @param upperBoundary
     */
    public void setDomainBoundaries(final Number lowerBoundary, final Number upperBoundary) {
        limits.setMinX(lowerBoundary);
        limits.setMaxX(upperBoundary);
    }

    /**
     * Sets the range boundaries by which pan/zoom calculations will abide.
     * @param lowerBoundary
     * @param upperBoundary
     */
    public void setRangeBoundaries(final Number lowerBoundary, final Number upperBoundary) {
        limits.setMinY(lowerBoundary);
        limits.setMaxY(upperBoundary);
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
        boolean isConsumed = false;
        if (delegate != null) {
            isConsumed = delegate.onTouch(view, event);
        }
        if (isEnabled() && !isConsumed) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: // start gesture
                    firstFingerPos = new PointF(event.getX(), event.getY());
                    dragState = DragState.ONE_FINGER;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN: // second finger
                {
                    fingersRect = fingerDistance(event);
                    // the distance check is done to avoid false alarms
                    if (fingersRect.width() > MIN_DIST_2_FING || fingersRect.width() < -MIN_DIST_2_FING) {
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

                case MotionEvent.ACTION_UP:
                    reset();
                    break;
            }
        }
        // we're forced to consume the event here as not consuming it will prevent future calls:
        return true;
    }

    /**
     * Calculates the distance between two finger motion events.
     * @param firstFingerX
     * @param firstFingerY
     * @param secondFingerX
     * @param secondFingerY
     * @return
     */
    protected RectF fingerDistance(float firstFingerX, float firstFingerY, float secondFingerX, float secondFingerY) {
        final float left = firstFingerX > secondFingerX ? secondFingerX : firstFingerX;
        final float right = firstFingerX > secondFingerX ? firstFingerX : secondFingerX;
        final float top = firstFingerY > secondFingerY ? secondFingerY : firstFingerY;
        final float bottom = firstFingerY > secondFingerY ? firstFingerY : secondFingerY;
        return new RectF(left, top, right, bottom);
    }

    /**
     * Calculates the distance between two finger motion events.
     * @param evt
     * @return
     */
    protected RectF fingerDistance(final MotionEvent evt) {
        return fingerDistance(
                evt.getX(FIRST_FINGER),
                evt.getY(FIRST_FINGER),
                evt.getX(SECOND_FINGER),
                evt.getY(SECOND_FINGER));
    }

    protected Number getMinXLimit() {
        if (limits.getMinX() == null) {
            limits.setMinX(plot.getBounds().getMinX().floatValue());
            previousLimits.setMinX(limits.getMinX());
        }
        return limits.getMinX();
    }

    protected Number getMaxXLimit() {
        if (limits.getMaxX() == null) {
            limits.setMaxX(plot.getBounds().getMaxX().floatValue());
            previousLimits.setMaxX(limits.getMaxX());
        }
        return limits.getMaxX();
    }

    protected Number getMinYLimit() {
        if (limits.getMinY() == null) {
            limits.setMinY(plot.getBounds().getMinY().floatValue());
            previousLimits.setMinY(limits.getMinY());
        }
        return limits.getMinY();
    }

    protected Number getMaxYLimit() {
        if (limits.getMaxY() == null) {
            limits.setMaxY(plot.getBounds().getMaxY().floatValue());
            previousLimits.setMaxY(limits.getMaxY());
        }
        return limits.getMaxY();
    }

    protected Number getLastMinX() {
        if (previousLimits.getMinX() == null) {
            previousLimits.setMinX(plot.getBounds().getMinX().floatValue());
        }
        return previousLimits.getMinX();
    }

    protected Number getLastMaxX() {
        if (previousLimits.getMaxX() == null) {
            previousLimits.setMaxX(plot.getBounds().getMaxX().floatValue());
        }
        return previousLimits.getMaxX();
    }

    protected Number getLastMinY() {
        if (previousLimits.getMinY() == null) {
            previousLimits.setMinY(plot.getBounds().getMinY().floatValue());
        }
        return previousLimits.getMinY();
    }

    private Number getLastMaxY() {
        if (previousLimits.getMaxY() == null) {
            previousLimits.setMaxY(plot.getBounds().getMaxY().floatValue());
        }
        return previousLimits.getMaxY();
    }

    protected void pan(final MotionEvent motionEvent) {
        if (pan == Pan.NONE) {
            return;
        }

        final PointF oldFirstFinger = firstFingerPos; //save old position of finger
        firstFingerPos = new PointF(motionEvent.getX(), motionEvent.getY()); //update finger position
        Region newBounds = new Region();
        if (EnumSet.of(Pan.HORIZONTAL, Pan.BOTH).contains(pan)) {
            calculatePan(oldFirstFinger, newBounds, true);
            plot.setDomainBoundaries(newBounds.getMin(), newBounds.getMax(), BoundaryMode.FIXED);
            previousLimits.setMinX(newBounds.getMin());
            previousLimits.setMaxX(newBounds.getMax());
        }
        if (EnumSet.of(Pan.VERTICAL, Pan.BOTH).contains(pan)) {
            calculatePan(oldFirstFinger, newBounds, false);
            plot.setRangeBoundaries(newBounds.getMin(), newBounds.getMax(), BoundaryMode.FIXED);
            previousLimits.setMinY(newBounds.getMin());
            previousLimits.setMaxY(newBounds.getMax());
        }
        plot.redraw();
    }

    protected void calculatePan(final PointF oldFirstFinger, Region bounds, final boolean horizontal) {
        final float offset;
        // multiply the absolute finger movement for a factor.
        // the factor is dependent on the calculated min and max
        if (horizontal) {
            bounds.setMin(getLastMinX());
            bounds.setMax(getLastMaxX());
            offset = (oldFirstFinger.x - firstFingerPos.x) *
                    ((bounds.getMax().floatValue() - bounds.getMin().floatValue()) / plot.getWidth());
        } else {
            bounds.setMin(getLastMinY());
            bounds.setMax(getLastMaxY());
            offset = -(oldFirstFinger.y - firstFingerPos.y) *
                    ((bounds.getMax().floatValue() - bounds.getMin().floatValue()) / plot.getHeight());
        }
        // move the calculated offset
        bounds.setMin(bounds.getMin().floatValue() + offset);
        bounds.setMax(bounds.getMax().floatValue() + offset);

        //get the distance between max and min
        final float diff = bounds.length().floatValue();

        //check if we reached the limit of panning
        if (horizontal) {
            if (bounds.getMin().floatValue() < getMinXLimit().floatValue()) {
                bounds.setMin(getMinXLimit());
                bounds.setMax(bounds.getMin().floatValue() + diff);
            }
            if (bounds.getMax().floatValue() > getMaxXLimit().floatValue()) {
                bounds.setMax(getMaxXLimit());
                bounds.setMin(bounds.getMax().floatValue() - diff);
            }
        } else {
            if (bounds.getMin().floatValue() < getMinYLimit().floatValue()) {
                bounds.setMin(getMinYLimit());
                bounds.setMax(bounds.getMin().floatValue() + diff);
            }
            if (bounds.getMax().floatValue() > getMaxYLimit().floatValue()) {
                bounds.setMax(getMaxYLimit());
                bounds.setMin(bounds.getMax().floatValue() - diff);
            }
        }
    }

    protected boolean isValidScale(float scale) {
        if (Float.isInfinite(scale) || Float.isNaN(scale) || scale > -0.001 && scale < 0.001) {
            return false;
        }
        return true;
    }

    protected void zoom(final MotionEvent motionEvent) {
        if (zoom == Zoom.NONE) {
            return;
        }
        final RectF oldFingersRect = fingersRect;
        final RectF newFingersRect = fingerDistance(motionEvent);
        fingersRect = newFingersRect;
        RectF newRect = new RectF();

        float scaleX = 1;
        float scaleY = 1;
        switch (zoom) {
            case STRETCH_HORIZONTAL:
                scaleX = oldFingersRect.width() / fingersRect.width();
                if (!isValidScale(scaleX)) {
                    return;
                }
                break;
            case STRETCH_VERTICAL:
                scaleY = oldFingersRect.height() / fingersRect.height();
                if (!isValidScale(scaleY)) {
                    return;
                }
                break;
            case STRETCH_BOTH:
                scaleX = oldFingersRect.width() / fingersRect.width();
                scaleY = oldFingersRect.height() / fingersRect.height();
                if (!isValidScale(scaleX) || !isValidScale(scaleY)) {
                    return;
                }
                break;
            case SCALE:
                float sc1 = (float) Math.hypot(oldFingersRect.height(), oldFingersRect.width());
                float sc2 = (float) Math.hypot(fingersRect.height(), fingersRect.width());
                float sc = sc1 / sc2;
                scaleX = sc;
                scaleY = sc;
                if (!isValidScale(scaleX) || !isValidScale(scaleY)) {
                    return;
                }
                break;
        }

        if (EnumSet.of(
                Zoom.STRETCH_HORIZONTAL,
                Zoom.STRETCH_BOTH,
                Zoom.SCALE).contains(zoom)) {
            calculateZoom(newRect, scaleX, true);
            plot.setDomainBoundaries(newRect.left, newRect.right, BoundaryMode.FIXED);
            previousLimits.setMinX(newRect.left);
            previousLimits.setMaxX(newRect.right);
        }
        if (EnumSet.of(
                Zoom.STRETCH_VERTICAL,
                Zoom.STRETCH_BOTH,
                Zoom.SCALE).contains(zoom)) {
            calculateZoom(newRect, scaleY, false);
            plot.setRangeBoundaries(newRect.top, newRect.bottom, BoundaryMode.FIXED);
            previousLimits.setMinY(newRect.top);
            previousLimits.setMaxY(newRect.bottom);
        }
        plot.redraw();
    }

    protected void calculateZoom(RectF newRect, float scale, boolean isHorizontal) {

        final float calcMax;
        final float span;
        if (isHorizontal) {
            calcMax = getLastMaxX().floatValue();
            span = calcMax - getLastMinX().floatValue();
        } else {
            calcMax = getLastMaxY().floatValue();
            span = calcMax - getLastMinY().floatValue();
        }

        final float midPoint = calcMax - (span / 2.0f);
        final float offset = span * scale / 2.0f;

        if (isHorizontal) {
            newRect.left = midPoint - offset;
            newRect.right = midPoint + offset;
            if (newRect.left < getMinXLimit().floatValue()) {
                newRect.left = getMinXLimit().floatValue();
            }
            if (newRect.right > getMaxXLimit().floatValue()) {
                newRect.right = getMaxXLimit().floatValue();
            }
        } else {
            newRect.top = midPoint - offset;
            newRect.bottom = midPoint + offset;
            if (newRect.top < getMinYLimit().floatValue()) {
                newRect.top = getMinYLimit().floatValue();
            }
            if (newRect.bottom > getMaxYLimit().floatValue()) {
                newRect.bottom = getMaxYLimit().floatValue();
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

    public void reset() {
        Log.d("PanZoom", "Reset invoked");
        this.previousLimits = new RectRegion();
        this.firstFingerPos = null;
        this.fingersRect = null;
    }
}
