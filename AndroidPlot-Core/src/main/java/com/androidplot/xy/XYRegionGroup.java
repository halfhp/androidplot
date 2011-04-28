package com.androidplot.xy;

import com.androidplot.util.ZLinkedList;
import com.androidplot.util.ZIndexable;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of regions.
 */
@Deprecated
public class XYRegionGroup  implements ZIndexable<XYRegion> {
    ZLinkedList<XYRegion> regions;

    {
        regions = new ZLinkedList<XYRegion>();
    }

    @Override
    public boolean moveToTop(XYRegion element) {
        return regions.moveToTop(element);
    }

    @Override
    public boolean moveAbove(XYRegion objectToMove, XYRegion reference) {
        return regions.moveAbove(objectToMove, reference);
    }

    @Override
    public boolean moveBeneath(XYRegion objectToMove, XYRegion reference) {
        return regions.moveBeneath(objectToMove, reference);
    }

    @Override
    public boolean moveToBottom(XYRegion key) {
        return regions.moveToBottom(key);
    }

    @Override
    public boolean moveUp(XYRegion key) {
        return regions.moveUp(key);
    }

    @Override
    public boolean moveDown(XYRegion key) {
        return regions.moveDown(key);
    }

    @Override
    public List<XYRegion> elements() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public void addToBottom(XYRegion element) {
        regions.addToBottom(element);
    }

    public void addToTop(XYRegion element) {
        regions.addToTop(element);
    }

    public void remove(XYRegion element) {
        regions.remove(element);
    }
}
