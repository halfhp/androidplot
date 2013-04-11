package com.androidplot.mock;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetric;
import com.androidplot.ui.SizeMetrics;
import mockit.Mock;
import mockit.MockClass;

@MockClass(realClass = SizeMetrics.class)
public class MockSizeMetrics {

    @Mock
    public SizeMetric getWidthMetric() {
        return new SizeMetric(0, SizeLayoutType.ABSOLUTE);
    }

    @Mock
    public SizeMetric getHeightMetric() {
        return new SizeMetric(0, SizeLayoutType.ABSOLUTE);
    }
}
