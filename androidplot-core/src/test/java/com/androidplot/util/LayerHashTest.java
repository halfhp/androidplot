// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import org.junit.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

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

    // ---- edge cases: unknown keys, replacement, size/get/keys ----

    @Test
    public void addToBottom_existingKey_replacesValueAndKeepsOrder() {
        Object replacement = new Object();
        layerHash.addToBottom(obj2, replacement);

        assertEquals(3, layerHash.size());
        assertSame(replacement, layerHash.get(obj2));
        assertEquals(obj2, layerHash.getKeysAsList().get(1));
    }

    @Test
    public void addToTop_existingKey_replacesValueAndKeepsOrder() {
        Object replacement = new Object();
        layerHash.addToTop(obj3, replacement);

        assertEquals(3, layerHash.size());
        assertSame(replacement, layerHash.get(obj3));
        assertEquals(obj3, layerHash.getKeysAsList().get(0));
    }

    @Test
    public void moveToTop_unknownKey_returnsFalse() {
        assertFalse(layerHash.moveToTop(new Object()));
        assertTrue(layerHash.moveToTop(obj3));
        assertEquals(obj3, layerHash.getTop());
    }

    @Test
    public void moveToBottom_unknownKey_returnsFalse() {
        assertFalse(layerHash.moveToBottom(new Object()));
        assertTrue(layerHash.moveToBottom(obj1));
        assertEquals(obj1, layerHash.getBottom());
    }

    @Test
    public void moveUp_moveDown_unknownKey_returnFalse() {
        Object unknown = new Object();
        assertFalse(layerHash.moveUp(unknown));
        assertFalse(layerHash.moveDown(unknown));
        assertEquals(3, layerHash.size());
    }

    @Test
    public void moveAbove_sameObject_throws() {
        try {
            layerHash.moveAbove(obj1, obj1);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void moveAbove_unknownKey_returnsFalse() {
        Object unknown = new Object();
        assertFalse(layerHash.moveAbove(unknown, obj1));
        assertFalse(layerHash.moveAbove(obj1, unknown));
        assertTrue(layerHash.moveAbove(obj3, obj1));
        assertEquals(obj3, layerHash.getAbove(obj1));
    }

    @Test
    public void moveBeneath_sameObject_throws() {
        try {
            layerHash.moveBeneath(obj2, obj2);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void moveBeneath_unknownKey_returnsFalse() {
        Object unknown = new Object();
        assertFalse(layerHash.moveBeneath(unknown, obj1));
        assertFalse(layerHash.moveBeneath(obj1, unknown));
        assertTrue(layerHash.moveBeneath(obj1, obj3));
        assertEquals(obj1, layerHash.getBeneath(obj3));
    }

    @Test
    public void remove_unknownKey_returnsFalse() {
        assertFalse(layerHash.remove(new Object()));
        assertEquals(3, layerHash.size());
        assertTrue(layerHash.remove(obj2));
        assertEquals(2, layerHash.size());
        assertNull(layerHash.get(obj2));
    }

    @Test
    public void keys_andElements_areTheOrderedKeyList() {
        assertEquals(layerHash.getKeysAsList(), layerHash.keys());
        assertEquals(layerHash.getKeysAsList(), layerHash.elements());
        // bottom first, top last:
        assertEquals(obj3, layerHash.keys().get(0));
        assertEquals(obj1, layerHash.keys().get(2));
    }

    @Test
    public void getAbove_getBeneath_atEdges_returnNull() {
        // nothing above the top or beneath the bottom, and nothing around an unknown key:
        assertNull(layerHash.getAbove(obj1));
        assertNull(layerHash.getBeneath(obj3));
        assertNull(layerHash.getAbove(new Object()));
        assertNull(layerHash.getBeneath(new Object()));
        assertEquals(obj2, layerHash.getAbove(obj3));
        assertEquals(obj2, layerHash.getBeneath(obj1));
    }
}
