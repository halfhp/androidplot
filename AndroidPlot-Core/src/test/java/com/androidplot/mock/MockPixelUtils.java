package com.androidplot.mock;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import com.androidplot.util.PixelUtils;
import mockit.Mock;
import mockit.MockClass;

@MockClass(realClass = PixelUtils.class, stubs="", inverse=true)
public class MockPixelUtils {

    @Mock
    public static void init(Context ctx) {

    }

    @Mock
    public static PointF sub(PointF lhs, PointF rhs) {
        return new PointF(0, 0);
    }
}
