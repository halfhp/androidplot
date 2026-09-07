// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import java.util.List;
import androidx.annotation.NonNull;

/**
 * Created by nick_f on 9/25/14.
 */
public interface Interpolator<ParamsType extends InterpolationParams> {


    @NonNull
    List<XYCoords> interpolate(@NonNull XYSeries series, @NonNull ParamsType params);
}
