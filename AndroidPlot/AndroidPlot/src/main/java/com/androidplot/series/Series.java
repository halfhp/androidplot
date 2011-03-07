package com.androidplot.series;

/**
 * Base interface for all Series implementations
 */
public interface Series {

    /**
     *
     * @return The title of this Series.
     */
    public String getTitle();

    /**
     *
     * @return Number of elements in this Series.
     */
    public int size();

    // used primarily for synchronization.  can also be used to hang a condition on updates.

    /**
     * Called whenever the plot initiates a read of a Series.  In most cases this means that
     * a complete read of the Series contents will proceed.
     */
    //public void onReadBegin();

    /**
     * Called when a Plot concludes reading of a Series.
     */
    //public void onReadEnd();
}
