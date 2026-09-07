// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;
import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.util.PixelUtils;

/**
 * PieChart with tap-to-select segments, a donut-size seek bar and an intro sweep animation.
 */
public class SimplePieChartActivity extends Activity {

    private static final int SELECTED_SEGMENT_OFFSET = 50;

    private PieChart pie;
    private TextView donutSizeTextView;
    private SeekBar donutSizeSeekBar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_chart);
        pie = findViewById(R.id.mySimplePieChart);

        EmbossMaskFilter emf = new EmbossMaskFilter(new float[]{1, 1, 1}, 0.4f, 10, 8.2f);
        pie.addSegment(new Segment("s1", 3), segmentFormatter(R.xml.pie_segment_formatter1, emf));
        pie.addSegment(new Segment("s2", 1), segmentFormatter(R.xml.pie_segment_formatter2, emf));
        pie.addSegment(new Segment("s3", 7), segmentFormatter(R.xml.pie_segment_formatter3, emf));
        pie.addSegment(new Segment("s4", 9), segmentFormatter(R.xml.pie_segment_formatter4, emf));

        pie.getLegend().setVisible(true);
        float padding = PixelUtils.dpToPix(30);
        pie.getPie().setPadding(padding, padding, padding, padding);
        pie.getBorderPaint().setColor(Color.TRANSPARENT);
        pie.getBackgroundPaint().setColor(Color.TRANSPARENT);

        // detect segment clicks:
        pie.setOnTouchListener((v, event) -> {
            onPieTouched(new PointF(event.getX(), event.getY()));
            return false;
        });

        bindDonutSeekBar();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupIntroAnimation();
    }

    private SegmentFormatter segmentFormatter(int xmlId, EmbossMaskFilter emf) {
        SegmentFormatter formatter = new SegmentFormatter(this, xmlId);
        formatter.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);
        formatter.getFillPaint().setMaskFilter(emf);
        return formatter;
    }

    // toggles the tapped segment's offset, deselecting all others
    private void onPieTouched(PointF point) {
        if (!pie.getPie().containsPoint(point)) {
            return;
        }
        Segment tapped = pie.getRenderer(PieRenderer.class).getContainingSegment(point);
        if (tapped == null) {
            return;
        }
        boolean wasSelected = pie.getFormatter(tapped, PieRenderer.class).getOffset() != 0;
        for (Segment segment : pie.getRegistry().getSeriesList()) {
            SegmentFormatter formatter = pie.getFormatter(segment, PieRenderer.class);
            formatter.setOffset(segment == tapped && !wasSelected ? SELECTED_SEGMENT_OFFSET : 0);
        }
        pie.redraw();
    }

    private void bindDonutSeekBar() {
        donutSizeTextView = findViewById(R.id.donutSizeTextView);
        donutSizeSeekBar = findViewById(R.id.donutSizeSeekBar);
        donutSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pie.getRenderer(PieRenderer.class).setDonutSize(seekBar.getProgress() / 100f,
                        PieRenderer.DonutMode.PERCENT);
                pie.redraw();
                updateDonutText();
            }
        });
        updateDonutText();
    }

    private void updateDonutText() {
        donutSizeTextView.setText(donutSizeSeekBar.getProgress() + "%");
    }

    // PieRenderer.setExtentDegs drives the animation: sweep the pie open from 0 to 360 degrees
    private void setupIntroAnimation() {
        PieRenderer renderer = pie.getRenderer(PieRenderer.class);
        renderer.setExtentDegs(0);

        // animate a scale value from a starting val of 0 to a final value of 1:
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

        // use an animation pattern that begins and ends slowly:
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(a -> {
            renderer.setExtentDegs(360 * a.getAnimatedFraction());
            pie.redraw();
        });

        // the animation will run for 1.5 seconds:
        animator.setDuration(1500);
        animator.start();
    }
}
