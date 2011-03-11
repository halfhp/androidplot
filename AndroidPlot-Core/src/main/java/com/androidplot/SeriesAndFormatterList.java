package com.androidplot;

import com.androidplot.series.Series;
import com.androidplot.ui.widget.formatter.Formatter;

import java.util.LinkedList;
import java.util.List;

public class SeriesAndFormatterList<SeriesType extends Series, FormatterType extends Formatter> {
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
