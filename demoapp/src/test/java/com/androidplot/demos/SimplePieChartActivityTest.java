// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.graphics.PointF;
import android.graphics.RectF;
import android.widget.SeekBar;
import android.widget.TextView;
import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class SimplePieChartActivityTest {

    private static final float SELECTED_SEGMENT_OFFSET = 50;

    private ActivityController<SimplePieChartActivity> controller;
    private SimplePieChartActivity activity;
    private PieChart pie;
    private PieRenderer renderer;

    @Before
    public void setUp() {
        controller = DemoAppTest.launch(SimplePieChartActivity.class);
        activity = controller.get();
        pie = activity.findViewById(R.id.mySimplePieChart);
        renderer = pie.getRenderer(PieRenderer.class);
        assertNotNull(renderer);
    }

    @After
    public void tearDown() {
        DemoAppTest.finish(controller);
        DemoAppTest.assertNoLoggedErrors();
    }

    @Test
    public void initialState() {
        assertEquals(4, pie.getRegistry().getSeriesList().size());
        for (Segment segment : pie.getRegistry().getSeriesList()) {
            assertEquals(0, offsetOf(segment), 0.001f);
        }
        TextView donutSizeText = activity.findViewById(R.id.donutSizeTextView);
        assertEquals("50%", donutSizeText.getText().toString());
        DemoAppTest.draw(pie);
    }

    @Test
    public void tappingASegment_togglesItsOffset() {
        DemoAppTest.draw(pie);

        // a point right of the pie's centre, on whichever segment the renderer puts there
        RectF pieRect = pie.getPie().getWidgetDimensions().marginatedRect;
        PointF point = new PointF(pieRect.centerX() + pieRect.width() / 4, pieRect.centerY());
        assertTrue(pie.getPie().containsPoint(point));
        Segment tapped = renderer.getContainingSegment(point);
        assertNotNull(tapped);

        shadowOf(pie).clearWasInvalidated();
        DemoAppTest.tap(pie, point);
        assertTrue(shadowOf(pie).wasInvalidated());
        for (Segment segment : pie.getRegistry().getSeriesList()) {
            assertEquals(segment.getTitle(),
                    segment == tapped ? SELECTED_SEGMENT_OFFSET : 0, offsetOf(segment), 0.001f);
        }
        DemoAppTest.draw(pie);

        // tapping a different segment moves the selection
        PointF opposite = new PointF(pieRect.centerX() - pieRect.width() / 4, pieRect.centerY());
        Segment other = renderer.getContainingSegment(opposite);
        assertNotSame(tapped, other);
        DemoAppTest.tap(pie, opposite);
        assertEquals(0, offsetOf(tapped), 0.001f);
        assertEquals(SELECTED_SEGMENT_OFFSET, offsetOf(other), 0.001f);

        // tapping the selected segment again deselects it
        DemoAppTest.tap(pie, opposite);
        for (Segment segment : pie.getRegistry().getSeriesList()) {
            assertEquals(0, offsetOf(segment), 0.001f);
        }
        DemoAppTest.draw(pie);
    }

    @Test
    public void tappingOutsideThePie_changesNothing() {
        DemoAppTest.draw(pie);
        PointF corner = new PointF(1, 1);
        DemoAppTest.tap(pie, corner);
        for (Segment segment : pie.getRegistry().getSeriesList()) {
            assertEquals(0, offsetOf(segment), 0.001f);
        }
    }

    @Test
    public void donutSeekBar_changesDonutSizeOnRelease() {
        SeekBar seekBar = activity.findViewById(R.id.donutSizeSeekBar);
        TextView donutSizeText = activity.findViewById(R.id.donutSizeTextView);

        // the example only applies the new size when the user lets go of the thumb
        seekBar.setProgress(30);
        DemoAppTest.idle();
        assertEquals("50%", donutSizeText.getText().toString());

        shadowOf(pie).clearWasInvalidated();
        shadowOf(seekBar).getOnSeekBarChangeListener().onStopTrackingTouch(seekBar);
        DemoAppTest.idle();
        assertEquals("30%", donutSizeText.getText().toString());
        assertTrue(shadowOf(pie).wasInvalidated());
        DemoAppTest.draw(pie);

        // 0% (a plain pie) and the max (90%) are both legal
        seekBar.setProgress(0);
        shadowOf(seekBar).getOnSeekBarChangeListener().onStopTrackingTouch(seekBar);
        assertEquals("0%", donutSizeText.getText().toString());
        DemoAppTest.draw(pie);
        seekBar.setProgress(seekBar.getMax());
        shadowOf(seekBar).getOnSeekBarChangeListener().onStopTrackingTouch(seekBar);
        assertEquals("90%", donutSizeText.getText().toString());
        DemoAppTest.draw(pie);
    }

    private float offsetOf(Segment segment) {
        return pie.getFormatter(segment, PieRenderer.class).getOffset();
    }
}
