// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import androidx.annotation.NonNull;

/**
 * Created by nick_f on 9/25/14.
 */
public interface InterpolationParams<InterpolatorType extends Interpolator> {

    @NonNull
    Class<InterpolatorType> getInterpolatorClass();
}
