// SPDX-License-Identifier: Apache-2.0
package com.androidplot.util.fig;

import android.graphics.Paint;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.SizeMode;
import com.androidplot.xy.XYPlot;

import org.junit.Test;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Parameter inflation paths of {@link Fig}: enum, boxed and multi-argument setters, string /
 * float resource references, dimension strings and the error paths.  Basic int / boolean /
 * nested paths are covered by {@link FigTest}.
 */
public class FigParamsTest extends AndroidplotTest {

    public static class Target {
        SizeMode mode;
        float left, top, right, bottom;
        String label;
        Float boxedFloat;
        Integer boxedInt;
        Boolean boxedBool;
        float size;
        String name;
        Target child;
        Paint paint = new Paint();

        public void setMode(SizeMode mode) {
            this.mode = mode;
        }

        public void setMargins(float left, float top, float right, float bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public void setBoxedFloat(Float boxedFloat) {
            this.boxedFloat = boxedFloat;
        }

        public void setBoxedInt(Integer boxedInt) {
            this.boxedInt = boxedInt;
        }

        public void setBoxedBool(Boolean boxedBool) {
            this.boxedBool = boxedBool;
        }

        public void setSize(float size) {
            this.size = size;
        }

        public void setNameAndSize(String name, float size) {
            this.name = name;
            this.size = size;
        }

        public void setUnsupported(Object o) {
            fail("must not be invoked");
        }

        public void setExplodes(int value) {
            throw new IllegalStateException("boom " + value);
        }

        public Target getChild() {
            return child;
        }

        public void setChild(Target child) {
            this.child = child;
        }

        public Paint getPaint() {
            return paint;
        }

        /** not a setter: no params */
        public void setNothing() {
        }

        long ambiguousLong = -1;
        int ambiguousInt = -1;

        // overloads that only differ by an uninflatable type, in both declaration orders:
        public void setAmbiguous(long value) {
            this.ambiguousLong = value;
        }

        public void setAmbiguous(int value) {
            this.ambiguousInt = value;
        }

        public void setAmbiguousReversed(int value) {
            this.ambiguousInt = value;
        }

        public void setAmbiguousReversed(long value) {
            this.ambiguousLong = value;
        }

        // overloads that differ by arity:
        public void setSized(float size) {
            this.size = size;
        }

        public void setSized(float size, SizeMode mode) {
            this.size = size;
            this.mode = mode;
        }
    }

    private static HashMap<String, String> params(String... keyValues) {
        final HashMap<String, String> params = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            params.put(keyValues[i], keyValues[i + 1]);
        }
        return params;
    }

    @Test
    public void enumParam_isCaseInsensitive() throws Exception {
        final Target target = new Target();
        Fig.configure(getContext(), target, params("mode", "relative"));
        assertEquals(SizeMode.RELATIVE, target.mode);
        Fig.configure(getContext(), target, params("mode", "FILL"));
        assertEquals(SizeMode.FILL, target.mode);
        Fig.configure(getContext(), target, params("mode", "Absolute"));
        assertEquals(SizeMode.ABSOLUTE, target.mode);
    }

    @Test
    public void enumParam_unknownConstant_throwsFigException() {
        try {
            Fig.configure(getContext(), new Target(), params("mode", "sideways"));
            fail("expected FigException");
        } catch (FigException expected) {
            assertTrue(expected.getCause() instanceof java.lang.reflect.InvocationTargetException);
        }
    }

    @Test
    public void multiArgSetter_splitsOnPipe() throws Exception {
        final Target target = new Target();
        Fig.configure(getContext(), target, params("margins", "1|2.5|3dp|-4"));
        assertEquals(1f, target.left, 0);
        assertEquals(2.5f, target.top, 0);
        assertEquals(3f, target.right, 0);
        assertEquals(-4f, target.bottom, 0);

        Fig.configure(getContext(), target, params("nameAndSize", "hello|12px"));
        assertEquals("hello", target.name);
        assertEquals(12f, target.size, 0);
    }

    @Test
    public void multiArgSetter_wrongArgCount_throws() throws Exception {
        for (String value : new String[]{"1|2", "1|2|3|4|5", "1"}) {
            try {
                Fig.configure(getContext(), new Target(), params("margins", value));
                fail("expected IllegalArgumentException for '" + value + "'");
            } catch (IllegalArgumentException expected) {
                assertTrue(expected.getMessage().contains("Unexpected number of argments"));
            }
        }
    }

