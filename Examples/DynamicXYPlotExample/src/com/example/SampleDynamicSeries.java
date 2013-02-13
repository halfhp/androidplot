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

package com.example;

import com.androidplot.series.XYSeries;

public class SampleDynamicSeries implements XYSeries {
    private SampleDynamicXYDatasource datasource;
    private int seriesIndex;
    private String title;

    public SampleDynamicSeries(SampleDynamicXYDatasource datasource, int seriesIndex, String title) {
        this.datasource = datasource;
        this.seriesIndex = seriesIndex;
        this.title = title;
    }
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int size() {
        return datasource.getItemCount(seriesIndex);
    }

    @Override
    public void onReadBegin() {}

    @Override
    public void onReadEnd() {}

    @Override
    public Number getX(int index) {
        return datasource.getX(seriesIndex, index);
    }

    @Override
    public Number getY(int index) {
        return datasource.getY(seriesIndex, index);
    }
}
