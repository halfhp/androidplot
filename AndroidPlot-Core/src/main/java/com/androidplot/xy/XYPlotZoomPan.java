package com.androidplot.xy;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class XYPlotZoomPan extends XYPlot implements OnTouchListener {
    private static final float MIN_DIST_2_FING = 5f;

    // Definition of the touch states
    private enum State
    {
        NONE,
        ONE_FINGER_DRAG,
        TWO_FINGERS_DRAG
    }

    private State mode = State.NONE;
    private Number minXLimit;
    private Number maxXLimit;
    private Number minYLimit;
    private Number maxYLimit;
    private PointF firstFingerPos;
    private float mDistX;
    private boolean mZoomEnabled = true; //default is enabled
    private boolean mZoomVertically = true;
    private boolean mZoomHorizontally = true;

    public XYPlotZoomPan(Context context, String title, RenderMode mode) {
        super(context, title, mode);
        setZoomEnabled(true); //Default is ZoomEnabled if instantiated programmatically
    }

    public XYPlotZoomPan(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        if(mZoomEnabled) {
            setZoomEnabled(true);
        }
    }

    public XYPlotZoomPan(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        if(mZoomEnabled) {
            setZoomEnabled(true);
        }
    }

    public XYPlotZoomPan(final Context context, final String title) {
        super(context, title);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        if(l != this) {
            mZoomEnabled = false;
        }
        super.setOnTouchListener(l);
    }

    public boolean getZoomVertically() {
        return mZoomVertically;
    }

    public void setZoomVertically(boolean zoomVertically) {
        mZoomVertically = zoomVertically;
    }

    public boolean getZoomHorizontally() {
        return mZoomHorizontally;
    }

    public void setZoomHorizontally(boolean zoomHorizontally) {
        mZoomHorizontally = zoomHorizontally;
    }

    public void setZoomEnabled(boolean enabled) {
        if(enabled) {
            setOnTouchListener(this);
        } else {
            setOnTouchListener(null);
        }
        mZoomEnabled = enabled;
    }

    public boolean getZoomEnabled() {
        return mZoomEnabled;
    }

    @Override
    public synchronized void setDomainBoundaries(final Number lowerBoundary, final Number upperBoundary, final BoundaryMode mode) {
        minXLimit = lowerBoundary;
        maxXLimit = upperBoundary;
        super.setDomainBoundaries(lowerBoundary, upperBoundary, mode);
    }

    @Override
    public synchronized void setRangeBoundaries(final Number lowerBoundary, final Number upperBoundary, final BoundaryMode mode) {
        minYLimit = lowerBoundary;
        maxYLimit = upperBoundary;
        super.setRangeBoundaries(lowerBoundary, upperBoundary, mode);
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN: // start gesture
                firstFingerPos = new PointF(event.getX(), event.getY());
                mode = State.ONE_FINGER_DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // second finger
            {
                mDistX = getXDistance(event);
                // the distance check is done to avoid false alarms
                if(mDistX > MIN_DIST_2_FING || mDistX < -MIN_DIST_2_FING) {
                    mode = State.TWO_FINGERS_DRAG;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: // end zoom
                mode = State.NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if(mode == State.ONE_FINGER_DRAG) {
                    pan(event);
                } else if(mode == State.TWO_FINGERS_DRAG) {
                    zoom(event);
                }
                break;
        }
        return true;
    }

    private float getXDistance(final MotionEvent event) {
        return event.getX(0) - event.getX(1);
    }

    private void pan(final MotionEvent motionEvent) {
        calculateMinMaxVals();
        final PointF oldFirstFinger = firstFingerPos; //save old position of finger
        firstFingerPos = new PointF(motionEvent.getX(), motionEvent.getY()); //update finger position
        PointF newX = new PointF();
        if(mZoomHorizontally) {
            calculatePan(oldFirstFinger, newX, true);
            super.setDomainBoundaries(newX.x, newX.y, BoundaryMode.FIXED);
        }
        if(mZoomVertically) {
            calculatePan(oldFirstFinger, newX, false);
            super.setRangeBoundaries(newX.x, newX.y, BoundaryMode.FIXED);
        }
        redraw();
    }

    private void calculatePan(final PointF oldFirstFinger, PointF newX, final boolean horizontal) {
        final float offset;
        // multiply the absolute finger movement for a factor.
        // the factor is dependent on the calculated min and max
        if(horizontal) {
            newX.x = getCalculatedMinX().floatValue();
            newX.y = getCalculatedMaxX().floatValue();
            offset = (oldFirstFinger.x - firstFingerPos.x) * ((newX.y - newX.x) / getWidth());
        } else {
            newX.x = getCalculatedMinY().floatValue();
            newX.y = getCalculatedMaxY().floatValue();
            offset = (oldFirstFinger.y - firstFingerPos.y) * ((newX.y - newX.x) / getHeight());
        }
        // move the calculated offset
        newX.x = newX.x + offset;
        newX.y = newX.y + offset;
        //get the distance between max and min
        final float diff = newX.y - newX.x;
        //check if we reached the limit of panning
        if(horizontal) {
            if(newX.x < minXLimit.floatValue()) {
                newX.x = minXLimit.floatValue();
                newX.y = newX.x + diff;
            }
            if(newX.y > maxXLimit.floatValue()) {
                newX.y = maxXLimit.floatValue();
                newX.x = newX.y - diff;
            }
        } else {
            if(newX.x < minYLimit.floatValue()) {
                newX.x = minYLimit.floatValue();
                newX.y = newX.x + diff;
            }
            if(newX.y > maxYLimit.floatValue()) {
                newX.y = maxYLimit.floatValue();
                newX.x = newX.y - diff;
            }
        }
    }

    private void zoom(final MotionEvent motionEvent) {
        calculateMinMaxVals();
        final float oldDist = mDistX;
        final float newDist = getXDistance(motionEvent);
        // sign change! Fingers have crossed ;-)
        if(oldDist > 0 && newDist < 0 || oldDist < 0 && newDist > 0) {
            return;
        }
        mDistX = newDist;
        float scale = (oldDist / mDistX);
        // sanity check
        if(Float.isInfinite(scale) || Float.isNaN(scale) || scale > -0.001 && scale < 0.001) {
            return;
        }
        PointF newX = new PointF();
        if(mZoomHorizontally) {
            calculateZoom(scale, newX, true);
            super.setDomainBoundaries(newX.x, newX.y, BoundaryMode.FIXED);
        }
        if(mZoomVertically) {
            calculateZoom(scale, newX, false);
            super.setRangeBoundaries(newX.x, newX.y, BoundaryMode.FIXED);
        }
        redraw();
    }

    private void calculateZoom(float scale, PointF newX, final boolean horizontal) {
        final float calcMax;
        final float span;
        if(horizontal) {
            calcMax = getCalculatedMaxX().floatValue();
            span = calcMax - getCalculatedMinX().floatValue();
        } else {
            calcMax = getCalculatedMaxY().floatValue();
            span = calcMax - getCalculatedMinY().floatValue();
        }
        final float midPoint = calcMax - (span / 2.0f);
        final float offset = span * scale / 2.0f;
        newX.x = midPoint - offset;
        newX.y = midPoint + offset;
        if(horizontal) {
            if(newX.x < minXLimit.floatValue()) {
                newX.x = minXLimit.floatValue();
            }
            if(newX.y > maxXLimit.floatValue()) {
                newX.y = maxXLimit.floatValue();
            }
        } else {
            if(newX.x < minYLimit.floatValue()) {
                newX.x = minYLimit.floatValue();
            }
            if(newX.y > maxYLimit.floatValue()) {
                newX.y = maxYLimit.floatValue();
            }
        }
    }
}
