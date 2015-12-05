/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.util;

import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.TypedValue;
import com.androidplot.ui.*;
import com.androidplot.ui.widget.Widget;

/**
 * Methods for applying styleable attributes.
 *
 */
public class AttrUtils {

    /**
     * Configure a {@link Paint} instance used for drawing text from xml attrs.
     * @param attrs
     * @param paint
     * @param colorAttr
     * @param textSizeAttr
     */
    public static void configureTextPaint(TypedArray attrs, Paint paint, int colorAttr, int textSizeAttr) {
        if(attrs != null) {
            setColor(attrs, paint, colorAttr);
            setTextSize(attrs, paint, textSizeAttr);
        }
    }

    /**
     * Configure a {@link Paint} instance used for drawing lines from xml attrs.
     * @param attrs
     * @param paint
     * @param colorAttr
     * @param strokeWidthAttr
     */
    public static void configureLinePaint(TypedArray attrs, Paint paint, int colorAttr, int strokeWidthAttr) {
        if(attrs != null) {
            setColor(attrs, paint, colorAttr);
            paint.setStrokeWidth(attrs.getDimension(strokeWidthAttr, paint.getStrokeWidth()));
        }
    }

    private static void setColor(TypedArray attrs, Paint paint, int attrId) {
        paint.setColor(attrs.getColor(attrId, paint.getColor()));
    }

    private static void setTextSize(TypedArray attrs, Paint paint, int attrId) {
        paint.setTextSize(attrs.getDimension(attrId, paint.getTextSize()));
    }

    /**
     * Configure a {@link BoxModelable} instance from xml attrs.
     * @param attrs
     * @param model
     * @param marginTop
     * @param marginBottom
     * @param marginLeft
     * @param marginRight
     * @param paddingTop
     * @param paddingBottom
     * @param paddingLeft
     * @param paddingRight
     */
    public static void configureBoxModelable(TypedArray attrs, BoxModelable model, int marginTop, int marginBottom,
                                         int marginLeft, int marginRight, int paddingTop, int paddingBottom,
                                         int paddingLeft, int paddingRight) {
        if(attrs != null) {
            model.setMargins(attrs.getDimension(marginLeft, model.getMarginLeft()),
                    attrs.getDimension(marginTop, model.getMarginTop()),
                    attrs.getDimension(marginRight, model.getMarginRight()),
                    attrs.getDimension(marginBottom, model.getMarginBottom()));

            model.setPadding(attrs.getDimension(paddingLeft, model.getPaddingLeft()),
                    attrs.getDimension(paddingTop, model.getPaddingTop()),
                    attrs.getDimension(paddingRight, model.getPaddingRight()),
                    attrs.getDimension(paddingBottom, model.getPaddingBottom()));
        }
    }

    /**
     * Configure a {@link Size} instance from xml attrs.
     * @param attrs
     * @param model
     * @param heightSizeLayoutTypeAttr
     * @param heightAttr
     * @param widthSizeLayoutTypeAttr
     * @param widthAttr
     */
    public static void configureSize(TypedArray attrs, Size model, int heightSizeLayoutTypeAttr, int heightAttr,
                                     int widthSizeLayoutTypeAttr, int widthAttr) {
        if(attrs != null) {
            configureSizeMetric(attrs, model.getHeight(), heightSizeLayoutTypeAttr, heightAttr);
            configureSizeMetric(attrs, model.getWidth(), widthSizeLayoutTypeAttr, widthAttr);
        }
    }

    private static void configureSizeMetric(TypedArray attrs, SizeMetric model, int typeAttr, int valueAttr) {

        final float value = getFloatOrDimenValue(attrs, valueAttr, model.getValue());
        final SizeLayoutType sizeLayoutType =
                getSizeLayoutType(attrs, typeAttr, model.getLayoutType());

        model.set(value, sizeLayoutType);

        /*if(attrs != null && attrs.hasValue(valueAttr)) {
            final int valueType = attrs.peekValue(valueAttr).type;

            final SizeLayoutType sizeLayoutType =
                    getSizeLayoutType(attrs, typeAttr, model.getLayoutType());

            if (valueType == TypedValue.TYPE_DIMENSION) {
                model.set(attrs.getDimension(valueAttr, model.getValue()), sizeLayoutType);
            } else if (valueType == TypedValue.TYPE_FLOAT) {
                model.set(attrs.getFloat(valueAttr, model.getValue()), sizeLayoutType);
            } else {
                throw new IllegalArgumentException("Invalid size metric value type - must be float or dimension.");
            }
        }*/
    }

