// SPDX-License-Identifier: Apache-2.0
package com.androidplot.util;

import android.graphics.Paint;

import com.androidplot.Plot;
import com.androidplot.R;
import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYGraphWidget.Edge;
import com.androidplot.xy.XYPlot;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.android.AttributeSetBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * {@link AttrUtils} (and Plot / XYPlot) map XML enum attrs to Java enums by ordinal, so the
 * {@code <enum name="..." value="..."/>} declarations in attrs.xml must stay in sync with the
 * Java enum declarations.  This test parses attrs.xml from source and, for every declared enum
 * value, inflates an XYPlot with that value and checks the resulting Java enum constant has the
 * declared ordinal.  Every enum attr in attrs.xml must be listed in {@link #enumAttrs()}.
 */
public class AttrsXmlEnumSyncTest extends AndroidplotTest {

    private interface EnumReader {
        Enum<?> read(XYPlot plot);
    }

    private static class EnumAttr {
        final int attrId;
        final Class<? extends Enum<?>> enumClass;
        final EnumReader reader;
        /** an attr that must be set alongside to keep relative modes valid; 0 for none. */
        final int companionAttrId;

        EnumAttr(int attrId, Class<? extends Enum<?>> enumClass, EnumReader reader) {
            this(attrId, enumClass, reader, 0);
        }

        EnumAttr(int attrId, Class<? extends Enum<?>> enumClass, EnumReader reader, int companionAttrId) {
            this.attrId = attrId;
            this.enumClass = enumClass;
            this.reader = reader;
            this.companionAttrId = companionAttrId;
        }
    }

    /**
     * Java enum constants that attrs.xml deliberately (or historically) does not expose.  Their
     * ordinals must still be beyond every declared value so the declared ones stay in sync.
     */
    private static final Map<String, List<String>> KNOWN_UNDECLARED = new LinkedHashMap<>();
    static {
        // INCREMENT_BY_FIT was added to StepMode without a matching <enum> in attrs.xml
        KNOWN_UNDECLARED.put("domainStepMode", List.of("INCREMENT_BY_FIT"));
        KNOWN_UNDECLARED.put("rangeStepMode", List.of("INCREMENT_BY_FIT"));
    }

    private Map<String, EnumAttr> enumAttrs;

    @Before
    public void setUp() {
        enumAttrs = enumAttrs();
    }

    private static Map<String, EnumAttr> enumAttrs() {
        final Map<String, EnumAttr> m = new LinkedHashMap<>();
        m.put("renderMode", new EnumAttr(R.attr.renderMode, Plot.RenderMode.class, Plot::getRenderMode));
        m.put("previewMode", new EnumAttr(R.attr.previewMode, XYPlot.PreviewMode.class,
                AttrsXmlEnumSyncTest::previewModeOf));
        m.put("domainStepMode", new EnumAttr(R.attr.domainStepMode, StepMode.class,
                plot -> plot.getDomainStepModel().getMode()));
        m.put("rangeStepMode", new EnumAttr(R.attr.rangeStepMode, StepMode.class,
                plot -> plot.getRangeStepModel().getMode()));

        m.put("lineLabelAlignTop", new EnumAttr(R.attr.lineLabelAlignTop, Paint.Align.class,
                plot -> plot.getGraph().getLineLabelStyle(Edge.TOP).getPaint().getTextAlign()));
        m.put("lineLabelAlignBottom", new EnumAttr(R.attr.lineLabelAlignBottom, Paint.Align.class,
                plot -> plot.getGraph().getLineLabelStyle(Edge.BOTTOM).getPaint().getTextAlign()));
        m.put("lineLabelAlignLeft", new EnumAttr(R.attr.lineLabelAlignLeft, Paint.Align.class,
                plot -> plot.getGraph().getLineLabelStyle(Edge.LEFT).getPaint().getTextAlign()));
        m.put("lineLabelAlignRight", new EnumAttr(R.attr.lineLabelAlignRight, Paint.Align.class,
                plot -> plot.getGraph().getLineLabelStyle(Edge.RIGHT).getPaint().getTextAlign()));

        m.put("graphAnchor", new EnumAttr(R.attr.graphAnchor, Anchor.class,
                plot -> plot.getGraph().getAnchor()));
        m.put("domainTitleAnchor", new EnumAttr(R.attr.domainTitleAnchor, Anchor.class,
                plot -> plot.getDomainTitle().getAnchor()));
        m.put("rangeTitleAnchor", new EnumAttr(R.attr.rangeTitleAnchor, Anchor.class,
                plot -> plot.getRangeTitle().getAnchor()));
        m.put("legendAnchor", new EnumAttr(R.attr.legendAnchor, Anchor.class,
                plot -> plot.getLegend().getAnchor()));

        m.put("graphHeightMode", new EnumAttr(R.attr.graphHeightMode, SizeMode.class,
                plot -> plot.getGraph().getSize().getHeight().getLayoutType(), R.attr.graphHeight));
        m.put("graphWidthMode", new EnumAttr(R.attr.graphWidthMode, SizeMode.class,
                plot -> plot.getGraph().getSize().getWidth().getLayoutType(), R.attr.graphWidth));
        m.put("domainTitleHeightMode", new EnumAttr(R.attr.domainTitleHeightMode, SizeMode.class,
                plot -> plot.getDomainTitle().getSize().getHeight().getLayoutType(), R.attr.domainTitleHeight));
        m.put("domainTitleWidthMode", new EnumAttr(R.attr.domainTitleWidthMode, SizeMode.class,
                plot -> plot.getDomainTitle().getSize().getWidth().getLayoutType(), R.attr.domainTitleWidth));
        m.put("rangeTitleHeightMode", new EnumAttr(R.attr.rangeTitleHeightMode, SizeMode.class,
                plot -> plot.getRangeTitle().getSize().getHeight().getLayoutType(), R.attr.rangeTitleHeight));
        m.put("rangeTitleWidthMode", new EnumAttr(R.attr.rangeTitleWidthMode, SizeMode.class,
                plot -> plot.getRangeTitle().getSize().getWidth().getLayoutType(), R.attr.rangeTitleWidth));
        m.put("legendHeightMode", new EnumAttr(R.attr.legendHeightMode, SizeMode.class,
                plot -> plot.getLegend().getSize().getHeight().getLayoutType(), R.attr.legendHeight));
        m.put("legendWidthMode", new EnumAttr(R.attr.legendWidthMode, SizeMode.class,
                plot -> plot.getLegend().getSize().getWidth().getLayoutType(), R.attr.legendWidth));
        m.put("legendIconHeightMode", new EnumAttr(R.attr.legendIconHeightMode, SizeMode.class,
                plot -> plot.getLegend().getIconSize().getHeight().getLayoutType(), R.attr.legendIconHeight));
        m.put("legendIconWidthMode", new EnumAttr(R.attr.legendIconWidthMode, SizeMode.class,
                plot -> plot.getLegend().getIconSize().getWidth().getLayoutType(), R.attr.legendIconWidth));

        m.put("graphRotation", new EnumAttr(R.attr.graphRotation, Widget.Rotation.class,
                plot -> plot.getGraph().getRotation()));

        m.put("graphHorizontalPositioning", new EnumAttr(R.attr.graphHorizontalPositioning,
                HorizontalPositioning.class,
                plot -> plot.getGraph().getPositionMetrics().getXPositionMetric().getLayoutType(),
                R.attr.graphHorizontalPosition));
        m.put("graphVerticalPositioning", new EnumAttr(R.attr.graphVerticalPositioning,
                VerticalPositioning.class,
                plot -> plot.getGraph().getPositionMetrics().getYPositionMetric().getLayoutType(),
                R.attr.graphVerticalPosition));
        m.put("domainTitleHorizontalPositioning", new EnumAttr(R.attr.domainTitleHorizontalPositioning,
                HorizontalPositioning.class,
                plot -> plot.getDomainTitle().getPositionMetrics().getXPositionMetric().getLayoutType(),
                R.attr.domainTitleHorizontalPosition));
        m.put("domainTitleVerticalPositioning", new EnumAttr(R.attr.domainTitleVerticalPositioning,
                VerticalPositioning.class,
                plot -> plot.getDomainTitle().getPositionMetrics().getYPositionMetric().getLayoutType(),
                R.attr.domainTitleVerticalPosition));
        m.put("rangeTitleHorizontalPositioning", new EnumAttr(R.attr.rangeTitleHorizontalPositioning,
                HorizontalPositioning.class,
                plot -> plot.getRangeTitle().getPositionMetrics().getXPositionMetric().getLayoutType(),
                R.attr.rangeTitleHorizontalPosition));
        m.put("rangeTitleVerticalPositioning", new EnumAttr(R.attr.rangeTitleVerticalPositioning,
                VerticalPositioning.class,
                plot -> plot.getRangeTitle().getPositionMetrics().getYPositionMetric().getLayoutType(),
                R.attr.rangeTitleVerticalPosition));
        m.put("legendHorizontalPositioning", new EnumAttr(R.attr.legendHorizontalPositioning,
                HorizontalPositioning.class,
                plot -> plot.getLegend().getPositionMetrics().getXPositionMetric().getLayoutType(),
                R.attr.legendHorizontalPosition));
        m.put("legendVerticalPositioning", new EnumAttr(R.attr.legendVerticalPositioning,
                VerticalPositioning.class,
                plot -> plot.getLegend().getPositionMetrics().getYPositionMetric().getLayoutType(),
                R.attr.legendVerticalPosition));
        return m;
    }

    @Test
    public void everyDeclaredEnumValue_mapsToJavaEnumOfSameOrdinal() throws Exception {
        final Map<String, Map<String, Integer>> declared = parseEnumAttrs(attrsXml());
        assertFalse("no enum attrs parsed from attrs.xml", declared.isEmpty());

        for (Map.Entry<String, Map<String, Integer>> attr : declared.entrySet()) {
            final String attrName = attr.getKey();
            final EnumAttr enumAttr = enumAttrs.get(attrName);
            assertNotNull("attrs.xml enum attr '" + attrName
                    + "' is not covered by AttrsXmlEnumSyncTest.enumAttrs()", enumAttr);

            final Enum<?>[] constants = enumAttr.enumClass.getEnumConstants();
            final List<String> undeclared = KNOWN_UNDECLARED.getOrDefault(attrName, List.of());
            assertEquals("number of <enum> values declared for " + attrName + " vs "
                    + enumAttr.enumClass.getSimpleName() + " (undeclared: " + undeclared + ")",
                    constants.length - undeclared.size(), attr.getValue().size());
            for (String name : undeclared) {
                final Enum<?> constant = Enum.valueOf((Class) enumAttr.enumClass, name);
                assertTrue(attrName + ": undeclared constant " + name + " must follow all declared values",
                        constant.ordinal() >= attr.getValue().size());
            }

            for (Map.Entry<String, Integer> entry : attr.getValue().entrySet()) {
                final String xmlName = entry.getKey();
                final int xmlValue = entry.getValue();
                assertTrue(attrName + "=" + xmlName + " declares out of range value " + xmlValue,
                        xmlValue >= 0 && xmlValue < constants.length);

                final AttributeSetBuilder builder = Robolectric.buildAttributeSet()
                        .addAttribute(enumAttr.attrId, xmlName);
                if (enumAttr.companionAttrId != 0) {
                    // a value valid for absolute, relative and fill modes alike:
                    builder.addAttribute(enumAttr.companionAttrId, "0.5");
                }
                final XYPlot plot = new XYPlot(getContext(), builder.build());
                final Enum<?> actual = enumAttr.reader.read(plot);
                assertEquals(attrName + "=\"" + xmlName + "\" (value " + xmlValue + ") resolved to "
                        + actual + " (ordinal " + actual.ordinal() + ")", xmlValue, actual.ordinal());
            }
        }

        // and nothing in the map that has disappeared from attrs.xml:
        for (String attrName : enumAttrs.keySet()) {
            assertTrue("attr '" + attrName + "' is no longer an enum attr in attrs.xml",
                    declared.containsKey(attrName));
        }
    }

    @Test
    public void lineLabelsFlags_matchEdgeValues() throws Exception {
        final Map<String, Integer> flags = parseFlagAttr(attrsXml(), "lineLabels");
        assertEquals(Edge.values().length, flags.size());

        for (Map.Entry<String, Integer> flag : flags.entrySet()) {
            final Edge edge = Edge.valueOf(flag.getKey().toUpperCase());
            assertEquals("lineLabels flag " + flag.getKey(), flag.getValue().intValue(), edge.getValue());

            final XYGraphWidget graph = new XYPlot(getContext(), Robolectric.buildAttributeSet()
                    .addAttribute(R.attr.lineLabels, flag.getKey()).build()).getGraph();
            for (Edge e : new Edge[]{Edge.TOP, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT}) {
                assertEquals("lineLabels=" + flag.getKey() + " enables " + e,
                        e == edge, graph.isLineLabelEnabled(e));
            }
        }
    }

    private static XYPlot.PreviewMode previewModeOf(XYPlot plot) {
        // there is no public getter; the mode is only consumed in edit mode
        try {
            final Field field = XYPlot.class.getDeclaredField("previewMode");
            field.setAccessible(true);
            return (XYPlot.PreviewMode) field.get(plot);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static File attrsXml() {
        final String[] candidates = {
                "src/main/res/values/attrs.xml",
                "androidplot-core/src/main/res/values/attrs.xml"};
        for (String candidate : candidates) {
            final File file = new File(candidate);
            if (file.isFile()) {
                return file;
            }
        }
        fail("attrs.xml not found relative to " + new File(".").getAbsolutePath());
        return null;
    }

    /** attr name -> (enum name -> value), for every top level attr that declares enum children. */
    private static Map<String, Map<String, Integer>> parseEnumAttrs(File attrsXml) throws Exception {
        final Map<String, Map<String, Integer>> result = new LinkedHashMap<>();
        for (Element attr : childAttrs(attrsXml)) {
            final Map<String, Integer> values = childValues(attr, "enum");
            if (!values.isEmpty()) {
                result.put(attr.getAttribute("name"), values);
            }
        }
        return result;
    }

    private static Map<String, Integer> parseFlagAttr(File attrsXml, String name) throws Exception {
        for (Element attr : childAttrs(attrsXml)) {
            if (name.equals(attr.getAttribute("name"))) {
                final Map<String, Integer> values = childValues(attr, "flag");
                assertFalse("no flags declared for " + name, values.isEmpty());
                return values;
            }
        }
        fail("no top level attr named " + name);
        return null;
    }

    private static List<Element> childAttrs(File attrsXml) throws Exception {
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(attrsXml);
        final List<Element> attrs = new ArrayList<>();
        final NodeList children = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && "attr".equals(node.getNodeName())) {
                attrs.add((Element) node);
            }
        }
        return attrs;
    }

    private static Map<String, Integer> childValues(Element attr, String tag) {
        final Map<String, Integer> values = new LinkedHashMap<>();
        final NodeList children = attr.getElementsByTagName(tag);
        for (int i = 0; i < children.getLength(); i++) {
            final Element child = (Element) children.item(i);
            values.put(child.getAttribute("name"), Integer.parseInt(child.getAttribute("value")));
        }
        return values;
    }
}
