package com.androidplot.ui.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.TextOrientation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TextLabelWidgetTest extends AndroidplotTest {

    @Mock
    LayoutManager layoutManager;

    @Mock
    Size size;

    @Mock
    Canvas canvas;

    @Mock
    RectF rectF;

    private TextLabelWidget textLabelWidget;

    @Before
    public void before() {
        textLabelWidget = spy(new TextLabelWidget(layoutManager, size));
    }

    @Test
    public void onMetricsChanged_invokesPack_ifAutopackEnabled() {
        textLabelWidget.setAutoPackEnabled(true);
        textLabelWidget.onMetricsChanged(size, size);
        verify(textLabelWidget, times(2)).pack();
    }

    @Test
    public void onMetricsChanged_doesNotInvokePack_ifAutopackDisabled() {
        textLabelWidget.setAutoPackEnabled(false);
        textLabelWidget.onMetricsChanged(size, size);
        verify(textLabelWidget, never()).pack();
    }

    @Test
    public void doOnDraw_rotatesThenDraws_ifVerticalAscending() {
        textLabelWidget.setText("this is a test");
        textLabelWidget.setOrientation(TextOrientation.VERTICAL_ASCENDING);
        textLabelWidget.doOnDraw(canvas, rectF);

        InOrder inOrder = inOrder(canvas);
        inOrder.verify(canvas).rotate(-90);
        inOrder.verify(canvas).drawText(eq(textLabelWidget.getText()), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void doOnDraw_rotatesThenDraws_ifVerticalDescending() {
        textLabelWidget.setText("this is a test");
        textLabelWidget.setOrientation(TextOrientation.VERTICAL_DESCENDING);
        textLabelWidget.doOnDraw(canvas, rectF);

        InOrder inOrder = inOrder(canvas);
        inOrder.verify(canvas).rotate(90);
        inOrder.verify(canvas).drawText(eq(textLabelWidget.getText()), anyFloat(), anyFloat(), any(Paint.class));
    }
}
