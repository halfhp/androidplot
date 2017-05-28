package com.androidplot.xy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidplot.util.FastNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * An efficient implementation of {@link EditableXYSeries} intended for use cases where
 * the total number of points visible is known ahead of time and is fairly static.
 *
 * {@link #resize(int)} may be used to resize the series when necessary, however it is a slow
 * operation and should be avoided as much as possible.
 *
 */
public class FixedSizeEditableXYSeries implements EditableXYSeries {

    @NonNull
    private List<FastNumber> xVals = new ArrayList<>();

    @NonNull
    private List<FastNumber> yVals = new ArrayList<>();
    private String title;

    public FixedSizeEditableXYSeries(String title, int size) {
        setTitle(title);
        resize(size);
    }

    @Override
    public void setX(@Nullable Number x, int index) {
        xVals.set(index, FastNumber.orNull(x));
    }

    @Override
    public void setY(@Nullable Number y, int index) {
        yVals.set(index, FastNumber.orNull(y));
    }

    /**
     * May be used to dynamically resize the series.  This is a relatively slow operation, especially
     * as size increases so care should be taken to avoid unnecessary usage.
     * @param size
     */
    @Override
    public void resize(int size) {
        resize(xVals, size);
        resize(yVals, size);
    }

    protected void resize(@NonNull List list, int size) {
        if (size > list.size()) {
            while (list.size() < size) {
                list.add(null);
            }
        } else if (size < list.size()) {
            while (list.size() > size) {
                list.remove(list.size() - 1);
            }
        }
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
