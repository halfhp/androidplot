/*
 * Copyright 2012 AndroidPlot.com
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

package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.xy.*;

import java.util.Arrays;

/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class SimplePieChartActivity extends Activity
{

    private PieChart pie;

    private Segment s1;
    private Segment s2;
    private Segment s3;
    private Segment s4;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_pie_chart_example);

        // initialize our XYPlot reference:
        pie = (PieChart) findViewById(R.id.mySimplePieChart);

        s1 = new Segment("s1", 10);
        s2 = new Segment("s2", 1);
        s3 = new Segment("s3", 10);
        s4 = new Segment("s4", 10);

        pie.addSeries(s1, new SegmentFormatter(
                Color.rgb(200, 150, 150), Color.DKGRAY,Color.DKGRAY, Color.DKGRAY));
        pie.addSeries(s2, new SegmentFormatter(
                Color.rgb(150, 200, 150), Color.DKGRAY,Color.DKGRAY, Color.DKGRAY));
        pie.addSeries(s3, new SegmentFormatter(
                Color.rgb(150, 150, 200), Color.DKGRAY,Color.DKGRAY, Color.DKGRAY));
        pie.addSeries(s4, new SegmentFormatter(
                Color.BLUE, Color.DKGRAY,Color.DKGRAY, Color.DKGRAY));
    }
}
