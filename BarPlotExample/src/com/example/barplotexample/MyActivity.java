
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

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import com.androidplot.plot.bar.BarPlot;
import com.halfhp.android.widgets.barplotexample.R;

public class MyActivity extends Activity {

    private BarPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // android boilerplate stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        plot = (BarPlot) findViewById(R.id.barplot);

        // set the display title of our bar plot:
        plot.setTitle("BarPlot #1");

        // Create an instance of our datasource:
        SampleBarDataset data = new SampleBarDataset();

        // and position it to our plot:
        plot.setDataset(data);

        // Let's set the default fill color of our middle bars to blue:
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        plot.getFormat().setBarFillPaint(1, p);

        // our datasource also implements BarPlotFormatFilter so let's set it as our plot's
        // custom format filter as well:
        plot.setFormatFilter(data);

    }


}