    @Test
    public void stringParam_plainValueAndResourceReference() throws Exception {
        final Target target = new Target();
        Fig.configure(getContext(), target, params("label", "plain text"));
        assertEquals("plain text", target.label);

        Fig.configure(getContext(), target, params("label", "@" + android.R.string.ok));
        assertEquals(getContext().getString(android.R.string.ok), target.label);

        // a value that merely looks like a reference but has no resource stays a string:
        Fig.configure(getContext(), target, params("label", "@twitter"));
        assertEquals("@twitter", target.label);
    }

    @Test
    public void floatParam_dimensionStringsResourceReferencesAndPlainNumbers() throws Exception {
        final Target target = new Target();
        Fig.configure(getContext(), target, params("size", "2.5"));
        assertEquals(2.5f, target.size, 0);

        Fig.configure(getContext(), target, params("size", "12dp"));
        assertEquals(12f, target.size, 0);

        Fig.configure(getContext(), target, params("size", "7px"));
        assertEquals(7f, target.size, 0);

        Fig.configure(getContext(), target, params("size", "-3"));
        assertEquals(-3f, target.size, 0);

        Fig.configure(getContext(), target, params("size", "@" + android.R.dimen.app_icon_size));
        assertEquals(getContext().getResources().getDimension(android.R.dimen.app_icon_size), target.size, 0);
    }

    @Test
    public void floatParam_negativeDimensionString_keepsSign() throws Exception {
        final Target target = new Target();
        Fig.configure(getContext(), target, params("size", "-12dp"));
        assertEquals(-12f, target.size, 0);
        Fig.configure(getContext(), target, params("size", "-1.5px"));
        assertEquals(-1.5f, target.size, 0);
    }

    @Test
    public void floatParam_unparseable_throwsNumberFormatException() throws Exception {
        try {
            Fig.configure(getContext(), new Target(), params("size", "big"));
            fail("expected NumberFormatException");
        } catch (NumberFormatException expected) {
            // ok
        }
    }

    @Test
    public void boxedParams() throws Exception {
        final Target target = new Target();
        Fig.configure(getContext(), target, params(
                "boxedFloat", "1.5",
                "boxedInt", "42",
                "boxedBool", "true"));
        assertEquals(1.5f, target.boxedFloat, 0);
        assertEquals(42, target.boxedInt.intValue());
        assertTrue(target.boxedBool);

        Fig.configure(getContext(), target, params("boxedBool", "nonsense"));
        assertFalse(target.boxedBool);
    }

    @Test
    public void intParam_dimenResourceReference_resolvesToPixelSize() throws Exception {
        final Target target = new Target();
        Fig.configure(getContext(), target, params("boxedInt", "@" + android.R.dimen.app_icon_size));
        assertEquals(getContext().getResources().getDimensionPixelSize(android.R.dimen.app_icon_size),
                target.boxedInt.intValue());
    }

    @Test
    public void intParam_compressedTransparentColor() throws Exception {
        final Target target = new Target();
        Fig.configure(getContext(), target, params("boxedInt", "#0"));
        assertEquals(0, target.boxedInt.intValue());
    }

