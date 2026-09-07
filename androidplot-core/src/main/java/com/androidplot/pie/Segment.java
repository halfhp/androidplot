// SPDX-License-Identifier: Apache-2.0

package com.androidplot.pie;

import com.androidplot.Series;

/**
 * An implementation of Series representing a segment in a pie chart.
 */
public class Segment implements Series {

    private String title;

    private Number value;

    public Segment(String title, Number value) {
        this.title = title;
        this.setValue(value);
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }
}
