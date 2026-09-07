// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos

import android.app.Activity
import android.graphics.Color
import com.androidplot.ui.SeriesBundle
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidplot.demos.databinding.RecyclerviewExampleBinding
import com.androidplot.demos.databinding.RecyclerviewExampleItemBinding
import com.androidplot.util.PixelUtils
import com.androidplot.xy.*
import kotlin.random.Random

private typealias PlotData = List<SeriesBundle<XYSeries, LineAndPointFormatter>>

class RecyclerViewActivity : Activity() {
    private lateinit var binding: RecyclerviewExampleBinding

    companion object {
        private const val NUM_PLOTS = 10
        private const val NUM_POINTS_PER_SERIES = 10
        private const val NUM_SERIES_PER_PLOT = 5
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // formatters below are built before any Plot view exists, so init PixelUtils by hand
        PixelUtils.init(this)

        binding = RecyclerviewExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = MyRecyclerViewAdapter()
    }

    class MyRecyclerViewHolder(
        private val binding: RecyclerviewExampleItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        // recycling: clear the plot, re-add this row's series, redraw
        fun bind(data: PlotData, title: String) {
            val plot = binding.plot
            plot.clear()
            plot.title.text = title
            data.forEach { plot.addSeries(it.series, it.formatter) }
            plot.redraw()
        }
    }

    class MyRecyclerViewAdapter : RecyclerView.Adapter<MyRecyclerViewHolder>() {
        private val seriesData: List<PlotData> =
            List(NUM_PLOTS) { List(NUM_SERIES_PER_PLOT) { k -> generateBundle("S$k") } }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewHolder {
            val itemBinding = RecyclerviewExampleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyRecyclerViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: MyRecyclerViewHolder, position: Int) {
            holder.bind(seriesData[position], "Series $position")
        }

        override fun getItemCount() = seriesData.size

        private fun randomColor() = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))

        private fun generateBundle(seriesLabel: String): SeriesBundle<XYSeries, LineAndPointFormatter> {
            val yVals = List<Number>(NUM_POINTS_PER_SERIES) { Random.nextFloat() }

            val formatter = LineAndPointFormatter(randomColor(), randomColor(), null, null).apply {
                // for fun, configure interpolation on the formatter:
                interpolationParams = CatmullRomInterpolator.Params(20, CatmullRomInterpolator.Type.Centripetal)
            }

            return SeriesBundle(
                SimpleXYSeries(yVals, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, seriesLabel),
                formatter
            )
        }
    }
}