    private static SizeLayoutType getSizeLayoutType(TypedArray attrs, int attr, SizeLayoutType defaultValue) {
        return SizeLayoutType.values()[attrs.getInt(attr, defaultValue.ordinal())];
    }

    public static void configureWidget(TypedArray attrs, Widget widget, int xLayoutStyleAttr, int xLayoutValueAttr,
                                       int yLayoutStyleAttr, int yLayoutValueAttr, int anchorPositionAttr,
                                       int visibilityAttr) {
        if(attrs != null) {
            configurePositionMetrics(attrs, widget.getPositionMetrics(), xLayoutStyleAttr, xLayoutValueAttr,
                    yLayoutStyleAttr, yLayoutValueAttr, anchorPositionAttr);
            widget.setVisible(attrs.getBoolean(visibilityAttr, widget.isVisible()));
        }
    }

    /**
     * Configure a {@link Widget} from xml attrs.
     * @param attrs
     * @param metrics
     * @param xLayoutStyleAttr
     * @param xLayoutValueAttr
     * @param yLayoutStyleAttr
     * @param yLayoutValueAttr
     * @param anchorPositionAttr
     */
    public static void configurePositionMetrics(TypedArray attrs, PositionMetrics metrics, int xLayoutStyleAttr, int xLayoutValueAttr,
                                       int yLayoutStyleAttr, int yLayoutValueAttr, int anchorPositionAttr) {
        if(attrs != null) {
            metrics.getXPositionMetric().set(
                    getFloatOrDimenValue(attrs, xLayoutValueAttr, metrics.getXPositionMetric().getValue()),
                    getXLayoutStyle(attrs, xLayoutStyleAttr, metrics.getXPositionMetric().getLayoutType()));

            metrics.getYPositionMetric().set(
                    getFloatOrDimenValue(attrs, yLayoutValueAttr, metrics.getYPositionMetric().getValue()),
                    getYLayoutStyle(attrs, yLayoutStyleAttr, metrics.getYPositionMetric().getLayoutType()));
            metrics.setAnchor(getAnchorPosition(attrs, anchorPositionAttr, metrics.getAnchor()));
        }
    }

    /**
     * Convenience method to retrieve float values from xml that can be entered as either a float or a dimen.
     * @param attrs
     * @param valueAttr
     * @param defaultValue
     * @return
     */
    private static float getFloatOrDimenValue(TypedArray attrs, int valueAttr, float defaultValue) {
        float result = defaultValue;
        if(attrs != null && attrs.hasValue(valueAttr)) {
            final int valueType = attrs.peekValue(valueAttr).type;
            if (valueType == TypedValue.TYPE_DIMENSION) {
                result = attrs.getDimension(valueAttr, defaultValue);
            } else if (valueType == TypedValue.TYPE_FLOAT) {
                result = attrs.getFloat(valueAttr, defaultValue);
            } else {
                throw new IllegalArgumentException("Invalid value type - must be float or dimension.");
            }
        }
        return result;
    }

    private static XLayoutStyle getXLayoutStyle(TypedArray attrs, int attr, XLayoutStyle defaultValue) {
        return XLayoutStyle.values()[attrs.getInt(attr, defaultValue.ordinal())];
    }

    private static YLayoutStyle getYLayoutStyle(TypedArray attrs, int attr, YLayoutStyle defaultValue) {
        return YLayoutStyle.values()[attrs.getInt(attr, defaultValue.ordinal())];
    }

    private static AnchorPosition getAnchorPosition(TypedArray attrs, int attr, AnchorPosition defaultValue) {
        return AnchorPosition.values()[attrs.getInt(attr, defaultValue.ordinal())];
    }
}
