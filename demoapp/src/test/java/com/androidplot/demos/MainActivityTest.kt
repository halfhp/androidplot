// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    private lateinit var controller: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    /** Which example each button on the main screen opens. */
    private val expected: Map<Int, Class<out Activity>> = mapOf(
        R.id.startSimplePieExButton to SimplePieChartActivity::class.java,
        R.id.startSimpleXYExButton to SimpleXYPlotActivity::class.java,
        R.id.animatedXYPlotExButton to AnimatedXYPlotActivity::class.java,
        R.id.startScatterExButton to ScatterPlotActivity::class.java,
        R.id.startDynamicXYExButton to DynamicXYPlotActivity::class.java,
        R.id.startCandlestickExButton to CandlestickChartActivity::class.java,
        R.id.startOrSensorExButton to OrientationSensorExampleActivity::class.java,
        R.id.startDualScaleExButton to DualScaleActivity::class.java,
        R.id.startTimeSeriesExButton to TimeSeriesActivity::class.java,
        R.id.startStepChartExButton to StepChartExampleActivity::class.java,
        R.id.startScrollZoomButton to TouchZoomExampleActivity::class.java,
        R.id.startBarPlotExButton to BarPlotExampleActivity::class.java,
        R.id.startXyRegionExampleButton to XYRegionExampleActivity::class.java,
        R.id.startXyListViewExButton to ListViewActivity::class.java,
        R.id.startXyRecyclerViewExButton to RecyclerViewActivity::class.java,
        R.id.startXYPlotWithBgImgExample to XYPlotWithBgImgActivity::class.java,
        R.id.startECGExample to ECGExample::class.java,
        R.id.fxPlotExample to FXPlotExampleActivity::class.java,
        R.id.bubbleChartExample to BubbleChartActivity::class.java,
        R.id.aboutButton to AboutActivity::class.java,
    )

    @Before
    fun setUp() {
        controller = DemoAppTest.launch(MainActivity::class.java)
        activity = controller.get()
    }

    @After
    fun tearDown() {
        DemoAppTest.finish(controller)
    }

    @Test
    fun eachButtonStartsItsExample() {
        assertNull(shadowOf(activity).nextStartedActivity)
        expected.forEach { (id, cls) ->
            val button = activity.findViewById<Button>(id)
            button.performClick()
            DemoAppTest.idle()
            val intent = shadowOf(activity).nextStartedActivity
            assertEquals("button ${activity.resources.getResourceEntryName(id)}",
                cls.name, intent?.component?.className)
        }
        assertNull(shadowOf(activity).nextStartedActivity)
    }

    @Test
    fun everyButtonIsWired() {
        val buttons = buttonsIn(activity.window.decorView)
        assertEquals(expected.keys, buttons.map { it.id }.toSet())
        buttons.forEach { assertEquals(it.text.toString(), true, it.hasOnClickListeners()) }
    }

    @Test
    fun eachExampleButtonOpensADifferentActivity() {
        assertEquals(expected.size, expected.values.toSet().size)
        // every example activity except the launcher itself has a button
        assertEquals(DemoActivities.ALL.toSet() - MainActivity::class.java, expected.values.toSet())
    }

    private fun buttonsIn(view: View): List<Button> = when (view) {
        is Button -> listOf(view)
        is ViewGroup -> (0 until view.childCount).flatMap { buttonsIn(view.getChildAt(it)) }
        else -> emptyList()
    }
}
