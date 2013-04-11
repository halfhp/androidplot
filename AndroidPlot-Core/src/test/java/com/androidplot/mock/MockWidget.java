package com.androidplot.mock;

import com.androidplot.ui.widget.Widget;
import mockit.Mock;
import mockit.MockClass;

@MockClass(realClass = Widget.class, inverse = false)
public class MockWidget {

    @Mock
    public float getWidthPix(float f) {
        return 100;
    }

    @Mock
    public float getHeightPix(float f) {
        return 100;
    }

}
