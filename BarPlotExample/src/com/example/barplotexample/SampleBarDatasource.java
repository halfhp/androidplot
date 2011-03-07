/*
Copyright 2010 Nick Fellows. All rights reserved.

Redistribution and use in source and binary forms, without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY Nick Fellows ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NICK FELLOWS OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors and should not be interpreted as representing official policies, either expressed
or implied, of Nick Fellows.
*/

package com.example.barplotexample;

import android.graphics.Color;
import com.androidplot.plot.bar.BarDataset;
import com.androidplot.plot.bar.BarFormat;
import com.androidplot.plot.bar.BarPlotFormatFilter;
import com.androidplot.series.DatasetChangeListener;


/**
 * A basic example of a BarPlot series.
 */
class SampleBarDataset implements BarDataset, BarPlotFormatFilter {

    private BarFormat bigValFormat;
    private BarFormat smallValFormat;

    {
        bigValFormat = new BarFormat("BigVal");
        bigValFormat.getBarFillPaint().setColor(Color.RED);
        smallValFormat = new BarFormat("SmallVal");
        smallValFormat.getBarFillPaint().setColor(Color.GRAY);
    }

    public int getBarsPerGroup() {
        return 3;
    }

    public String getGroupLabel(int i) {
        return null;
    }

    public String getBarLabel(int i) {
        return "bar " + i;
    }

    public Number getMinValue() {
        return 10;
    }

    public Number getMaxValue() {
        return ((getBarsPerGroup() - 1) * 10 * (getGroupCount() - 1)) + 10;
    }

    public Number getZeroPoint() {
        // let's arbitrarily use a zero point of 25, to make the plot look more interesting:
        return 25;
    }

    public Number getValue(int g, int b) {
        return (b * g * 10) + 10;
    }

    public int getGroupCount() {
        return 5;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public DomainOrder getDomainOrder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean addChangeListener(DatasetChangeListener datasetChangeListener) {
        return true;  // stubged: only dynamic datasources need to properly implement this
    }

    public boolean removeChangeListener(DatasetChangeListener datasetChangeListener) {
        return true;  // stubged: only dynamic datasources need to properly implement this
    }

    public void readLock() {
        // stubbed: race conditions arent really a concern with static datasources.
    }

    public void readUnlock() {
        // stubbed: race conditions arent really a concern with static datasources.
    }


    public BarFormat getUserBarFormat(int g, int b) {
        int v = (g * b * 10) + 10;

        // all values over 50 are drawn using the "bigVal" format
        if (v > 50) {
            return bigValFormat;

            // all values less than our zero point are drawn using the "smallVal" format
        } else if (v < getZeroPoint().intValue()) {
            return smallValFormat;

            // everything else uses defaults
        } else {
            return null;
        }
    }

    public int getUserBarFormatCount() {
        return 2;
    }

    // this method is used to step through all user defined "custom" formats
    // and position them to the legend section of the plot.  It doesnt matter how
    // these formats are indexed, but the return of f(i) should be consistent.

    public BarFormat getUserBarFormat(int i) {
        switch (i) {
            case 0:
                return bigValFormat;
            case 1:
                return smallValFormat;
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
