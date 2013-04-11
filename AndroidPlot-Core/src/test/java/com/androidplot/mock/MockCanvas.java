package com.androidplot.mock;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import mockit.Mock;
import mockit.MockClass;
import mockit.MockUp;
import mockit.Mocked;

@MockClass(realClass = Canvas.class, stubs="", inverse=true)
public class MockCanvas {

    @Mock
    public int getHeight() {
        return 100;
    }

    @Mock
    public int getWidth() {
        return 100;
    }

    @Mock
    public void restore() {}

    @Mock
    public int save(int flags) {
        return 1;
    }

    @Mock
    public void drawPoint(float x, float y, Paint paint) {}
}
