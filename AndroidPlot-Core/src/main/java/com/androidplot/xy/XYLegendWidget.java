package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.SeriesAndFormatterList;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.layout.TableModel;
import com.androidplot.ui.widget.Widget;
import com.androidplot.ui.layout.SizeMetrics;

public class XYLegendWidget extends Widget {
    public static final int ICON_WIDTH_DEFAULT = 10;
    public static final int ICON_HEIGHT_DEFAULT = 10;

    private XYPlot plot;
    private float iconWidth = 12;
    private Paint textPaint;
    private TableModel tableModel;
    private boolean drawIconBackgroundEnabled = true;
    private boolean drawIconBorderEnabled = false;

    private RectF iconRect = new RectF(0, 0, ICON_WIDTH_DEFAULT, ICON_HEIGHT_DEFAULT);

    {
        textPaint = new Paint();
        textPaint.setColor(Color.LTGRAY);
        textPaint.setAntiAlias(true);
    }

    public XYLegendWidget(XYPlot plot, SizeMetrics sizeMetrics, TableModel tableModel) {
        super(sizeMetrics);
        this.plot = plot;
        setTableModel(tableModel);
    }

    public synchronized void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    private void drawCell(Canvas canvas, XYSeriesRenderer renderer, XYSeriesFormatter formatter, RectF cellRect, String seriesTitle) {

        iconRect.offsetTo(cellRect.left, cellRect.top);

        if(drawIconBackgroundEnabled) {
            canvas.drawRect(iconRect, plot.getGraphWidget().getGridBackgroundPaint());
            //bgPaint = plot.getGraphWidget().getGridBackgroundPaint();
        }



                renderer.drawLegendIcon(
                        canvas,
                        iconRect,
                        seriesTitle,
                        formatter);
                canvas.drawText(seriesTitle, iconRect.left + iconWidth, iconRect.bottom, textPaint);

        if(drawIconBorderEnabled) {
            canvas.drawRect(iconRect, plot.getGraphWidget().getGridLinePaint());
        }
    }

    @Override
    protected synchronized void doOnDraw(Canvas canvas, RectF widgetRect) {
        // TODO: a good amount of iterating could be avoided if
        // we create a temporary list of all the legend items up here.
        if(plot.isEmpty()) {
            return;
        }

        int seriesCount = 0;
        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            seriesCount += plot.getSeriesAndFormatterListForRenderer(renderer.getClass()).size();
        }

        TableModel.TableModelIterator it = tableModel.getIterator(widgetRect, seriesCount);
        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            SeriesAndFormatterList<XYSeries,XYSeriesFormatter> sfList = plot.getSeriesAndFormatterListForRenderer(renderer.getClass());

            // maxIndex is only used if it has been determined.
            // if it is 0 then it could not be determined.
            for(int i = 0; i < sfList.size() && it.hasNext(); i++) {
                RectF cellRect = it.next();
                drawCell(canvas, renderer, sfList.getFormatter(i), cellRect, sfList.getSeries(i).getTitle());
            }
        }
    }

    public Paint getTextPaint() {
        return textPaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public boolean isDrawIconBackgroundEnabled() {
        return drawIconBackgroundEnabled;
    }

    public void setDrawIconBackgroundEnabled(boolean drawIconBackgroundEnabled) {
        this.drawIconBackgroundEnabled = drawIconBackgroundEnabled;
    }

    public boolean isDrawIconBorderEnabled() {
        return drawIconBorderEnabled;
    }

    public void setDrawIconBorderEnabled(boolean drawIconBorderEnabled) {
        this.drawIconBorderEnabled = drawIconBorderEnabled;
    }

    public TableModel getTableModel() {
        return tableModel;
    }
}