    @Test
    public void unsupportedParamType_throwsIllegalArgumentException() throws Exception {
        try {
            Fig.configure(getContext(), new Target(), params("unsupported", "x"));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("unsupported type"));
        }
    }

    @Test
    public void noSuchSetter_throwsFigException() {
        try {
            Fig.configure(getContext(), new Target(), params("doesNotExist", "x"));
            fail("expected FigException");
        } catch (FigException expected) {
            assertTrue(expected.getCause() instanceof NoSuchMethodException);
        }
    }

    @Test
    public void setterWithoutParams_throwsIllegalArgumentException() throws Exception {
        try {
            Fig.configure(getContext(), new Target(), params("nothing", "x"));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("no setter method"));
        }
    }

    @Test
    public void setterThrows_wrappedInFigException() {
        try {
            Fig.configure(getContext(), new Target(), params("explodes", "5"));
            fail("expected FigException");
        } catch (FigException expected) {
            assertTrue(expected.getCause() instanceof java.lang.reflect.InvocationTargetException);
            assertTrue(expected.getMessage().contains("explodes"));
        }
    }

    @Test
    public void nestedPath_resolvesThroughGetters() throws Exception {
        final Target root = new Target();
        root.setChild(new Target());
        root.getChild().setChild(new Target());
        Fig.configure(getContext(), root, params(
                "child.child.size", "9",
                "child.mode", "fill",
                "child.paint.color", "#FF102030",
                "paint.textAlign", "right"));
        assertEquals(9f, root.child.child.size, 0);
        assertEquals(SizeMode.FILL, root.child.mode);
        assertEquals(0xFF102030, root.child.paint.getColor());
        assertEquals(Paint.Align.RIGHT, root.paint.getTextAlign());
        assertNull(root.label);

        assertSame(root.child.child, Fig.getObjectContaining(root, "child.child.size"));
        assertSame(root, Fig.getObjectContaining(root, "size"));
    }

    @Test
    public void nestedPath_nullIntermediate_throwsNullPointerException() throws Exception {
        try {
            Fig.configure(getContext(), new Target(), params("child.size", "1"));
            fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            assertTrue(expected.getMessage().contains("size"));
        }
    }

    @Test
    public void configureFromMissingFile_throwsFigException() {
        try {
            Fig.configure(getContext(), new Target(), new File("does/not/exist.xml"));
            fail("expected FigException");
        } catch (FigException expected) {
            assertTrue(expected.getCause() instanceof java.io.FileNotFoundException);
        }
    }

    @Test
    public void configureFromXmlResource_appliesConfigElementAttrs() throws Exception {
        // the test xml resource is packaged but absent from the compile time R stub
        final int cfgId = getContext().getResources().getIdentifier(
                "fig_params_cfg", "xml", getContext().getPackageName());
        assertTrue("test xml resource not found", cfgId != 0);

        final Target target = new Target();
        Fig.configure(getContext(), target, cfgId);

        assertEquals(3f, target.size, 0);
        assertEquals(SizeMode.RELATIVE, target.mode);
        assertEquals("from xml", target.label);
        assertEquals(1f, target.left, 0);
        assertEquals(2f, target.top, 0);
        assertEquals(3f, target.right, 0);
        assertEquals(4f, target.bottom, 0);
        assertEquals(0xFF102030, target.paint.getColor());
        assertTrue(target.boxedBool);
        assertNull(target.boxedInt);
    }

    @Test
    public void configureFromXmlResource_unknownProperty_throwsFigException() {
        final int cfgId = getContext().getResources().getIdentifier(
                "segment_formatter_cfg", "xml", getContext().getPackageName());
        assertTrue("test xml resource not found", cfgId != 0);
        try {
            Fig.configure(getContext(), new Target(), cfgId);
            fail("expected FigException for the unknown properties in the config");
        } catch (FigException expected) {
            assertTrue(expected.getCause() instanceof NoSuchMethodException);
        }
    }

    @Test
    public void overloadedSetter_prefersInflatableParamTypes() throws Exception {
        final Target target = new Target();
        Fig.configure(getContext(), target, params("ambiguous", "7"));
        assertEquals(7, target.ambiguousInt);
        assertEquals(-1, target.ambiguousLong);

        Fig.configure(getContext(), target, params("ambiguousReversed", "8"));
        assertEquals(8, target.ambiguousInt);
        assertEquals(-1, target.ambiguousLong);
    }

    @Test
    public void overloadedSetter_prefersMatchingArity() throws Exception {
        final Target target = new Target();
        Fig.configure(getContext(), target, params("sized", "3"));
        assertEquals(3f, target.size, 0);
        assertNull(target.mode);

        Fig.configure(getContext(), target, params("sized", "0.5|relative"));
        assertEquals(0.5f, target.size, 0);
        assertEquals(SizeMode.RELATIVE, target.mode);

        // real world case: Widget.setWidth(float) vs Widget.setWidth(float, SizeMode)
        final XYPlot plot = new XYPlot(getContext(), "plot");
        Fig.configure(getContext(), plot, params("graph.width", "0.25|relative"));
        assertEquals(0.25f, plot.getGraph().getWidthMetric().getValue(), 0);
        assertEquals(SizeMode.RELATIVE, plot.getGraph().getWidthMetric().getLayoutType());
        Fig.configure(getContext(), plot, params("graph.width", "0.75"));
        assertEquals(0.75f, plot.getGraph().getWidthMetric().getValue(), 0);
        assertEquals(SizeMode.RELATIVE, plot.getGraph().getWidthMetric().getLayoutType());
    }

    /**
     * On API 29+ Paint declares both setColor(int) and setColor(long); the configurator must pick
     * the int overload regardless of reflection ordering.
     */
    @Test
    @Config(sdk = 36)
    public void overloadedSetter_paintSetColorOnModernSdk() throws Exception {
        final XYPlot plot = new XYPlot(getContext(), "plot");
        Fig.configure(getContext(), plot, params("backgroundPaint.color", "#FF0000"));
        assertEquals(0xFFFF0000, plot.getBackgroundPaint().getColor());

        final Target target = new Target();
        Fig.configure(getContext(), target, params("paint.color", "#FF00FF00"));
        assertEquals(0xFF00FF00, target.paint.getColor());
    }
}
