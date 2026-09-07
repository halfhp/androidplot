// SPDX-License-Identifier: Apache-2.0
package com.androidplot.util.fig;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;

import org.xmlpull.v1.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Configures an object from XML by mapping attribute names onto setter methods.
 *
 * Originally the standalone library com.halfhp.fig:figlib (Apache 2.0, by the
 * Androidplot author); folded into Androidplot as it is the sole consumer.
 */
public abstract class Fig {

    private static final String CFG_ELEMENT_NAME = "config";
    private static final String PATH_SEPARATOR = "/";
    private static final String DOT_SEPARATOR = ".";
    private static final String RESOURCE_ID_PREFIX = "@";
    private static final String GETTER_PREFIX = "get";
    private static final String SETTER_PREFIX = "set";
    private static final String COLOR_TRANSPARENT_COMPRESSED_HEX = "#0";
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^-?\\d+$");
    private static final String RES_TYPE_COLOR = "color";
    private static final String RES_TYPE_DIMEN = "dimen";

    /**
     * Parse a resource id either as a raw string ref such as '@dimen/some_resource' or
     * as a reference such as '@1234342'.
     *
     * @param ctx
     * @param value
     * @return The int resourceId corresponding to the input.
     * @throws IllegalArgumentException If the resoureId cannot be obtained from the input.
     */
    private static int parseResId(Context ctx, String value) {
        if (value.startsWith(RESOURCE_ID_PREFIX)) {
            if (value.contains(PATH_SEPARATOR)) {
                String[] split = value.split(PATH_SEPARATOR);
                String pack = split[0].replace(RESOURCE_ID_PREFIX, "");
                String name = split[1];
                return ctx.getResources().getIdentifier(name, pack, ctx.getPackageName());
            } else {
                // starting with gradle tools 3.0.1, it appears that dimen refs are now inlined
                // at compile time using the form '@intRef'
                return Integer.parseInt(value.substring(1));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Treats value as an int parameter.  It may be a plain integer, a resource reference such
     * as '@color/foo' or '@2131034163' (the compiled form AAPT emits for references in res/xml
     * files), or a color in one of the forms accepted by {@link Color#parseColor(String)}.
     */
    private static int parseIntAttr(Context ctx, String value) {
        if (INTEGER_PATTERN.matcher(value).matches()) {
            return Integer.parseInt(value);
        } else if (value.startsWith(RESOURCE_ID_PREFIX)) {
            return resolveIntResource(ctx, value);
        } else {
            // it's a hex val
            if (value.equalsIgnoreCase(COLOR_TRANSPARENT_COMPRESSED_HEX)) {
                // for some reason, since gradle tools 3.x.x #00000000 gets
                // converted to #0.
                return Color.TRANSPARENT;
            } else {
                // value starts with a non-digit char so its either a color, or an invalid value
                return Color.parseColor(value);
            }
        }
    }

    /**
     * Resolves a resource reference to an int according to the resource's type: colors yield
     * the color value, dimensions the pixel size, and anything else is read as an integer.
     */
    @SuppressWarnings("deprecation") // Resources.getColor(int): minSdk predates the themed variant
    private static int resolveIntResource(Context ctx, String value) {
        final Resources res = ctx.getResources();
        final int resId = parseResId(ctx, value);
        final String type;
        try {
            type = res.getResourceTypeName(resId);
        } catch (Resources.NotFoundException e) {
            throw new IllegalArgumentException("Unable to resolve resource reference: " + value, e);
        }
        if (RES_TYPE_COLOR.equals(type)) {
            return res.getColor(resId);
        } else if (RES_TYPE_DIMEN.equals(type)) {
            return res.getDimensionPixelSize(resId);
        } else {
            return res.getInteger(resId);
        }
    }

    /**
     * Treats value as a float parameter.  First value is tested to see whether
     * it contains a resource identifier.  Failing that, it is tested to see whether
     * a dimension suffix (dp, em, mm etc.) exists.  Failing that, it is evaluated as
     * a plain old float.
     *
     * @param ctx
     * @param value
     * @return
     */
    private static float parseFloatAttr(Context ctx, String value) {
        try {
            return ctx.getResources().getDimension(parseResId(ctx, value));
        } catch (IllegalArgumentException e1) {
            try {
                return FigUtils.stringToDimension(ctx, value);
            } catch (Exception e2) {
                return Float.parseFloat(value);
            }
        }
    }

    private static String parseStringAttr(Context ctx, String value) {
        try {
            return ctx.getResources().getString(parseResId(ctx, value));
        } catch (IllegalArgumentException e1) {
            return value;
        }
    }

    /**
     * Finds a public method by case insensitive name.  Class.getMethods() returns overloads in
     * no particular order, so when several methods share the name the one whose parameter list
     * has the expected count and consists only of types this configurator can inflate is
     * preferred (eg. Paint.setColor(int) over Paint.setColor(long)); otherwise the first match
     * is returned so that the caller can report the mismatch.
     */
    private static Method getMethodByName(Class clazz, final String methodName, int paramCount)
            throws NoSuchMethodException {
        Method fallback = null;
        for (Method method : clazz.getMethods()) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                final Class[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == paramCount && isInflatable(paramTypes)) {
                    return method;
                }
                if (fallback == null) {
                    fallback = method;
                }
            }
        }
        if (fallback != null) {
            return fallback;
        }
        throw new NoSuchMethodException("No such public method (case insensitive): " +
                methodName + " in " + clazz);
    }

    private static boolean isInflatable(Class[] paramTypes) {
        for (Class param : paramTypes) {
            if (!(Enum.class.isAssignableFrom(param)
                    || param == Float.TYPE || param == Float.class
                    || param == Integer.TYPE || param == Integer.class
                    || param == Boolean.TYPE || param == Boolean.class
                    || param == String.class)) {
                return false;
            }
        }
        return true;
    }

    private static Method getSetter(Class clazz, final String fieldId, int paramCount)
            throws NoSuchMethodException {
        return getMethodByName(clazz, SETTER_PREFIX + fieldId, paramCount);
    }

    private static Method getGetter(Class clazz, final String fieldId) throws NoSuchMethodException {
        return getMethodByName(clazz, GETTER_PREFIX + fieldId, 0);
    }

    /**
     * Returns the object containing the field specified by path.
     *
     * @param obj
     * @param path Path through member hierarchy to the destination field.
     * @return null if the object at path cannot be found.
     * @throws java.lang.reflect.InvocationTargetException
     * @throws IllegalAccessException
     */
    static Object getObjectContaining(Object obj, String path) throws
            InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (obj == null) {
            throw new NullPointerException("Attempt to call getObjectContaining(...) " +
                    "on a null Object instance.  Path was: " + path);
        }
        int separatorIndex = path.indexOf(DOT_SEPARATOR);
        if (separatorIndex > 0) {
            String lhs = path.substring(0, separatorIndex);
            String rhs = path.substring(separatorIndex + 1, path.length());
            Method m = getGetter(obj.getClass(), lhs);
            if (m == null) {
                throw new NoSuchMethodException("No getter found for field: " + lhs + " within " + obj.getClass());
            }
            Object o = m.invoke(obj);
            return getObjectContaining(o, rhs);
        } else {
            return obj;
        }
    }

    private static Object[] inflateParams(Context ctx, Class[] params, String[] vals) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Object[] out = new Object[params.length];
        int i = 0;
        for (Class param : params) {
            if (Enum.class.isAssignableFrom(param)) {
                out[i] = param.getMethod("valueOf", String.class).invoke(null, vals[i].toUpperCase());
            } else if (param.equals(Float.TYPE) || param == Float.class) {
                out[i] = parseFloatAttr(ctx, vals[i]);
            } else if (param.equals(Integer.TYPE) || param == Integer.class) {
                out[i] = parseIntAttr(ctx, vals[i]);
            } else if (param.equals(Boolean.TYPE) || param == Boolean.class) {
                out[i] = Boolean.valueOf(vals[i]);
            } else if (param.equals(String.class)) {
                out[i] = parseStringAttr(ctx, vals[i]);
            } else {
                throw new IllegalArgumentException("Error inflating XML: Setter requires param of unsupported type: " + param);
            }
            i++;
        }
        return out;
    }

