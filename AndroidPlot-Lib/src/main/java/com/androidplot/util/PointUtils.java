package com.androidplot.util;

import android.graphics.PointF;

public class PointUtils {

    public static PointF add(PointF lhs, PointF rhs) {
        return new PointF(lhs.x + rhs.x, lhs.y + rhs.y);
    }

    public static PointF sub(PointF lhs, PointF rhs) {
        return new PointF(lhs.x - rhs.x, lhs.y - rhs.y);
    }
}

