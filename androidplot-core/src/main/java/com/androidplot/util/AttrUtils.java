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
import android.util.*;

import com.androidplot.ui.*;
import com.androidplot.ui.Size;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.StepModel;

/**
 * Methods for applying styleable attributes.
 *
 */
public class AttrUtils {

    private static final String TAG = AttrUtils.class.getName();

    public static void configureInsets(TypedArray attrs, Insets insets,
            int topAttr, int bottomAttr, int leftAttr, int rightAttr) {
        insets.setTop(attrs.getDimension(topAttr, insets.getTop()));
        insets.setBottom(attrs.getDimension(bottomAttr, insets.getBottom()));
        insets.setLeft(attrs.getDimension(leftAttr, insets.getLeft()));
        insets.setRight(attrs.getDimension(rightAttr, insets.getRight()));
    }

    /**
     * Configure a {@link Paint} instance used for drawing text from xml attrs.
     * @param attrs
     * @param paint
     * @param colorAttr
     * @param textSizeAttr
     */
    public static void configureTextPaint(TypedArray attrs, Paint paint,
            int colorAttr, int textSizeAttr) {
        configureTextPaint(attrs, paint, colorAttr, textSizeAttr, null);
    }

    /**
     * Configure a {@link Paint} instance used for drawing text from xml attrs.
     * @param attrs
     * @param paint
     * @param colorAttr
     * @param textSizeAttr
     * @param alignAttr
     */
    public static void configureTextPaint(TypedArray attrs, Paint paint, int colorAttr,
            int textSizeAttr, Integer alignAttr) {
        if(attrs != null) {
            setColor(attrs, paint, colorAttr);
            setTextSize(attrs, paint, textSizeAttr);

            if(alignAttr != null && attrs.hasValue(alignAttr)) {
                configureTextAlign(attrs, paint, alignAttr);
            }
        }
    }

    /**
     * Configure {@link Paint} text alignment from xml attrs.
     * @param attrs
     * @param paint
     * @param alignAttr
     */
    public static void configureTextAlign(TypedArray attrs, Paint paint, int alignAttr) {
        if (attrs != null) {
            //if(attrs.hasValue(alignAttr)) {
            final Paint.Align alignment = Paint.Align.values()
                    [attrs.getInt(alignAttr, paint.getTextAlign().ordinal())];
            paint.setTextAlign(alignment);
            //}
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

    public static void setColor(TypedArray attrs, Paint paint, int attrId) {
        if(paint == null) {
            Log.w(TAG, "Attempt to configure null Paint property for attrId: " + attrId);
        } else {
            paint.setColor(attrs.getColor(attrId, paint.getColor()));
        }
    }

    public static void setTextSize(TypedArray attrs, Paint paint, int attrId) {
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

        final float value = getIntFloatDimenValue(attrs, valueAttr, model.getValue()).floatValue();
        final SizeMode sizeMode =
                getSizeLayoutType(attrs, typeAttr, model.getLayoutType());

        model.set(value, sizeMode);
    }

    private static SizeMode getSizeLayoutType(TypedArray attrs, int attr, SizeMode defaultValue) {
        return SizeMode.values()[attrs.getInt(attr, defaultValue.ordinal())];
    }

    public static void configureWidget(TypedArray attrs, Widget widget, int heightSizeLayoutTypeAttr, int heightAttr,
                                       int widthSizeLayoutTypeAttr, int widthAttr, int xLayoutStyleAttr,
                                       int xLayoutValueAttr, int yLayoutStyleAttr, int yLayoutValueAttr,
                                       int anchorPositionAttr, int visibilityAttr) {
        if(attrs != null) {
            configureSize(attrs, widget.getSize(), heightSizeLayoutTypeAttr,
                    heightAttr, widthSizeLayoutTypeAttr, widthAttr);
            configurePositionMetrics(attrs, widget.getPositionMetrics(), xLayoutStyleAttr, xLayoutValueAttr,
                    yLayoutStyleAttr, yLayoutValueAttr, anchorPositionAttr);
            widget.setVisible(attrs.getBoolean(visibilityAttr, widget.isVisible()));
        }
    }

    public static void configureWidgetRotation(TypedArray attrs, Widget widget, int rotationAttr) {
        if(attrs != null) {
            widget.setRotation(getWidgetRotation(attrs, rotationAttr, Widget.Rotation.NONE));
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
    public static void configurePositionMetrics(TypedArray attrs, PositionMetrics metrics, int xLayoutStyleAttr,
                                                int xLayoutValueAttr, int yLayoutStyleAttr, int yLayoutValueAttr,
                                                int anchorPositionAttr) {
        if(attrs != null && metrics != null) {
            metrics.getXPositionMetric().set(
                    getIntFloatDimenValue(attrs, xLayoutValueAttr, metrics.getXPositionMetric().getValue()).floatValue(),
                    getXLayoutStyle(attrs, xLayoutStyleAttr, metrics.getXPositionMetric().getLayoutType()));

            metrics.getYPositionMetric().set(
                    getIntFloatDimenValue(attrs, yLayoutValueAttr, metrics.getYPositionMetric().getValue()).floatValue(),
                    getYLayoutStyle(attrs, yLayoutStyleAttr, metrics.getYPositionMetric().getLayoutType()));
            metrics.setAnchor(getAnchorPosition(attrs, anchorPositionAttr, metrics.getAnchor()));
        }
    }

    /**
     * Convenience method to retrieve values from xml that can be entered as a int, float or dimen.
     * @param attrs
     * @param valueAttr
     * @param defaultValue
     * @return
     */
    private static Number getIntFloatDimenValue(TypedArray attrs, int valueAttr, Number defaultValue) {
        Number result = defaultValue;
        if(attrs != null && attrs.hasValue(valueAttr)) {
            final int valueType = attrs.peekValue(valueAttr).type;
            if (valueType == TypedValue.TYPE_DIMENSION) {
                result = attrs.getDimension(valueAttr, defaultValue.floatValue());
            } else if(valueType == TypedValue.TYPE_INT_DEC) {
                result = attrs.getInt(valueAttr, defaultValue.intValue());
            } else if (valueType == TypedValue.TYPE_FLOAT) {
                result = attrs.getFloat(valueAttr, defaultValue.floatValue());
            } else {
                throw new IllegalArgumentException("Invalid value type - must be int, float or dimension.");
            }
        }
        return result;
    }

    private static HorizontalPositioning getXLayoutStyle(TypedArray attrs, int attr, HorizontalPositioning defaultValue) {
        return HorizontalPositioning.values()[attrs.getInt(attr, defaultValue.ordinal())];
    }

    private static VerticalPositioning getYLayoutStyle(TypedArray attrs, int attr, VerticalPositioning defaultValue) {
        return VerticalPositioning.values()[attrs.getInt(attr, defaultValue.ordinal())];
    }

    private static Widget.Rotation getWidgetRotation(TypedArray attrs, int attr, Widget.Rotation defaultValue) {
        return Widget.Rotation.values()[attrs.getInt(attr, defaultValue.ordinal())];
    }

    private static Anchor getAnchorPosition(TypedArray attrs, int attr, Anchor defaultValue) {
        return Anchor.values()[attrs.getInt(attr, defaultValue.ordinal())];
    }

    public static void configureStep(TypedArray attrs, StepModel model, int stepModeAttr, int stepValueAttr) {
        if(attrs != null) {
            model.setMode(StepMode.values()[attrs.getInt(stepModeAttr, model.getMode().ordinal())]);
            model.setValue(getIntFloatDimenValue(attrs, stepValueAttr, model.getValue()).doubleValue());
        }
    }
}
