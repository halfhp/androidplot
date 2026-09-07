// SPDX-License-Identifier: Apache-2.0
package com.androidplot.util.fig;

import android.graphics.Color;

import com.androidplot.test.AndroidplotTest;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.*;
import java.net.*;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class FigTest extends AndroidplotTest {

    class A {
        private int d = 0;
        private float f = 0;
        private float p = 0;
        private boolean aBooleanPrimitive;

        public int getD() {
            return d;
        }

        public void setD(int d) {
            this.d = d;
        }

        public boolean isaBooleanPrimitive() {
            return aBooleanPrimitive;
        }

        public void setaBooleanPrimitive(boolean aBooleanPrimitive) {
            this.aBooleanPrimitive = aBooleanPrimitive;
        }

        public float getF() {
            return f;
        }

        public void setF(float f) {
            this.f = f;
        }

        public float getP() {
            return p;
        }

        public void setP(float p) {
            this.p = p;
        }
    }

    class B {
        private A a = new A();

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }
    }

    class C {
        private B b = new B();

        public B getB() {
            return b;
        }

        public void setB(B a) {
            this.b = b;
        }
    }

    @Test
    public void testGetFieldAt() throws Exception {
        C c = new C();
        assertEquals(c, Fig.getObjectContaining(c, "b"));
        assertEquals(c.getB(), Fig.getObjectContaining(c, "b.a"));
        assertEquals(c.getB().getA(), Fig.getObjectContaining(c, "b.a.d"));
    }

    @Test
    public void testConfigure() throws Exception {
        C c = new C();
        assertFalse(c.getB().getA().isaBooleanPrimitive());
        assertEquals(0, c.getB().getA().getD());

        // load xml config and verify:
        File f = getFileFromPath("c_config.xml");
        Fig.configure(RuntimeEnvironment.application, c, f);

        assertTrue(c.getB().getA().isaBooleanPrimitive());
        assertEquals(99, c.getB().getA().getD());
        assertEquals(2f, c.getB().getA().getF());
        assertEquals(6.6666665f, c.getB().getA().getP());
    }

    /**
     * Regression test for https://github.com/halfhp/androidplot/issues/88: an int attribute
     * given as a compiled color resource reference (the '@<id>' form AAPT emits for
     * '@color/foo' in res/xml) must resolve to the color value rather than crash.
     */
    @Test
    public void testConfigure_intFromColorResourceReference() throws Exception {
        A a = new A();
        HashMap<String, String> params = new HashMap<>();
        params.put("d", "@" + android.R.color.black);
        Fig.configure(RuntimeEnvironment.application, a, params);
        assertEquals(Color.BLACK, a.getD());
    }

    @Test
    public void testConfigure_intFromIntegerResourceReference() throws Exception {
        A a = new A();
        HashMap<String, String> params = new HashMap<>();
        params.put("d", "@" + android.R.integer.config_shortAnimTime);
        Fig.configure(RuntimeEnvironment.application, a, params);
        assertEquals(RuntimeEnvironment.application.getResources()
                .getInteger(android.R.integer.config_shortAnimTime), a.getD());
    }

    @Test
    public void testConfigure_intFromHexColor() throws Exception {
        A a = new A();
        HashMap<String, String> params = new HashMap<>();
        params.put("d", "#FF00AA00");
        Fig.configure(RuntimeEnvironment.application, a, params);
        assertEquals(0xFF00AA00, a.getD());
    }

    @Test
    public void testConfigure_negativeInt() throws Exception {
        A a = new A();
        HashMap<String, String> params = new HashMap<>();
        params.put("d", "-7");
        Fig.configure(RuntimeEnvironment.application, a, params);
        assertEquals(-7, a.getD());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigure_unresolvableResourceReference() throws Exception {
        A a = new A();
        HashMap<String, String> params = new HashMap<>();
        params.put("d", "@color/does_not_exist");
        Fig.configure(RuntimeEnvironment.application, a, params);
    }

    private File getFileFromPath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
    }
}
