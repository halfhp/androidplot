// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.androidplot.demos.databinding.MainBinding

class MainActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // each button opens one example activity
        listOf(
            binding.startSimplePieExButton to SimplePieChartActivity::class.java,
            binding.startSimpleXYExButton to SimpleXYPlotActivity::class.java,
            binding.animatedXYPlotExButton to AnimatedXYPlotActivity::class.java,
            binding.startScatterExButton to ScatterPlotActivity::class.java,
            binding.startDynamicXYExButton to DynamicXYPlotActivity::class.java,
            binding.startCandlestickExButton to CandlestickChartActivity::class.java,
            binding.startOrSensorExButton to OrientationSensorExampleActivity::class.java,
            binding.startDualScaleExButton to DualScaleActivity::class.java,
            binding.startTimeSeriesExButton to TimeSeriesActivity::class.java,
            binding.startStepChartExButton to StepChartExampleActivity::class.java,
            binding.startScrollZoomButton to TouchZoomExampleActivity::class.java,
            binding.startBarPlotExButton to BarPlotExampleActivity::class.java,
            binding.startXyRegionExampleButton to XYRegionExampleActivity::class.java,
            binding.startXyListViewExButton to ListViewActivity::class.java,
            binding.startXyRecyclerViewExButton to RecyclerViewActivity::class.java,
            binding.startXYPlotWithBgImgExample to XYPlotWithBgImgActivity::class.java,
            binding.startECGExample to ECGExample::class.java,
            binding.fxPlotExample to FXPlotExampleActivity::class.java,
            binding.bubbleChartExample to BubbleChartActivity::class.java,
            binding.aboutButton to AboutActivity::class.java,
        ).forEach { (button, activity) ->
            button.setOnClickListener { startActivity(Intent(this, activity)) }
        }
    }
}
