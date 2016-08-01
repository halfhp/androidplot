/*
 * Copyright 2015 AndroidPlot.com
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
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

public class StepChartExampleActivity extends Activity
{

    private XYPlot mySimpleXYPlot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_chart_example);

        // initialize our XYPlot reference:
        mySimpleXYPlot = (XYPlot) findViewById(R.id.stepChartExamplePlot);

        // y-vals to plot:
        Number[] series1Numbers = {1, 2, 3, 4, 2, 3, 4, 2, 2, 2, 3, 4, 2, 3, 2, 2};
        // create our series from our array of nums:
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Thread #1");


        final int screenHeightPx = getWindowManager().getDefaultDisplay().getHeight();
        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, screenHeightPx, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));

        StepFormatter stepFormatter  = new StepFormatter(Color.WHITE, Color.BLUE);
        stepFormatter.setVertexPaint(null); // don't draw individual points
        stepFormatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(3));

        stepFormatter.getLinePaint().setAntiAlias(false);
        stepFormatter.setFillPaint(lineFill);
        mySimpleXYPlot.addSeries(series2, stepFormatter);

        // adjust the domain/range ticks to make more sense; label per line for range and label per 5 ticks domain:
        mySimpleXYPlot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);
        mySimpleXYPlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        mySimpleXYPlot.setLinesPerRangeLabel(1);
        mySimpleXYPlot.setLinesPerDomainLabel(5);

        // get rid of decimal points in our domain labels:
        mySimpleXYPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("0"));

        // create a custom getFormatter to draw our state names as range tick labels:
        mySimpleXYPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo,
                            FieldPosition pos) {
                        Number num = (Number) obj;
                        switch (num.intValue()) {
                            case 1:
                                toAppendTo.append("Init");
                                break;
                            case 2:
                                toAppendTo.append("Idle");
                                break;
                            case 3:
                                toAppendTo.append("Recv");
                                break;
                            case 4:
                                toAppendTo.append("Send");
                                break;
                            default:
                                toAppendTo.append("Unknown");
                                break;
                        }
                        return toAppendTo;
                    }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }
}
