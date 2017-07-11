package com.androidplot.ui.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.TableModel;
import com.androidplot.util.FontUtils;
import com.androidplot.util.PixelUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Provides core functionality for displaying a legend widget within a {@link com.androidplot.Plot}.
 * @param <ItemT>
 */
public abstract class LegendWidget<ItemT extends LegendItem> extends Widget {

    private static final float DEFAULT_TEXT_SIZE_DP = 20;

    private TableModel tableModel;
    private Size iconSize;

    private Paint textPaint;
    private Paint iconBackgroundPaint;
    private Paint iconBorderPaint;

    private boolean drawIconBackgroundEnabled = true;
    private boolean drawIconBorderEnabled = true;

    private Comparator<ItemT> legendItemComparator;

    {
        textPaint = new Paint();
        textPaint.setColor(Color.LTGRAY);
        textPaint.setTextSize(PixelUtils.spToPix(DEFAULT_TEXT_SIZE_DP));
        textPaint.setAntiAlias(true);

        iconBackgroundPaint = new Paint();
        iconBackgroundPaint.setColor(Color.BLACK);

        iconBorderPaint = new Paint();
        iconBorderPaint.setColor(Color.TRANSPARENT);
        iconBorderPaint.setStyle(Paint.Style.STROKE);
    }


    public LegendWidget(@NonNull TableModel tableModel, @NonNull LayoutManager layoutManager,
                        @NonNull Size size, @NonNull Size iconSize) {
        super(layoutManager, size);
        setTableModel(tableModel);
        this.iconSize = iconSize;
    }

    @Override
    protected void doOnDraw(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        final List<ItemT> items = getLegendItems();
        if(legendItemComparator != null) {
            Collections.sort(items, legendItemComparator);
        }
        final Iterator<RectF> cellRectIterator = tableModel.getIterator(widgetRect, items.size());
        for(ItemT item : items) {
            final RectF cellRect = cellRectIterator.next();
            final RectF iconRect = getIconRect(cellRect);
            beginDrawingCell(canvas, iconRect);
            drawItem(canvas, iconRect, item);
            finishDrawingCell(canvas, cellRect, iconRect, item);
        }
    }

    protected void drawItem(@NonNull Canvas canvas,  @NonNull RectF iconRect, @NonNull ItemT item) {
        drawIcon(canvas, iconRect, item);
    }

    /**
     * Draw the icon representing the legend item
     * @param canvas
     * @param iconRect The space to be occupied by the icon.
     * @param item
     */
    protected abstract void drawIcon(@NonNull Canvas canvas, @NonNull RectF iconRect, @NonNull ItemT item);

    /**
     *
     * @return The list of legend items to be drawn.  This is used to calculate table dimensions etc.
     */
    protected abstract List<ItemT> getLegendItems();

    private RectF getIconRect(RectF cellRect) {
        float cellRectCenterY = cellRect.top + (cellRect.height()/2);
        RectF iconRect = iconSize.getRectF(cellRect);

        // center the icon rect vertically
        float centeredIconOriginY = cellRectCenterY - (iconRect.height()/2);
        iconRect.offsetTo(cellRect.left + 1, centeredIconOriginY);
        return iconRect;
    }

    /**
     * Done at the start of rendering a new cell.  Whatever is drawn here will be beneath the rest
     * of the cell content; typically used to draw backgrounds.
     * @param canvas
     * @param iconRect
     */
    protected void beginDrawingCell(Canvas canvas, RectF iconRect) {

        if(drawIconBackgroundEnabled && iconBackgroundPaint != null) {
            canvas.drawRect(iconRect, iconBackgroundPaint);
        }
    }

    /**
     * Done at the end of rendering a new cell.  Whatever is drawn here will be on top of
     * the rest of the cell content; typically used to draw borders and text.
     * @param canvas
     * @param cellRect
     * @param iconRect
     * @param legendItem
     */
    protected void finishDrawingCell(Canvas canvas, RectF cellRect, RectF iconRect, LegendItem legendItem) {

        if(drawIconBorderEnabled && iconBorderPaint != null) {
            canvas.drawRect(iconRect, iconBorderPaint);
        }

        float centeredTextOriginY = getRectCenterY(cellRect) + (FontUtils.getFontHeight(textPaint)/2);

        if (textPaint.getTextAlign().equals(Paint.Align.RIGHT)) {
            canvas.drawText(legendItem.getTitle(), iconRect.left - 2, centeredTextOriginY, textPaint);
        } else {
            canvas.drawText(legendItem.getTitle(), iconRect.right + 2, centeredTextOriginY, textPaint);
        }
    }

    protected static float getRectCenterY(RectF cellRect) {
        return cellRect.top + (cellRect.height()/2);
    }

    public synchronized void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
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

    public Size getIconSize() {
        return iconSize;
    }

    public void setIconSize(Size iconSize) {
        this.iconSize = iconSize;
    }

    public Comparator<ItemT> getLegendItemComparator() {
        return legendItemComparator;
    }

    /**
     * Set a scheme for sorting the display order or legend items.  By default no sorting is applied
     * and {@link com.androidplot.Series} items typically appear in the order which the series was
     * added to the {@link com.androidplot.Plot}.
     * @param legendItemComparator
     */
    public void setLegendItemComparator(Comparator<ItemT> legendItemComparator) {
        this.legendItemComparator = legendItemComparator;
    }
}
