// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import android.graphics.RectF;

import com.androidplot.ui.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Convenience methods for dealing with {@link android.graphics.RectF}
 */
public abstract class RectFUtils {


    /**
     * Determine if two {@link RectF} instances are equal.  Must be used in place
     * of default equality operation due to a bug that exists in older versions of Android:
     * http://stackoverflow.com/questions/13517852/rectf-equals-fails-on-android-versions-below-jelly-bean
     * @param r1 May not be null
     * @param r2 May not be null
     * @return True if r1 and r2 are identical, false otherwise.
     */
    public static boolean areIdentical(@NonNull RectF r1, @NonNull RectF r2) {
        return r1.left == r2.left &&
                r1.top == r2.top &&
                r1.right == r2.right &&
                r1.bottom == r2.bottom;
    }

    /**
     * Calculates a new {@link RectF} by applying insets to rect.
     * @param rect
     * @param insets
     * @return The {@link RectF} created as a result of applying insets, or the passed in
     * instance, if the insets were null.
     */
    @NonNull
    public static RectF applyInsets(@NonNull RectF rect, @Nullable Insets insets) {
        if (insets != null) {
            return new RectF(
                    rect.left + insets.getLeft(),
                    rect.top + insets.getTop(),
                    rect.right - insets.getRight(),
                    rect.bottom - insets.getBottom());
        } else {
            return rect;
        }
    }

    /**
     * Generates a RectF from two height and two width values; the h and w values will
     * be passed into the RectF constructor such that RectF.left  <= RectF.right and
     * RectF.top <= RectF.bottom.
     * @param w1 width1
     * @param h1 height1
     * @param w2 width2
     * @param h2 height2
     * @return
     */
    @NonNull
    public static RectF createFromEdges(float w1, float h1, float w2, float h2) {
        final boolean w1IsLeft = w1 <= w2;
        final boolean h1IsTop = h1 <= h2;
        return new RectF(
                w1IsLeft ? w1 : w2,
                h1IsTop ? h1 : h2,
                w1IsLeft ? w2 : w1,
                h1IsTop ? h2 : h1);
    }
}
