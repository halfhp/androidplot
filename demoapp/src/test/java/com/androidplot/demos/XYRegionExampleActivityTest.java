// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.widget.CheckBox;
import com.androidplot.ui.SeriesBundle;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.RectRegion;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class XYRegionExampleActivityTest {

    private ActivityController<XYRegionExampleActivity> controller;
    private XYRegionExampleActivity activity;
    private XYPlot plot;

    @Before
    public void setUp() {
        controller = DemoAppTest.launch(XYRegionExampleActivity.class);
        activity = controller.get();
        plot = activity.findViewById(R.id.xyRegionExamplePlot);
    }

    @After
    public void tearDown() {
        DemoAppTest.finish(controller);
        DemoAppTest.assertNoLoggedErrors();
    }

    @Test
    public void initialState_bothSeriesWithAllRegions() {
        assertEquals(2, plot.getRegistry().getSeriesList().size());
        for (String series : new String[] {"Tim", "Nick"}) {
            assertEquals(series, Set.of("Short", "Warmup", "H. Run"), regionLabels(series));
        }
        for (int id : new int[] {R.id.s1CheckBox, R.id.s2CheckBox,
                R.id.r2CheckBox, R.id.r3CheckBox, R.id.r4CheckBox}) {
            assertTrue(((CheckBox) activity.findViewById(id)).isChecked());
        }
    }

    @Test
    public void regionCheckBoxes_addAndRemoveRegionsAndRedraw() {
        assertRegionToggle(R.id.r2CheckBox, "Tim", "Short");
        assertRegionToggle(R.id.r3CheckBox, "Nick", "Warmup");
        assertRegionToggle(R.id.r4CheckBox, "Nick", "H. Run");
    }

    private void assertRegionToggle(int checkBoxId, String series, String regionLabel) {
        CheckBox checkBox = activity.findViewById(checkBoxId);
        Set<String> others = new HashSet<>(regionLabels(series));
        assertTrue(others.remove(regionLabel));

        shadowOf(plot).clearWasInvalidated();
        checkBox.performClick();
        DemoAppTest.idle();
        assertFalse(checkBox.isChecked());
        assertEquals(others, regionLabels(series));
        assertTrue("unchecking must redraw", shadowOf(plot).wasInvalidated());

        shadowOf(plot).clearWasInvalidated();
        checkBox.performClick();
        DemoAppTest.idle();
        assertTrue(checkBox.isChecked());
        assertTrue(regionLabels(series).contains(regionLabel));
        assertTrue("checking must redraw", shadowOf(plot).wasInvalidated());
    }

    @Test
    public void seriesCheckBoxes_removeAndRestoreSeries() {
        CheckBox timCheckBox = activity.findViewById(R.id.s1CheckBox);
        CheckBox nickCheckBox = activity.findViewById(R.id.s2CheckBox);
        CheckBox r2CheckBox = activity.findViewById(R.id.r2CheckBox);
        CheckBox r3CheckBox = activity.findViewById(R.id.r3CheckBox);
        CheckBox r4CheckBox = activity.findViewById(R.id.r4CheckBox);

        shadowOf(plot).clearWasInvalidated();
        timCheckBox.performClick();
        DemoAppTest.idle();
        assertEquals(List.of("Nick"), seriesTitles());
        assertFalse("Tim's region control is disabled with the series", r2CheckBox.isEnabled());
        assertTrue(shadowOf(plot).wasInvalidated());

        nickCheckBox.performClick();
        DemoAppTest.idle();
        assertEquals(List.of(), seriesTitles());
        assertFalse(r3CheckBox.isEnabled());
        assertFalse(r4CheckBox.isEnabled());

        // an empty plot must still draw
        DemoAppTest.draw(plot);

        timCheckBox.performClick();
        nickCheckBox.performClick();
        DemoAppTest.idle();
        assertEquals(List.of("Tim", "Nick"), seriesTitles());
        assertTrue(r2CheckBox.isEnabled());
        assertTrue(r3CheckBox.isEnabled());
        assertTrue(r4CheckBox.isEnabled());

        // the formatter (and its regions) survive the series being removed and re-added
        assertEquals(Set.of("Short", "Warmup", "H. Run"), regionLabels("Tim"));
        DemoAppTest.draw(plot);
    }

    private List<String> seriesTitles() {
        List<String> titles = new ArrayList<>();
        for (XYSeries series : plot.getRegistry().getSeriesList()) {
            titles.add(series.getTitle());
        }
        return titles;
    }

    /** The labels of the regions on the named series' formatter (a layer list, so unordered). */
    private Set<String> regionLabels(String seriesTitle) {
        LineAndPointFormatter formatter = null;
        for (SeriesBundle<XYSeries, ? extends XYSeriesFormatter> bundle
                : plot.getRegistry().getSeriesAndFormatterList()) {
            if (seriesTitle.equals(bundle.getSeries().getTitle())) {
                formatter = (LineAndPointFormatter) bundle.getFormatter();
            }
        }
        assertNotNull("no series titled " + seriesTitle, formatter);
        Set<String> labels = new HashSet<>();
        for (RectRegion region : formatter.getRegions().elements()) {
            labels.add(region.getLabel());
        }
        return labels;
    }
}
