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

package com.androidplot.ui;

import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XPositionMetric;
import com.androidplot.xy.YLayoutStyle;
import com.androidplot.xy.YPositionMetric;

public class PositionMetrics implements Comparable<PositionMetrics> {

    private XPositionMetric xPositionMetric;
    private YPositionMetric yPositionMetric;
    private AnchorPosition anchor;
    private float layerDepth;

    public PositionMetrics(float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle, AnchorPosition anchor) {
        setxPositionMetric(new XPositionMetric(x, xLayoutStyle));
        setyPositionMetric(new YPositionMetric(y, yLayoutStyle));
        setAnchor(anchor);

    }


    public XPositionMetric getxPositionMetric() {
        return xPositionMetric;
    }

    public void setxPositionMetric(XPositionMetric xPositionMetric) {
        this.xPositionMetric = xPositionMetric;
    }

    public YPositionMetric getyPositionMetric() {
        return yPositionMetric;
    }

    public void setyPositionMetric(YPositionMetric yPositionMetric) {
        this.yPositionMetric = yPositionMetric;
    }

    public AnchorPosition getAnchor() {
        return anchor;
    }

    public void setAnchor(AnchorPosition anchor) {
        this.anchor = anchor;
    }

    @Override
    public int compareTo(PositionMetrics o) {
        if(this.layerDepth < o.layerDepth) {
            return -1;
        } else if(this.layerDepth == o.layerDepth) {
            return 0;
        } else {
            return 1;
        }
    }
}
