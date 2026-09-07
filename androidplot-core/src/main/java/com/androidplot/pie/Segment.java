// SPDX-License-Identifier: Apache-2.0

package com.androidplot.pie;

import com.androidplot.Series;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * An implementation of Series representing a segment in a pie chart.
 */
public class Segment implements Series {

    private String title;

    private Number value;

    public Segment(@Nullable String title, @NonNull Number value) {
        this.title = title;
        this.setValue(value);
    }

    @Override
    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @NonNull
    public Number getValue() {
        return value;
    }

    public void setValue(@NonNull Number value) {
        this.value = value;
    }
}
