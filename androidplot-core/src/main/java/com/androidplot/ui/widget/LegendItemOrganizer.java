package com.androidplot.ui.widget;

import java.util.List;


public interface LegendItemOrganizer<LegendItemT extends  LegendItem> {

    void organize(List<LegendItemT> items);
}
