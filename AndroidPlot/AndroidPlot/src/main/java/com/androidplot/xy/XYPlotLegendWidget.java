package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.SeriesAndFormatterList;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.Point;
import com.androidplot.ui.layout.SizeMetrics;

import java.util.List;
//import com.androidplot.xy.ui.widget.renderer.LineAndPointRenderBundle;


public class XYPlotLegendWidget extends Widget {

    public enum LayoutOrientation {
        HORIZONTAL,
        VERTICAL
    }

    private XYPlot plot;
    private float iconWidth = 12;

    private LayoutOrientation layoutOrientation;

    private Paint legendLabelPaint;

    {
        legendLabelPaint = new Paint();
        legendLabelPaint.setColor(Color.LTGRAY);
        legendLabelPaint.setAntiAlias(true);
    }

    public XYPlotLegendWidget(XYPlot plot, SizeMetrics sizeMetrics, LayoutOrientation orientation) {
        super(sizeMetrics);
        this.plot = plot;
        this.layoutOrientation = orientation;
    }

    @Override
    protected void doOnDraw(Canvas canvas, RectF widgetRect) {
        if(plot.isEmpty()) {
            // no data, so dont do anything
            return;
        }
        //float spacing = 5;
        //List<XYSeries> seriesList = plot.getSeriesListForRenderer(LineAndPointRenderer.class);
        //if(seriesList == null) {
        //    return;
        //}

        // NEW WAY
        RectF iconRect = new RectF(0, 0, 10, 10);
        iconRect.offsetTo(widgetRect.left + 12, widgetRect.top);
        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            SeriesAndFormatterList<XYSeries,XYSeriesFormatter> sfList = plot.getSeriesAndFormatterListForRenderer(renderer.getClass());

            for(int i = 0; i < sfList.size(); i++) {

                String text = sfList.getSeries(i).getTitle();
                renderer.drawLegendIcon(canvas, iconRect, text, sfList.getFormatter(i));
                canvas.drawText(text, iconRect.left + iconWidth, iconRect.bottom, legendLabelPaint);

                // shift up or over for next legend entry depending on orientation:
                switch (layoutOrientation) {
                    case HORIZONTAL:

                        iconRect.offset(legendLabelPaint.measureText(text) + iconRect.width() + 4, 0);
                        break;
                    case VERTICAL:
                        break;
                    default:
                        break;
                }
            }
        }


        // NEW WAY

        /*switch (layoutOrientation) {



            case HORIZONTAL: {
                float x = widgetRect.left + 12;
                float y = widgetRect.bottom - legendLabelPaint.getFontMetrics().descent;
                Point point = new Point(x, y);

               //plot.getSeriesListForRenderer(LineAndPointRenderer.class);


                for(XYSeries series : seriesList) {
                    if(series != null) {
                        LineAndPointFormatter lpFormatter = (LineAndPointFormatter) plot.getFormatter(series, LineAndPointRenderer.class);
                        point.setX(point.getX() + drawLegendItem(canvas, series.getTitle(), point, lpFormatter.getLinePaint(), lpFormatter.getVertexPaint()) + 4);
                    }
                }
                *//*
                for (int d = 0; d < plot.getDatasets().size(); d++) {
                    XYDatasetSeriesBundle bundle = plot.getDatasets().get(d);
                    point.setX(point.getX() + drawLegendItem(canvas, bundle.getTitle(), point, bundle.getFormat().getLinePaint(), bundle.getFormat().getVertexPaint()) + 4);
                }
                *//*
            }
            break;
            case VERTICAL:
                float x = widgetRect.left + 12;
                float y = widgetRect.top + 12;
                Point point = new Point(x, y);
                for(XYSeries series : seriesList) {
                    if(series == null) {
                        return;
                    }
                    LineAndPointFormatter lpFormatter = (LineAndPointFormatter) plot.getFormatter(series, LineAndPointRenderer.class);
                     drawLegendItem(canvas, series.getTitle(), point, lpFormatter.getLinePaint(), lpFormatter.getVertexPaint());
                    point.setY(point.getY() + 12);
                }
                *//*
                for (int d = 0; d < plot.getDatasets().size(); d++) {
                    XYDatasetSeriesBundle bundle = plot.getDatasets().get(d);
                    //point.setX();
                    drawLegendItem(canvas, bundle.getTitle(), point, bundle.getFormat().getLinePaint(), bundle.getFormat().getVertexPaint());
                    point.setY(point.getY() + 12);
                }
                *//*
                break;
            default:
                throw new IllegalArgumentException("Unknown LayoutOrientation: " + layoutOrientation);
        }
*/

    }

  /*  protected float drawLegendItem(Canvas canvas, String text, PointF point, Paint linePaint, Paint vertexPaint) {

        drawIcon(canvas, point, linePaint, vertexPaint);
        canvas.drawText(text, point.x + iconWidth, point.y, legendLabelPaint);

        float itemSize = iconWidth;
        itemSize += legendLabelPaint.measureText(text);
        return itemSize;
    }

    protected void drawIcon(Canvas canvas, PointF point, Paint linePaint, Paint vertexPaint) {
        // draw the line:

        // horizontal icon:
        //canvas.drawLine(point.x, point.y-5, point.x+iconWidth, point.y-5, linePaint);
        canvas.drawLine(point.x, point.y-5, point.x+iconWidth, point.y-5, linePaint);
        canvas.drawPoint(point.x + iconWidth/2, point.y-5, vertexPaint);


        
        // diagonal icon: (will take up less space than a horizontal line)
        //canvas.drawLine(point.getX(), point.getY()-(iconWidth*2), point.getX()+(iconWidth*2), point.getY(), linePaint);
        //canvas.drawPoint(point.getX() + iconWidth/2, point.getY()-(iconWidth/2), vertexPaint);
    }*/

}
