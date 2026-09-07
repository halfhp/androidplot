// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import com.androidplot.demos.databinding.MainBinding

class MainActivity : Activity() {

    private lateinit var binding: MainBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)

        binding.animatedXYPlotExButton.setOnClickListener {
            startActivity(Intent(this, AnimatedXYPlotActivity::class.java))
        }

        binding.startScatterExButton.setOnClickListener {
            startActivity(Intent(this, ScatterPlotActivity::class.java))
        }

        binding.startSimplePieExButton.setOnClickListener {
            startActivity(Intent(this, SimplePieChartActivity::class.java))
        }

        binding.startDynamicXYExButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, DynamicXYPlotActivity::class.java))
        }

        binding.startCandlestickExButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, CandlestickChartActivity::class.java))
        }

        binding.startSimpleXYExButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SimpleXYPlotActivity::class.java))
        }

        binding.startBarPlotExButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, BarPlotExampleActivity::class.java))
        }

        binding.startOrSensorExButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, OrientationSensorExampleActivity::class.java))
        }

        binding.startDualScaleExButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, DualScaleActivity::class.java))
        }

        binding.startTimeSeriesExButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, TimeSeriesActivity::class.java))
        }

        binding.startStepChartExButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, StepChartExampleActivity::class.java))
        }

        binding.startScrollZoomButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, TouchZoomExampleActivity::class.java))
        }

        binding.startXyRegionExampleButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, XYRegionExampleActivity::class.java))
        }

        binding.startXyListViewExButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, ListViewActivity::class.java))
        }

        binding.startXyRecyclerViewExButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, RecyclerViewActivity::class.java))
        }

        binding.startXYPlotWithBgImgExample.setOnClickListener {
            startActivity(Intent(this@MainActivity, XYPlotWithBgImgActivity::class.java))
        }

        binding.startECGExample.setOnClickListener {
            startActivity(Intent(this@MainActivity, ECGExample::class.java))
        }

        binding.fxPlotExample.setOnClickListener {
            startActivity(Intent(this@MainActivity, FXPlotExampleActivity::class.java))
        }

        binding.bubbleChartExample.setOnClickListener {
            startActivity(Intent(this@MainActivity, BubbleChartActivity::class.java))
        }

        setContentView(binding.root)
    }
}