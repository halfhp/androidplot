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

package com.androidplot.xy;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of Catmull-Rom interpolation, based on the information found at:
 * http://stackoverflow.com/questions/9489736/catmull-rom-curve-with-no-cusps-and-no-self-intersections/19283471#19283471
 */
public class CatmullRomInterpolator implements Interpolator<CatmullRomInterpolator.Params> {

    public enum Type {
        Uniform,
        Centripetal
    }

    public static class Params implements InterpolationParams {

        private int pointPerSegment;
        private Type type;

        public Params(int pointPerSegment, Type type) {
            this.pointPerSegment = pointPerSegment;
            this.type = type;
        }

        @Override
        public Class<CatmullRomInterpolator> getInterpolatorClass() {
            return CatmullRomInterpolator.class;
        }

        public int getPointPerSegment() {
            return pointPerSegment;
        }

        public void setPointPerSegment(int pointPerSegment) {
            this.pointPerSegment = pointPerSegment;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }
    }

    /**
     * Wraps a normal XYSeries, inserting a new point at the beginning and end of the series.
     */
    static class ExtrapolatedXYSeries implements XYSeries {
        private final XYCoords first;
        private final XYCoords last;
        private final XYSeries series;

        public ExtrapolatedXYSeries(XYSeries series, XYCoords first, XYCoords last) {
            this.series = series;
            this.first = first;
            this.last = last;
        }

        @Override
        public Number getX(int i) {
            if(i == 0) {
                return first.x;
            } else if(i == series.size() + 1) {
                return last.x;
            } else {
                return series.getX(i-1);
            }
        }

        @Override
        public Number getY(int i) {
            if(i == 0) {
                return first.y;
            } else if(i == series.size() + 1) {
                return last.y;
            } else {
                return series.getY(i-1);
            }
        }

        @Override
        public int size() {
            return series.size() + 2;
        }

        @Override
        public String getTitle() {
            return series.getTitle();
        }
    }

    /**
     * This method will calculate the Catmull-Rom interpolation curve, returning it as a list of Coord coordinate
     * objects.  This method in particular adds the first and last control points which are not visible, but required
     * for calculating the spline.
     *
     * @param series The list of original straight line points to calculate an interpolation from.
     * @return The list of interpolated coordinates.
     * @throws java.lang.IllegalArgumentException if pointsPerSegment is less than 2.
     */
    @Override
    public List<XYCoords> interpolate(XYSeries series, Params params) {
        if (params.getPointPerSegment() < 2) {
            throw new IllegalArgumentException(
                    "pointsPerSegment must be greater than 2, since 2 points is just the linear segment.");
        }

        // Cannot interpolate curves given only two points.  Two points
        // is best represented as a simple line segment.
        if (series.size() < 3) {
            throw new IllegalArgumentException("Cannot interpolate a series with fewer than 3 vertices.");
        }

        // Get the change in x and y between the first and second coordinates.
        double dx = series.getX(1).doubleValue() - series.getX(0).doubleValue();
        double dy = series.getY(1).doubleValue() - series.getY(0).doubleValue();

        // Then using the change, extrapolate backwards to find a control point.
        double x1 = series.getX(0).doubleValue() - dx;
        double y1 = series.getY(0).doubleValue() - dy;

        // Actually create the start point from the extrapolated values.
        XYCoords start = new XYCoords(x1, y1);

        // Repeat for the end control point.
        int n = series.size() -1;
        dx = series.getX(n).doubleValue() - series.getX(n-1).doubleValue();
        dy = series.getY(n).doubleValue() - series.getY(n - 1).doubleValue();
        double xn = series.getX(n).doubleValue() + dx;
        double yn = series.getY(n).doubleValue() + dy;
        XYCoords end = new XYCoords(xn, yn);

        // TODO: figure out whether this extra control-point synthesis is
        // TODO: really necessary and either remove the above or fix the below.
        // insert the start control point at the start of the vertices list.
        // TODO vertices.add(0, start);

        // append the end control ponit to the end of the vertices list.
        // TODO vertices.add(end);
        //}

        ExtrapolatedXYSeries extrapolatedXYSeries = new ExtrapolatedXYSeries(series, start, end);

        // Dimension a result list of coordinates.
        List<XYCoords> result = new ArrayList<>();

        // When looping, remember that each cycle requires 4 points, starting
        // with i and ending with i+3.  So we don't loop through all the points.
        for (int i = 0; i < extrapolatedXYSeries.size() - 3; i++) {

            // Actually calculate the Catmull-Rom curve for one segment.
            List<XYCoords> points = interpolate(extrapolatedXYSeries, i, params);

            // Since the middle points are added twice, once for each bordering
            // segment, we only add the 0 index result point for the first
            // segment.  Otherwise we will have duplicate points.
            if (result.size() > 0) {
                points.remove(0);
            }

            // Add the coordinates for the segment to the result list.
            result.addAll(points);
        }
        return result;

    }

