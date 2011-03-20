package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.SeriesAndFormatterList;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.layout.TableModel;
import com.androidplot.ui.widget.Widget;
import com.androidplot.ui.layout.SizeMetrics;
import com.androidplot.util.FontUtils;

public class XYLegendWidget extends Widget {
    ////public static final int ICON_WIDTH_DEFAULT = 10;
    //public static final int ICON_HEIGHT_DEFAULT = 10;

    private XYPlot plot;
    //private float iconWidth = 12;
    private Paint textPaint;
    private Paint iconBorderPaint;
    private TableModel tableModel;
    private boolean drawIconBackgroundEnabled = true;
    private boolean drawIconBorderEnabled = true;

    private SizeMetrics iconSizeMetrics;
    //private RectF iconRect = new RectF(0, 0, ICON_WIDTH_DEFAULT, ICON_HEIGHT_DEFAULT);

    {
        textPaint = new Paint();
        textPaint.setColor(Color.LTGRAY);
        textPaint.setAntiAlias(true);

        iconBorderPaint = new Paint();
        iconBorderPaint.setStyle(Paint.Style.STROKE);
    }

    public XYLegendWidget(XYPlot plot,
                          SizeMetrics widgetSizeMetrics,
                          TableModel tableModel,
                          SizeMetrics iconSizeMetrics) {
        super(widgetSizeMetrics);
        this.plot = plot;
        setTableModel(tableModel);
        this.iconSizeMetrics = iconSizeMetrics;
    }

    public synchronized void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    private void drawCell(Canvas canvas, XYSeriesRenderer renderer, XYSeriesFormatter formatter, RectF cellRect, String seriesTitle) {

        float cellRectCenterY = cellRect.top + (cellRect.height()/2);
        RectF iconRect = iconSizeMetrics.getRectF(cellRect);

        // center the icon rect vertically
        float centeredIconOriginY = cellRectCenterY - (iconRect.height()/2);
        iconRect.offsetTo(cellRect.left + 1, centeredIconOriginY);


        //RectF  iconRect = PixelUtils.sink(iconRect);

        if(drawIconBackgroundEnabled) {
            canvas.drawRect(iconRect, plot.getGraphWidget().getGridBackgroundPaint());
            //bgPaint = plot.getGraphWidget().getGridBackgroundPaint();
        }

        // center the label text vetically
        float centeredTextOriginY = cellRectCenterY + (FontUtils.getFontHeight(textPaint)/2);



                renderer.drawLegendIcon(
                        canvas,
                        iconRect,
                        seriesTitle,
                        formatter);
                canvas.drawText(seriesTitle, iconRect.right + 2, centeredTextOriginY, textPaint);

        if(drawIconBorderEnabled) {
            iconBorderPaint.setColor(plot.getGraphWidget().getGridBackgroundPaint().getColor());
            canvas.drawRect(iconRect, iconBorderPaint);
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

    public SizeMetrics getIconSizeMetrics() {
        return iconSizeMetrics;
    }

    /**
     * Set the size of each legend's icon.  Note that when using relative sizing,
     * the size is calculated against the countaining cell's size, not the plot's size.
     * @param iconSizeMetrics
     */
    public void setIconSizeMetrics(SizeMetrics iconSizeMetrics) {
        this.iconSizeMetrics = iconSizeMetrics;
    }
}
