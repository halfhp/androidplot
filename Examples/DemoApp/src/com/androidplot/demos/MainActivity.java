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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button startSimplePieExButton = (Button) findViewById(R.id.startSimplePieExButton);
        startSimplePieExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SimplePieChartActivity.class));
            }
        });

        Button startDynamicXYExButton = (Button)findViewById(R.id.startDynamicXYExButton);
        startDynamicXYExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DynamicXYPlotActivity.class));
            }
        });

        Button startSimpleXYExButton = (Button) findViewById(R.id.startSimpleXYExButton);
        startSimpleXYExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SimpleXYPlotActivity.class));
            }
        });

        Button startBarPlotExButton = (Button) findViewById(R.id.startBarPlotExButton);
        startBarPlotExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BarPlotExampleActivity.class));
            }
        });

        Button startOrSensorExButton = (Button) findViewById(R.id.startOrSensorExButton);
        startOrSensorExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrientationSensorExampleActivity.class));
            }
        });

        Button startTimeSeriesExButon = (Button)findViewById(R.id.startTimeSeriesExButton);
        startTimeSeriesExButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TimeSeriesActivity.class));
            }
        });

        Button startStepChartExButton = (Button)findViewById(R.id.startStepChartExButton);
        startStepChartExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StepChartExampleActivity.class));
            }
        });

        Button startScrollZoomExButton = (Button)findViewById(R.id.startScrollZoomButton);
        startScrollZoomExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TouchZoomExampleActivity.class));
            }
        });

        Button startXyRegionExampleButton = (Button)findViewById(R.id.startXyRegionExampleButton);
        startXyRegionExampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, XYRegionExampleActivity.class));
            }
        });


        Button listViewExButton = (Button)findViewById(R.id.startXyListViewExButton);
        listViewExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListViewActivity.class));
            }
        });

        Button startDualScaleExampleButton = (Button)findViewById(R.id.startDualScaleExampleButton);
        startDualScaleExampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DualScaleXYPlotExampleActivity.class));
            }
        });

        Button startXYPlotWithBgImgExampleButton = (Button)findViewById(R.id.startXYPlotWithBgImgExample);
        startXYPlotWithBgImgExampleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, XYPlotWithBgImgActivity.class));
                    }
                });

    }
}
