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
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.androidplot.Plot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ListViewActivity extends Activity {
    private static final int NUM_PLOTS = 10;
    private static final int NUM_POINTS_PER_SERIES = 10;
    private static final int NUM_SERIES_PER_PLOT = 5;
    private ListView lv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_example);
        lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(new MyViewAdapter(getApplicationContext(), R.layout.listview_example_item, null));
    }

    class MyViewAdapter extends ArrayAdapter<View> {
        public MyViewAdapter(Context context, int resId, List<View> views) {
            super(context, resId, views);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View v = convertView;
            if (v == null) {
                v = inf.inflate(R.layout.listview_example_item, parent, false);
            }

            Plot p = (XYPlot) v.findViewById(R.id.xyplot);
            Random generator = new Random();

            for (int k = 0; k < NUM_SERIES_PER_PLOT; k++) {
                ArrayList<Number> nums = new ArrayList<Number>();
                for (int j = 0; j < NUM_POINTS_PER_SERIES; j++) {
                    nums.add(generator.nextFloat());
                }

                double rl = Math.random();
                double gl = Math.random();
                double bl = Math.random();

                double rp = Math.random();
                double gp = Math.random();
                double bp = Math.random();

                XYSeries series = new SimpleXYSeries(nums, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "S" + k);
                p.addSeries(series, new LineAndPointFormatter(
                        Color.rgb(new Double(rl * 255).intValue(), new Double(gl * 255).intValue(), new Double(bl * 255).intValue()),
                        Color.rgb(new Double(rp * 255).intValue(), new Double(gp * 255).intValue(), new Double(bp * 255).intValue()),
                        null, null));
            }
            return v;
        }
    }
}