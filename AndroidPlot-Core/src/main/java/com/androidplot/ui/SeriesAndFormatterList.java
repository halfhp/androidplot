/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
 */

package com.androidplot.ui;

import com.androidplot.series.Series;

import java.util.LinkedList;
import java.util.List;

/**
 * Associates a Series with a Formatter.
 * @param <SeriesType>
 * @param <FormatterType>
 */
public class SeriesAndFormatterList<SeriesType extends Series, FormatterType> {
    private LinkedList<SeriesType> seriesList;
    private LinkedList<FormatterType> formatterList;
    {
        seriesList = new LinkedList<SeriesType>();
        formatterList = new LinkedList<FormatterType>();
    }

    public boolean contains(SeriesType series) {
        return seriesList.contains(series);
    }

    public int size() {
        return seriesList.size();
    }

    public List<SeriesType> getSeriesList() {
        return seriesList;
    }

    public List<FormatterType> getFormatterList() {
        return formatterList;
    }

    public boolean add(SeriesType series, FormatterType formatter) {
        if(series == null || formatter == null) {
            throw new IllegalArgumentException("series and formatter must not be null.");
        }
        if(seriesList.contains(series)) {
            return false;
        }
        seriesList.add(series);
        formatterList.add(formatter);
        return true;
    }

    public boolean remove(SeriesType series) {
        int index = seriesList.indexOf(series);
        if(index < 0) {
            return false;
        }
        seriesList.remove(index);
        formatterList.remove(index);
        return true;
    }


    public FormatterType getFormatter(SeriesType series) {
        return formatterList.get(seriesList.indexOf(series));
    }

    public FormatterType getFormatter(int index) {
        return formatterList.get(index);
    }

    public SeriesType getSeries(int index) {
        return seriesList.get(index);
    }

    public FormatterType setFormatter(SeriesType series, FormatterType formatter) {
        return formatterList.set(seriesList.indexOf(series), formatter);
    }
}
