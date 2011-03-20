package com.androidplot.ui.widget;

import android.graphics.*;
import com.androidplot.ui.layout.*;
import com.androidplot.util.FontUtils;

public abstract class TextLabelWidget extends Widget {


    //private Plot plot;
    //private String label;
    private Paint labelPaint;

    private TextOrientationType orientation;

    {
        labelPaint = new Paint();
        labelPaint.setColor(Color.WHITE);
        labelPaint.setAntiAlias(true);
        labelPaint.setTextAlign(Paint.Align.CENTER);
    }

    public TextLabelWidget(SizeMetrics sizeMetrics) {
        this(sizeMetrics, TextOrientationType.HORIZONTAL);
    }

    public TextLabelWidget(SizeMetrics sizeMetrics, TextOrientationType orientation) {
        super(new SizeMetrics(0, SizeLayoutType.ABSOLUTE, 0, SizeLayoutType.ABSOLUTE));
        //this.plot = plot;
        //this.setWidth(labelPaint.measureText(plot.getTitle()));
        //this.setHeight(labelPaint.getFontMetrics().top);
        setSize(sizeMetrics);
        this.orientation = orientation;
    }

    protected abstract String getText();

    /**
     * Sets the dimensions of the widget to exactly contain the text contents
     */
    public void pack() {
        Rect size = FontUtils.getStringDimensions(getText(), getLabelPaint());
        if(size == null) {
            return;
        }
        switch(orientation) {
            case HORIZONTAL:
                this.setSize(new SizeMetrics(size.height(), SizeLayoutType.ABSOLUTE, size.width()+2, SizeLayoutType.ABSOLUTE));
                break;
            case VERTICAL_ASCENDING:
            case VERTICAL_DESCENDING:
                this.setSize(new SizeMetrics(size.width(), SizeLayoutType.ABSOLUTE, size.height()+2, SizeLayoutType.ABSOLUTE));
                break;
        }

    }

    @Override
    public void doOnDraw(Canvas canvas, RectF widgetRect) {
        //Path path = new Path();
        //Point start;
        //Point end;
        //Rect labelSize = new Rect();
        String label = getText();
        //labelPaint.getTextBounds(label,0, label.length(), labelSize);
        FontUtils.getStringDimensions(label, labelPaint);
        //float hOffset = 0;
        //float vOffset = labelSize.height()/2;
        float vOffset = labelPaint.getFontMetrics().descent;
        //PointF start;

        PointF start = LayoutManager.getAnchorCoordinates(widgetRect, AnchorPosition.CENTER);

        // BEGIN ROTATION CALCULATION
        int canvasState = canvas.save();
        try {
        canvas.translate(start.x, start.y);
        switch(orientation) {
            case HORIZONTAL:
                //start = LayoutManager.getAnchorCoordinates(widgetRect, AnchorPosition.CENTER);
                //start = LayoutManager.getAnchorCoordinates(widgetRect, AnchorPosition.LEFT_MIDDLE);
                //end = LayoutManager.getAnchorCoordinates(widgetRect, AnchorPosition.RIGHT_MIDDLE);
                //hOffset = ((widgetRect.width()-labelSize.width())/2);

                break;
            case VERTICAL_ASCENDING:
                //canvas.translate(start.getX(), start.getY());
                canvas.rotate(-90);
                //start = LayoutManager.getAnchorCoordinates(widgetRect, AnchorPosition.BOTTOM_MIDDLE);
                //end = LayoutManager.getAnchorCoordinates(widgetRect, AnchorPosition.TOP_MIDDLE);
                //hOffset = (widgetRect.height()-labelSize.width())/2;
                break;
            case VERTICAL_DESCENDING:
                canvas.rotate(90);
                //start = LayoutManager.getAnchorCoordinates(widgetRect, AnchorPosition.TOP_MIDDLE);
                //end = LayoutManager.getAnchorCoordinates(widgetRect, AnchorPosition.BOTTOM_MIDDLE);
                //hOffset = (widgetRect.height()-labelSize.width())/2;
                break;
            default:

                throw new UnsupportedOperationException("Orientation " + orientation + " not yet implemented for TextLabelWidget.");
        }
        canvas.drawText(label, 0, vOffset, labelPaint);
        } finally {
            canvas.restoreToCount(canvasState);
        }

        // END ROTATION CALCULATION

        //float bottomEdge = coords.getY() + size.getFontHeight();
        //Path path = new Path();
        //path.moveTo(start.getX(), start.getY());
        //path.lineTo(end.getX(), end.getY());
        //path.close();
        //canvas.drawPath(path, labelPaint);
        //float x = getPlotMarginLeft();
        //float y = getHeightPix(canvas.getFontHeight())-getPlotMarginBottom();

        //Paint.FontMetrics fMetrics = labelPaint.getFontMetrics();
         //vOffset = labelPaint.getFontMetrics().descent;
        //vOffset = 0;

        /*
        if(orientation == TextOrientationType.HORIZONTAL) {
            canvas.save();
            canvas.translate(start.getX(), start.getY());
            canvas.rotate(90);
            //canvas.drawText(label, start.getX(), widgetRect.bottom - vOffset, labelPaint);
            canvas.drawText(label, 0, 0, labelPaint);
            canvas.restore();

            //canvas.translate(-10, -10);
            //canvas.rotate(-90);
            //canvas.translate(-start.getX(), -start.getY());
            //canvas.drawLine(start.getX(), start.getY()+ fMetrics.ascent, end.getX(), end.getY() + fMetrics.ascent, labelPaint);
            //canvas.drawLine(start.getX(), start.getY()+ fMetrics.descent, end.getX(), end.getY() + fMetrics.descent, labelPaint);
            //canvas.drawLine(start.getX(), start.getY(), end.getX(), end.getY(), labelPaint);
        }

        canvas.drawPath(path, labelPaint);
        */
        //canvas.drawTextOnPath(label, path, -45, vOffset, labelPaint);

        //Point centerPoint = LayoutManager.getAnchorCoordinates(coords.getX(), coords.getY(), size.getWidth(), size.getFontHeight(), AnchorPosition.CENTER);

        //canvas.drawText(plot.getTitle(), centerPoint.getX(), centerPoint.getY(), labelPaint);
        //canvas.drawText(plot.getTitle(), 15, 15, labelPaint);
    }

    /*
    public Plot getPlot() {
        return plot;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }
    */

    public Paint getLabelPaint() {
        return labelPaint;
    }

    public void setLabelPaint(Paint labelPaint) {
        this.labelPaint = labelPaint;
    }

    public TextOrientationType getOrientation() {
        return orientation;
    }

    public void setOrientation(TextOrientationType orientation) {
        this.orientation = orientation;
    }
}
