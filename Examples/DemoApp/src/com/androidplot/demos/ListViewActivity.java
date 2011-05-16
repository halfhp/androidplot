package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: halfhp
 * Date: 4/14/11
 * Time: 8:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListViewActivity extends Activity {
    private static final int NUM_PLOTS = 10;
    private static final int NUM_POINTS_PER_SERIES = 10;
    private static final int NUM_SERIES_PER_PLOT = 5;
    private ListView lv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview_example);

        lv = (ListView) findViewById(R.id.listView1);

        lv.setAdapter(new SackOfViewsAdapter(getPlots()));
    }

    private List<View> getPlots()  {
        ArrayList<View> plots = new ArrayList<View>();

        Random generator = new Random();

        for(int i = 0; i < NUM_PLOTS; i++) {

            XYPlot pl = new XYPlot(getApplicationContext(), "Plot #" + i);

            for (int k = 0; k < NUM_SERIES_PER_PLOT; k++) {
                ArrayList<Number> nums = new ArrayList<Number>();
                for (int j = 0; j < NUM_POINTS_PER_SERIES; j++) {
                    nums.add(generator.nextFloat());
                }

                double f = Math.random();


                XYSeries series = new SimpleXYSeries(nums, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
                pl.addSeries(series, new LineAndPointFormatter(
                        Color.rgb(new Double(f*255).intValue(), new Double(f*255).intValue(), new Double(f*255).intValue()),
                        Color.rgb(new Double(f*255).intValue(), new Double(f*255).intValue(), new Double(f*255).intValue()), null));

            }

            pl.setMinimumHeight(300);
            pl.disableAllMarkup();
            plots.add(pl);

        }
        return plots;
    }

    /**
     * Adapter that simply returns row views from a list.
     * <p/>
     * If you supply a size, you must implement newView(), to
     * create a required view. The adapter will then cache these
     * views.
     * <p/>
     * If you supply a list of views in the constructor, that
     * list will be used directly. If any elements in the list
     * are null, then newView() will be called just for those
     * slots.
     * <p/>
     * Subclasses may also wish to override areAllItemsEnabled()
     * (default: false) and isEnabled() (default: false), if some
     * of their rows should be selectable.
     * <p/>
     * It is assumed each view is unique, and therefore will not
     * get recycled.
     * <p/>
     * Note that this adapter is not designed for long lists. It
     * is more for screens that should behave like a list. This
     * is particularly useful if you combine this with other
     * adapters (e.g., SectionedAdapter) that might have an
     * arbitrary number of rows, so it all appears seamless.
     */
    static class SackOfViewsAdapter extends BaseAdapter {
        private List<View> views = null;

        /**
         * Constructor creating an empty list of views, but with
         * a specified count. Subclasses must override newView().
         */
        public SackOfViewsAdapter(int count) {
            super();

            views = new ArrayList<View>(count);

            for (int i = 0; i < count; i++) {
                views.add(null);
            }
        }

        /**
         * Constructor wrapping a supplied list of views.
         * Subclasses must override newView() if any of the elements
         * in the list are null.
         */
        public SackOfViewsAdapter(List<View> views) {
            super();

            this.views = views;
        }

        /**
         * Get the data item associated with the specified
         * position in the data set.
         *
         * @param position Position of the item whose data we want
         */
        @Override
        public Object getItem(int position) {
            return (views.get(position));
        }

        /**
         * How many items are in the data set represented by this
         * Adapter.
         */
        @Override
        public int getCount() {
            return (views.size());
        }

        /**
         * Returns the number of types of Views that will be
         * created by getView().
         */
        @Override
        public int getViewTypeCount() {
            return (getCount());
        }

        /**
         * Get the type of View that will be created by getView()
         * for the specified item.
         *
         * @param position Position of the item whose data we want
         */
        @Override
        public int getItemViewType(int position) {
            return (position);
        }

        /**
         * Are all items in this ListAdapter enabled? If yes it
         * means all items are selectable and clickable.
         */
        @Override
        public boolean areAllItemsEnabled() {
            return (false);
        }

        /**
         * Returns true if the item at the specified position is
         * not a separator.
         *
         * @param position Position of the item whose data we want
         */
        @Override
        public boolean isEnabled(int position) {
            return (false);
        }

        /**
         * Get a View that displays the data at the specified
         * position in the data set.
         *
         * @param position    Position of the item whose data we want
         * @param convertView View to recycle, if not null
         * @param parent      ViewGroup containing the returned View
         */
        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            View result = views.get(position);

            if (result == null) {
                result = newView(position, parent);
                views.set(position, result);
            }

            return (result);
        }

        /**
         * Get the row id associated with the specified position
         * in the list.
         *
         * @param position Position of the item whose data we want
         */
        @Override
        public long getItemId(int position) {
            return (position);
        }

        /**
         * Create a new View to go into the list at the specified
         * position.
         *
         * @param position Position of the item whose data we want
         * @param parent   ViewGroup containing the returned View
         */
        protected View newView(int position, ViewGroup parent) {
            throw new RuntimeException("You must override newView()!");
        }
    }
}