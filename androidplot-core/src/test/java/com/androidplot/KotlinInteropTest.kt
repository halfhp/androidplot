// SPDX-License-Identifier: Apache-2.0
package com.androidplot

import android.graphics.Color
import com.androidplot.test.AndroidplotTest
import com.androidplot.util.SeriesUtils
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.LineAndPointRenderer
import com.androidplot.xy.RectRegion
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYPlot
import com.androidplot.xy.XYSeries
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke test of the public API from Kotlin.  Its value is mostly that it compiles: the nullability
 * annotations turn the platform types Kotlin would otherwise see into real nullable / non-null
 * types, so `?.` / `?:` are required on the @Nullable returns used below and forbidden (as
 * warnings) on the @NonNull ones.
 */
class KotlinInteropTest : AndroidplotTest() {

    @Test
    fun nullablePointsAndOptionalStateAreNullableInKotlin() {
        // a null y value is legal series data, so getY() is Number? in Kotlin
        val series: XYSeries = SimpleXYSeries(listOf<Number?>(1, null, 3), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null)
        val ys: List<Double?> = (0 until series.size()).map { series.getY(it)?.toDouble() }
        assertEquals(listOf(1.0, null, 3.0), ys)
        assertEquals(1.0, series.getX(1)?.toDouble() ?: Double.NaN, 0.0)
        val title: String = series.title ?: "untitled"
        assertEquals("untitled", title)

        // null paint colors mean "disabled"; the fill paint getter is nullable (BarFormatter
        // returns the raw field) while the line paint is created lazily and never null
        val formatter = LineAndPointFormatter(Color.RED, Color.BLUE, null, null)
        assertNotNull(formatter.linePaint)
        assertFalse(formatter.hasFillPaint())
        val fillColor: Int = formatter.fillPaint?.color ?: Color.TRANSPARENT
        assertEquals(Color.TRANSPARENT, fillColor)

        val plot = XYPlot(context, "Kotlin")
        plot.title.text = "Kotlin interop" // getTitle() is @NonNull
        assertTrue(plot.addSeries(series, formatter))

        // an undefined region has null edges, and origins that have not been set are null
        assertNull(RectRegion().minX)
        val minX: Double = plot.bounds.minX?.toDouble() ?: Double.NaN
        assertFalse(minX.isNaN())
        val domainOrigin: Double = plot.domainOrigin?.toDouble() ?: 0.0
        assertEquals(0.0, domainOrigin, 0.0)

        // renderer lookup misses are null
        val renderer: LineAndPointRenderer<*>? = plot.getRenderer(LineAndPointRenderer::class.java)
        assertNotNull(renderer)
        val seriesCount = renderer?.getSeriesList()?.size ?: 0
        assertEquals(1, seriesCount)

        // min/max over a series with null values skips the nulls
        val minMax = SeriesUtils.minMax(series)
        assertEquals(1.0, minMax.minY?.toDouble() ?: Double.NaN, 0.0)
        assertEquals(3.0, minMax.maxY?.toDouble() ?: Double.NaN, 0.0)

        plot.layout(plot.displayDimensions)
        plot.redraw()
        assertEquals("Kotlin interop", plot.title.text)
    }
}
