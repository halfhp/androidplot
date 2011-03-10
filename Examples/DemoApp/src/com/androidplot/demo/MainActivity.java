package com.androidplot.demo;

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

    }
}
