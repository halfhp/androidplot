package com.androidplot.mock;

import android.graphics.PointF;
import mockit.Mock;
import mockit.MockClass;

@MockClass(realClass = PointF.class)
public class MockPointF {
    float x;
    float y;

    @Mock
    public void $init() {}

    @Mock
    public void $init(float x, float y) {
        set(x, y);
    }

    @Mock
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
