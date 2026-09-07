// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos;

import android.app.Activity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Every activity declared in the demoapp's AndroidManifest.xml.  Hand-maintained, but
 * {@link DemoActivitiesTest} fails if it ever disagrees with the manifest, so an example added to
 * the app cannot be left out of {@link ActivityLifecycleTest}.
 */
final class DemoActivities {

    static final List<Class<? extends Activity>> ALL = Collections.unmodifiableList(Arrays.asList(
            MainActivity.class,
            SimplePieChartActivity.class,
            AnimatedXYPlotActivity.class,
            SimpleXYPlotActivity.class,
            ScatterPlotActivity.class,
            BarPlotExampleActivity.class,
            DynamicXYPlotActivity.class,
            CandlestickChartActivity.class,
            OrientationSensorExampleActivity.class,
            StepChartExampleActivity.class,
            TouchZoomExampleActivity.class,
            ListViewActivity.class,
            RecyclerViewActivity.class,
            XYRegionExampleActivity.class,
            TimeSeriesActivity.class,
            XYPlotWithBgImgActivity.class,
            ECGExample.class,
            FXPlotExampleActivity.class,
            BubbleChartActivity.class,
            DualScaleActivity.class,
            AboutActivity.class));

    private DemoActivities() {}
}
