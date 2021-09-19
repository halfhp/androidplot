/*
 * Copyright 2021 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
import java.util.*

class RecyclerViewActivity : Activity() {
    private lateinit var binding: RecyclerviewExampleBinding

    companion object {
        private const val NUM_PLOTS = 10
        private const val NUM_POINTS_PER_SERIES = 10
        private const val NUM_SERIES_PER_PLOT = 5
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        fun bind(data: List<SeriesBundle<XYSeries, LineAndPointFormatter>>, title: String) {

            val plot = binding.plot
            plot.clear()
            plot.title.text = title
            for (sf in data) {
                plot.addSeries(sf.series, sf.formatter)
            }
        }

        // called by the adapter whenever a holder is attached.
        // this is necessary to do if you want to use background rendering mode.
        // otherwise just put the redraw in the bind method above.
        fun redraw() {
            binding.plot.redraw()
        }

    }

    class MyRecyclerViewAdapter : RecyclerView.Adapter<MyRecyclerViewHolder>() {
        private val seriesData = generateData()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewHolder {
            val itemBinding = RecyclerviewExampleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyRecyclerViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: MyRecyclerViewHolder, position: Int) {
            holder.bind(seriesData[position], "Series $position")
        }

        // necessary only if you use background rendering mode:
        override fun onViewAttachedToWindow(holder: MyRecyclerViewHolder) {
            super.onViewAttachedToWindow(holder)
            holder.redraw()
        }

        override fun getItemCount() = seriesData.size

        private fun generateData(): List<List<SeriesBundle<XYSeries, LineAndPointFormatter>>> {
            val theData = mutableListOf<List<SeriesBundle<XYSeries, LineAndPointFormatter>>>()
            fun generateBundle(seriesLabel: String): SeriesBundle<XYSeries, LineAndPointFormatter> {
                val generator = Random()
                val nums = ArrayList<Number>()
                for (j in 0 until NUM_POINTS_PER_SERIES) {
                    nums.add(generator.nextFloat())
                }

                val formatter = LineAndPointFormatter(
                    Color.rgb(
                        java.lang.Double.valueOf(Math.random() * 255).toInt(),
                        java.lang.Double.valueOf(Math.random() * 255).toInt(),
                        java.lang.Double.valueOf(Math.random() * 255).toInt()
                    ),
                    Color.rgb(
                        java.lang.Double.valueOf(Math.random() * 255).toInt(),
                        java.lang.Double.valueOf(Math.random() * 255).toInt(),
                        java.lang.Double.valueOf(Math.random() * 255).toInt()
                    ),
                    null, null
                )

                // for fun, configure interpolation on the formatter:
                formatter.interpolationParams = CatmullRomInterpolator.Params(
                    20,
                    CatmullRomInterpolator.Type.Centripetal
                )

                return SeriesBundle(
                    SimpleXYSeries(
                        nums,
                        SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                        seriesLabel
                    ),
                    formatter
                )
            }

            for (i in 0 until NUM_PLOTS) {
                val seriesList: MutableList<SeriesBundle<XYSeries, LineAndPointFormatter>> =
                    ArrayList(NUM_SERIES_PER_PLOT)

                for (k in 0 until NUM_SERIES_PER_PLOT) {
                    seriesList.add(generateBundle("S$k"))
                }
                theData.add(seriesList)
            }
            return theData
        }
    }
}