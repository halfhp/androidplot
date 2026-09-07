// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import java.util.List;

/**
 * Created by nick_f on 9/25/14.
 */
public interface Interpolator<ParamsType extends InterpolationParams> {


    List<XYCoords> interpolate(XYSeries series, ParamsType params);
}
