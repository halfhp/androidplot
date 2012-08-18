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

        Button startOrSensorExButton = (Button)findViewById(R.id.startOrSensorExButton);
        startOrSensorExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, OrientationSensorExampleActivity.class));
                //setContentView(R.layout.orientation_sensor_example);
            }
        });

        Button startStepChartExButton = (Button)findViewById(R.id.startStepChartExButton);
        startStepChartExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, StepChartExampleActivity.class));
                //setContentView(R.layout.orientation_sensor_example);
            }
        });

        //startXyRegionExampleButton
        Button startXyRegionExampleButton = (Button)findViewById(R.id.startXyRegionExampleButton);
        startXyRegionExampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, XYRegionExampleActivity.class));
                //setContentView(R.layout.orientation_sensor_example);
            }
        });


        Button listViewExButton = (Button)findViewById(R.id.startXyListViewExButton);
        listViewExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, ListViewActivity.class));
                //setContentView(R.layout.orientation_sensor_example);
            }
        });


    }
}