    /**
     * Given a list of control points, this will create a list of pointsPerSegment
     * points spaced uniformly along the resulting Catmull-Rom curve.
     *
     * @param series           The list of control points, leading and ending with a
     *                         coordinate that is only used for controling the spline and is not visualized.
     * @param index            The index of control point p0, where p0, p1, p2, and p3 are
     *                         used in order to create a curve between p1 and p2.
     * @return the list of coordinates that define the CatmullRom curve
     * between the points defined by index+1 and index+2.
     */
    protected List<XYCoords> interpolate(XYSeries series, int index, Params params) {
        List<XYCoords> result = new ArrayList<>();
        double[] x = new double[4];
        double[] y = new double[4];
        double[] time = new double[4];
        for (int i = 0; i < 4; i++) {
            x[i] = series.getX(index + i).doubleValue();
            y[i] = series.getY(index + i).doubleValue();
            time[i] = i;
        }

        double tstart = 1;
        double tend = 2;
        if (params.getType() != Type.Uniform) {
            double total = 0;
            for (int i = 1; i < 4; i++) {
                double dx = x[i] - x[i - 1];
                double dy = y[i] - y[i - 1];
                if (params.getType() == Type.Centripetal) {
                    total += Math.pow(dx * dx + dy * dy, .25);
                } else {
                    total += Math.pow(dx * dx + dy * dy, .5);
                }
                time[i] = total;
            }
            tstart = time[1];
            tend = time[2];
        }

        int segments = params.getPointPerSegment() - 1;
        result.add(new XYCoords(series.getX(index + 1), series.getY(index + 1)));
        for (int i = 1; i < segments; i++) {
            double xi = interpolate(x, time, tstart + (i * (tend - tstart)) / segments);
            double yi = interpolate(y, time, tstart + (i * (tend - tstart)) / segments);
            result.add(new XYCoords(xi, yi));
        }
        result.add(new XYCoords(series.getX(index + 2), series.getY(index + 2)));
        return result;
    }

    /**
     * Unlike the other implementation here, which uses the default "uniform"
     * treatment of t, this computation is used to calculate the same values but
     * introduces the ability to "parameterize" the t values used in the
     * calculation. This is based on Figure 3 from
     * http://www.cemyuksel.com/research/catmullrom_param/catmullrom.pdf
     *
     * @param p    An array of double values of length 4, where interpolation
     *             occurs from p1 to p2.
     * @param time An array of time measures of length 4, corresponding to each
     *             p value.
     * @param t    the actual interpolation ratio from 0 to 1 representing the
     *             position between p1 and p2 to interpolate the value.
     * @return
     */
    protected static double interpolate(double[] p, double[] time, double t) {
        double L01 = p[0] * (time[1] - t) / (time[1] - time[0]) + p[1] * (t - time[0]) / (time[1] - time[0]);
        double L12 = p[1] * (time[2] - t) / (time[2] - time[1]) + p[2] * (t - time[1]) / (time[2] - time[1]);
        double L23 = p[2] * (time[3] - t) / (time[3] - time[2]) + p[3] * (t - time[2]) / (time[3] - time[2]);
        double L012 = L01 * (time[2] - t) / (time[2] - time[0]) + L12 * (t - time[0]) / (time[2] - time[0]);
        double L123 = L12 * (time[3] - t) / (time[3] - time[1]) + L23 * (t - time[1]) / (time[3] - time[1]);
        double C12 = L012 * (time[2] - t) / (time[2] - time[1]) + L123 * (t - time[1]) / (time[2] - time[1]);
        return C12;
    }
}