    /**
     * Configure from a File.
     *
     * @param ctx
     * @param obj  The object to be configured
     * @param file The file containing the config xml.
     * @throws FigException
     */
    public static void configure(Context ctx, Object obj, File file) throws FigException {

        try {
            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
            xppf.setNamespaceAware(true);
            XmlPullParser xpp = xppf.newPullParser();
            FileInputStream fis = new FileInputStream(file);
            xpp.setInput(fis, null);
            configure(ctx, obj, xpp);
        } catch (FileNotFoundException e) {
            throw new FigException("Failed to open file for parsing", e);
        } catch (XmlPullParserException e) {
            throw new FigException("Error while parsing file", e);
        }
    }

    /**
     * @param ctx
     * @param obj
     * @param params
     * @throws FigException
     */
    public static void configure(Context ctx, Object obj, HashMap<String, String> params) throws FigException {
        for (String key : params.keySet()) {
            configure(ctx, obj, key, params.get(key));
        }
    }

    /**
     * @param ctx
     * @param obj
     * @param xrp
     * @throws FigException
     */
    private static void configure(Context ctx, Object obj, XmlPullParser xrp) throws FigException {
        try {
            HashMap<String, String> params = new HashMap<>();
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                xrp.next();
                String name = xrp.getName();
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    if (name.equalsIgnoreCase(CFG_ELEMENT_NAME))
                        for (int i = 0; i < xrp.getAttributeCount(); i++) {
                            final String attrName = xrp.getAttributeName(i);
                            final String attrVal = xrp.getAttributeValue(i);
                            params.put(attrName, attrVal);
                        }
                    break;
                }
            }
            configure(ctx, obj, params);
        } catch (XmlPullParserException e) {
            throw new FigException("Error while parsing XML configuration", e);
        } catch (IOException e) {
            throw new FigException("Error while parsing XML configuration", e);
        }
    }

    /**
     * Configure from a res/xml resource
     *
     * @param ctx
     * @param obj       The object to be configured
     * @param xmlFileId ID of an XML config file in /res/xml
     * @throws FigException
     */
    public static void configure(Context ctx, Object obj, int xmlFileId) throws FigException {
        XmlResourceParser xrp = ctx.getResources().getXml(xmlFileId);
        try {
            configure(ctx, obj, xrp);
        } finally {
            xrp.close();
        }
    }

    /**
     * Recursively descend into an object using key as the pathway and invoking the corresponding setter
     * if one exists.
     *
     * @param key
     * @param value
     * @throws FigException
     */
    private static void configure(Context ctx, Object obj, String key, String value) throws FigException {
        try {
            Object o = getObjectContaining(obj, key);
            if (o != null) {
                int idx = key.lastIndexOf(DOT_SEPARATOR);
                String fieldId = idx > 0 ? key.substring(idx + 1, key.length()) : key;

                // split on "|"
                // TODO: add support for String args containing a '|'
                String[] paramStrs = value.split("\\|");

                Method m = getSetter(o.getClass(), fieldId, paramStrs.length);
                Class[] paramTypes = m.getParameterTypes();
                // TODO: add support for generic type params
                if (paramTypes.length >= 1) {
                    if (paramStrs.length == paramTypes.length) {
                        Object[] oa = inflateParams(ctx, paramTypes, paramStrs);
                        m.invoke(o, oa);
                    } else {
                        throw new IllegalArgumentException("Error inflating XML: Unexpected number of argments passed to \""
                                + m.getName() + "\".  Expected: " + paramTypes.length + " Got: " + paramStrs.length);
                    }
                } else {
                    // this is not a setter
                    throw new IllegalArgumentException("Error inflating XML: no setter method found for param \"" +
                            fieldId + "\".");
                }
            }
        } catch (IllegalAccessException e) {
            throw new FigException("Error while parsing key: " + key + " value: " + value, e);
        } catch (InvocationTargetException e) {
            throw new FigException("Error while parsing key: " + key + " value: " + value, e);
        } catch (NoSuchMethodException e) {
            throw new FigException("Error while parsing key: " + key + " value: " + value, e);
        }
    }
}

