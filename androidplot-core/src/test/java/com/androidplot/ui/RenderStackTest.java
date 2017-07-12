package com.androidplot.ui;

import com.androidplot.Plot;
import com.androidplot.SeriesRegistry;
import com.androidplot.test.AndroidplotTest;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.XYSeries;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RenderStackTest extends AndroidplotTest {

    @Mock Plot plot;
    @Mock SeriesRegistry seriesRegistry;

    @Before
    public void before() {
        when(plot.getRegistry()).thenReturn(seriesRegistry);
    }

    @Test
    public void disable_disablesElements_ofSpecifiedRendererOnly() {
        final SeriesBundle s1 = new SeriesBundle(mock(XYSeries.class),
                new LineAndPointFormatter());
        final SeriesBundle s2 = new SeriesBundle(mock(XYSeries.class),
                new LineAndPointFormatter());
        when(seriesRegistry.getSeriesAndFormatterList()).thenReturn(Lists.newArrayList(s1, s2));
        RenderStack<XYSeries, LineAndPointFormatter> renderStack = new RenderStack<>(plot);

        renderStack.sync();
        assertEquals(2, renderStack.getElements().size());
        for(RenderStack.StackElement element : renderStack.getElements()) {
            assertTrue(element.isEnabled());
        }

        renderStack.disable(LineAndPointRenderer.class);
        assertEquals(2, renderStack.getElements().size());
        for(RenderStack.StackElement element : renderStack.getElements()) {
            assertFalse(element.isEnabled());
        }
    }
}
