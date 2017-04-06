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

package com.androidplot.util;

import org.junit.*;

import static junit.framework.Assert.assertEquals;

public class LayerHashTest {

    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    LayerHash<Object, Object> layerHash;

    @Before
    public void setUp() throws Exception {
        layerHash = new LayerHash<>();
        layerHash.addToBottom(obj1, obj1);
        layerHash.addToBottom(obj2, obj2);
        layerHash.addToBottom(obj3, obj3);

        assertEquals(obj1, layerHash.getTop());
        assertEquals(obj3, layerHash.getBottom());
    }

    // "top" == last element in the list:
    @Test
    public void moveUp() {
        layerHash.moveUp(obj3);
        assertEquals(obj1, layerHash.getTop());
        assertEquals(obj3, layerHash.getBeneath(obj1));
        assertEquals(obj2, layerHash.getBeneath(obj3));

        layerHash.moveUp(obj3);
        assertEquals(obj3, layerHash.getTop());
        assertEquals(obj1, layerHash.getBeneath(obj3));
        assertEquals(obj2, layerHash.getBeneath(obj1));

        layerHash.moveUp(obj3);
        assertEquals(obj3, layerHash.getTop());
        assertEquals(obj1, layerHash.getBeneath(obj3));
        assertEquals(obj2, layerHash.getBeneath(obj1));
    }

    // "bottom" == first element in the list:
    @Test
    public void moveDown() {
        layerHash.moveDown(obj1);
        assertEquals(obj3, layerHash.getKeysAsList().get(0));
        assertEquals(obj1, layerHash.getKeysAsList().get(1));
        assertEquals(obj2, layerHash.getKeysAsList().get(2));

        layerHash.moveDown(obj1);
        assertEquals(obj1, layerHash.getKeysAsList().get(0));
        assertEquals(obj3, layerHash.getKeysAsList().get(1));
        assertEquals(obj2, layerHash.getKeysAsList().get(2));

        layerHash.moveDown(obj1);
        assertEquals(obj1, layerHash.getKeysAsList().get(0));
        assertEquals(obj3, layerHash.getKeysAsList().get(1));
        assertEquals(obj2, layerHash.getKeysAsList().get(2));
    }

    @Test
    public void moveAbove() {
        layerHash.moveAbove(obj2, obj1);
        assertEquals(obj3, layerHash.getKeysAsList().get(0));
        assertEquals(obj1, layerHash.getKeysAsList().get(1));
        assertEquals(obj2, layerHash.getKeysAsList().get(2));
    }

    @Test
    public void moveBeneath() {
        layerHash.moveBeneath(obj1, obj2);
        assertEquals(obj3, layerHash.getKeysAsList().get(0));
        assertEquals(obj1, layerHash.getKeysAsList().get(1));
        assertEquals(obj2, layerHash.getKeysAsList().get(2));
    }

    @Test
    public void addToTop() {
        Object obj = new Object();
        layerHash.addToTop(obj, obj);
        assertEquals(obj, layerHash.getTop());
        assertEquals(obj3, layerHash.getKeysAsList().get(0));
        assertEquals(obj2, layerHash.getKeysAsList().get(1));
        assertEquals(obj1, layerHash.getKeysAsList().get(2));
        assertEquals(obj, layerHash.getKeysAsList().get(3));
    }

    @Test
    public void moveToTop() {
        layerHash.moveToTop(obj3);
        assertEquals(obj3, layerHash.getTop());
        assertEquals(obj1, layerHash.getBeneath(obj3));
        assertEquals(obj2, layerHash.getBeneath(obj1));
    }

    @Test
    public void moveToBottom() {
        layerHash.moveToBottom(obj1);
        assertEquals(obj1, layerHash.getBottom());
        assertEquals(obj3, layerHash.getAbove(obj1));
        assertEquals(obj2, layerHash.getAbove(obj3));
    }

    @Test
    public void remove() {
        layerHash.remove(obj2);
        assertEquals(2, layerHash.size());
        assertEquals(obj1, layerHash.getAbove(obj3));
    }

}
