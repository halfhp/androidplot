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
import android.graphics.*;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.xy.*;

import java.util.Arrays;

/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class SimplePieChartActivity extends Activity
{

    private TextView donutSizeTextView;
    private SeekBar donutSizeSeekBar;

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


        donutSizeSeekBar = (SeekBar) findViewById(R.id.donutSizeSeekBar);

        donutSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pie.getRenderer(PieRenderer.class).setDonutSize(seekBar.getProgress()/100f,
                        PieRenderer.DonutMode.PERCENT);
                pie.redraw();
                updateDonutText();
            }
        });

        donutSizeTextView = (TextView) findViewById(R.id.donutSizeTextView);
        updateDonutText();

        s1 = new Segment("s1", 10);
        s2 = new Segment("s2", 1);
        s3 = new Segment("s3", 10);
        s4 = new Segment("s4", 10);

        EmbossMaskFilter emf = new EmbossMaskFilter(new float[]{1, 1, 1}, 0.4f, 10, 8.2f);


        SegmentFormatter sf1 = new SegmentFormatter(
                        Color.rgb(200, 150, 150), Color.BLACK);

        sf1.getFillPaint().setMaskFilter(emf);

        SegmentFormatter sf2 = new SegmentFormatter(
                        Color.rgb(150, 200, 150), Color.BLACK);

        sf2.getFillPaint().setMaskFilter(emf);

        SegmentFormatter sf3 = new SegmentFormatter(
                        Color.rgb(150, 150, 200), Color.BLACK);

        sf3.getFillPaint().setMaskFilter(emf);

        SegmentFormatter sf4 = new SegmentFormatter(
                        Color.BLUE, Color.BLACK);

        sf4.getFillPaint().setMaskFilter(emf);

        pie.addSeries(s1, sf1);
        pie.addSeries(s2, sf2);
        pie.addSeries(s3, sf3);
        pie.addSeries(s4, sf4);
    }

    protected void updateDonutText() {
        donutSizeTextView.setText(donutSizeSeekBar.getProgress() + "%");
    }
}
