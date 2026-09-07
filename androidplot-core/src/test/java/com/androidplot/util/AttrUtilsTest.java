// SPDX-License-Identifier: Apache-2.0
package com.androidplot.util;

import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.TypedValue;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.Insets;
import com.androidplot.ui.PositionMetrics;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.StepModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Edge cases of {@link AttrUtils} that cannot be reached through a real AttributeSet
 * (null guards, unsupported value types); the happy paths are covered end to end by
 * {@link com.androidplot.xy.XYPlotAttrsTest}.
 */
public class AttrUtilsTest extends AndroidplotTest {

    private static final int ATTR_A = 1;
    private static final int ATTR_B = 2;
    private static final int ATTR_C = 3;
    private static final int ATTR_D = 4;

    @Mock
    TypedArray attrs;

    @Before
    public void setUp() {
        // behave like an empty attribute set: every lookup returns its default
        final Answer<Object> returnDefault = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                return invocation.getArgument(1);
            }
        };
        when(attrs.getBoolean(anyInt(), anyBoolean())).thenAnswer(returnDefault);
        when(attrs.getInt(anyInt(), anyInt())).thenAnswer(returnDefault);
        when(attrs.getFloat(anyInt(), anyFloat())).thenAnswer(returnDefault);
        when(attrs.getDimension(anyInt(), anyFloat())).thenAnswer(returnDefault);
        when(attrs.getColor(anyInt(), anyInt())).thenAnswer(returnDefault);
    }

    @Test
    public void nullAttrs_areIgnored() {
        final Paint paint = new Paint();
        paint.setColor(0xFF123456);
        paint.setTextSize(11);
        paint.setStrokeWidth(3);
        AttrUtils.configureTextPaint(null, paint, ATTR_A, ATTR_B);
        AttrUtils.configureTextPaint(null, paint, ATTR_A, ATTR_B, ATTR_C);
        AttrUtils.configureTextAlign(null, paint, ATTR_A);
        AttrUtils.configureLinePaint(null, paint, ATTR_A, ATTR_B);
        assertEquals(0xFF123456, paint.getColor());
        assertEquals(11f, paint.getTextSize(), 0);
        assertEquals(3f, paint.getStrokeWidth(), 0);

        final Size size = new Size(1, SizeMode.ABSOLUTE, 2, SizeMode.ABSOLUTE);
        AttrUtils.configureSize(null, size, ATTR_A, ATTR_B, ATTR_C, ATTR_D);
        assertEquals(1f, size.getHeight().getValue(), 0);
        assertEquals(2f, size.getWidth().getValue(), 0);

        final PositionMetrics metrics = new PositionMetrics(1, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                2, VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.CENTER);
        AttrUtils.configurePositionMetrics(null, metrics, ATTR_A, ATTR_B, ATTR_C, ATTR_D, ATTR_A);
        assertEquals(Anchor.CENTER, metrics.getAnchor());

        final StepModel step = new StepModel(StepMode.INCREMENT_BY_VAL, 5);
        AttrUtils.configureStep(null, step, ATTR_A, ATTR_B);
        assertEquals(StepMode.INCREMENT_BY_VAL, step.getMode());
        assertEquals(5.0, step.getValue(), 0);
    }

    @Test
    public void configurePositionMetrics_nullMetrics_isIgnored() {
        AttrUtils.configurePositionMetrics(attrs, null, ATTR_A, ATTR_B, ATTR_C, ATTR_D, ATTR_A);
        verifyNoInteractions(attrs);
    }

    @Test
    public void setColor_nullPaint_isIgnored() {
        AttrUtils.setColor(attrs, null, ATTR_A);
        verify(attrs, never()).getColor(anyInt(), anyInt());
    }

    @Test
    public void configureTextPaint_alignAttrWithoutValue_keepsAlignment() {
        final Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.RIGHT);
        when(attrs.hasValue(ATTR_C)).thenReturn(false);
        AttrUtils.configureTextPaint(attrs, paint, ATTR_A, ATTR_B, ATTR_C);
        assertEquals(Paint.Align.RIGHT, paint.getTextAlign());

        when(attrs.hasValue(ATTR_C)).thenReturn(true);
        when(attrs.getInt(eq(ATTR_C), anyInt())).thenReturn(Paint.Align.CENTER.ordinal());
        AttrUtils.configureTextPaint(attrs, paint, ATTR_A, ATTR_B, ATTR_C);
        assertEquals(Paint.Align.CENTER, paint.getTextAlign());
    }

    @Test
    public void configureInsets_appliesEachEdge() {
        final Insets insets = new Insets(1, 2, 3, 4);
        when(attrs.getDimension(eq(ATTR_A), anyFloat())).thenReturn(10f);
        when(attrs.getDimension(eq(ATTR_C), anyFloat())).thenReturn(30f);
        AttrUtils.configureInsets(attrs, insets, ATTR_A, ATTR_B, ATTR_C, ATTR_D);
        assertEquals(10f, insets.getTop(), 0);
        assertEquals(2f, insets.getBottom(), 0);
        assertEquals(30f, insets.getLeft(), 0);
        assertEquals(4f, insets.getRight(), 0);
    }

    @Test
    public void intFloatDimenValue_unsupportedType_throws() {
        final TypedValue stringValue = new TypedValue();
        stringValue.type = TypedValue.TYPE_STRING;
        when(attrs.hasValue(ATTR_B)).thenReturn(true);
        when(attrs.peekValue(ATTR_B)).thenReturn(stringValue);

        final StepModel step = new StepModel(StepMode.SUBDIVIDE, 5);
        try {
            AttrUtils.configureStep(attrs, step, ATTR_A, ATTR_B);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // ok
        }
        assertEquals(5.0, step.getValue(), 0);
    }

    @Test
    public void intFloatDimenValue_intType_usesGetInt() {
        final TypedValue intValue = new TypedValue();
        intValue.type = TypedValue.TYPE_INT_DEC;
        when(attrs.hasValue(ATTR_B)).thenReturn(true);
        when(attrs.peekValue(ATTR_B)).thenReturn(intValue);
        when(attrs.getInt(eq(ATTR_B), anyInt())).thenReturn(42);

        final StepModel step = new StepModel(StepMode.SUBDIVIDE, 5);
        AttrUtils.configureStep(attrs, step, ATTR_A, ATTR_B);
        assertEquals(42.0, step.getValue(), 0);
        verify(attrs, never()).getFloat(eq(ATTR_B), anyFloat());
        verify(attrs, never()).getDimension(eq(ATTR_B), anyFloat());
    }
}
