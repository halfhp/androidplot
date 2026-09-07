// SPDX-License-Identifier: Apache-2.0
package com.androidplot;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Guards the nullability contract of the public API: every public or protected method and
 * constructor declared in a public class or interface under {@code com.androidplot} must carry
 * exactly one of {@link androidx.annotation.NonNull} / {@link androidx.annotation.Nullable} on
 * each reference-typed return value and parameter.  Without this, Kotlin callers see platform
 * types.
 * <p>
 * The androidx annotations have {@code CLASS} retention so they are invisible to reflection;
 * the compiled classes are read with ASM instead.  Classes are enumerated from the main source
 * tree (the working directory of unit tests is the module directory) and loaded through the test
 * classloader, which also picks up nested classes.
 */
public class NullabilityAnnotationsTest {

    private static final String NONNULL = "Landroidx/annotation/NonNull;";
    private static final String NULLABLE = "Landroidx/annotation/Nullable;";
    private static final Path SOURCE_ROOT = Paths.get("src", "main", "java");
    private static final String PACKAGE_PREFIX = "com.androidplot.";

    @Test
    public void everyPublicApiMemberIsAnnotated() throws Exception {
        List<Class<?>> classes = publicApiClasses();
        assertTrue("no classes found under " + SOURCE_ROOT.toAbsolutePath(), classes.size() > 50);

        TreeSet<String> offenders = new TreeSet<>();
        for (Class<?> clazz : classes) {
            ClassNode node = read(clazz);
            for (MethodNode method : node.methods) {
                if (!isApiMember(clazz, node, method)) {
                    continue;
                }
                check(clazz, method, offenders);
            }
        }
        if (!offenders.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(offenders.size())
                    .append(" public API return values / parameters lack a @NonNull or @Nullable annotation:\n");
            for (String offender : offenders) {
                sb.append("  ").append(offender).append('\n');
            }
            fail(sb.toString());
        }
    }

    /**
     * An override may narrow a @Nullable return to @NonNull but must keep the parameter
     * annotations of the method it overrides, otherwise a caller holding the supertype sees a
     * different contract than one holding the subtype.
     */
    @Test
    public void overridesKeepTheSupertypeContract() throws Exception {
        List<Class<?>> classes = publicApiClasses();
        Map<Class<?>, ClassNode> nodes = new HashMap<>();
        for (Class<?> clazz : classes) {
            nodes.put(clazz, read(clazz));
        }
        TreeSet<String> offenders = new TreeSet<>();
        for (Class<?> clazz : classes) {
            ClassNode node = nodes.get(clazz);
            List<Class<?>> supertypes = new ArrayList<>();
            collectSupertypes(clazz, supertypes);
            for (MethodNode method : node.methods) {
                if (method.name.equals("<init>") || !isApiMember(clazz, node, method)) {
                    continue;
                }
                Set<String> descriptors = new HashSet<>();
                descriptors.add(method.desc);
                for (MethodNode other : node.methods) {
                    if ((other.access & Opcodes.ACC_BRIDGE) != 0 && other.name.equals(method.name)
                            && Type.getArgumentTypes(other.desc).length == Type.getArgumentTypes(method.desc).length) {
                        descriptors.add(other.desc);
                    }
                }
                for (Class<?> supertype : supertypes) {
                    ClassNode superNode = nodes.get(supertype);
                    if (superNode == null) {
                        continue;
                    }
                    for (MethodNode overridden : superNode.methods) {
                        if (overridden.name.equals(method.name) && descriptors.contains(overridden.desc)
                                && (overridden.access & Opcodes.ACC_BRIDGE) == 0
                                && (overridden.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) != 0) {
                            compare(clazz, method, supertype, overridden, offenders);
                        }
                    }
                }
            }
        }
        if (!offenders.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(offenders.size()).append(" overrides change the nullability contract of the overridden method:\n");
            for (String offender : offenders) {
                sb.append("  ").append(offender).append('\n');
            }
            fail(sb.toString());
        }
    }

    private static void compare(Class<?> clazz, MethodNode method, Class<?> supertype, MethodNode overridden,
                                TreeSet<String> offenders) {
        Type[] args = Type.getArgumentTypes(method.desc);
        String signature = clazz.getName() + "#" + method.name + "(" + describe(args) + ")";
        String superName = supertype.getSimpleName() + "." + method.name;
        boolean superReturnNonNull = has(NONNULL, overridden.visibleAnnotations, overridden.invisibleAnnotations);
        boolean returnNullable = has(NULLABLE, method.visibleAnnotations, method.invisibleAnnotations);
        if (superReturnNonNull && returnNullable) {
            offenders.add(signature + " return is @Nullable but " + superName + " returns @NonNull");
        }
        for (int i = 0; i < args.length; i++) {
            if (!isReference(args[i])) {
                continue;
            }
            String mine = nullability(method, i);
            String theirs = nullability(overridden, i);
            if (mine != null && theirs != null && !mine.equals(theirs)) {
                offenders.add(signature + " parameter " + i + " is " + mine + " but " + superName + " declares " + theirs);
            }
        }
    }

    private static String nullability(MethodNode method, int index) {
        List<AnnotationNode> visible = paramAnnotations(method.visibleParameterAnnotations, index);
        List<AnnotationNode> invisible = paramAnnotations(method.invisibleParameterAnnotations, index);
        if (has(NONNULL, visible, invisible)) {
            return "@NonNull";
        }
        if (has(NULLABLE, visible, invisible)) {
            return "@Nullable";
        }
        return null;
    }

    @SafeVarargs
    private static boolean has(String desc, List<AnnotationNode>... tables) {
        for (List<AnnotationNode> table : tables) {
            if (table == null) {
                continue;
            }
            for (AnnotationNode annotation : table) {
                if (desc.equals(annotation.desc)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void check(Class<?> clazz, MethodNode method, TreeSet<String> offenders) {
        Type[] args = Type.getArgumentTypes(method.desc);
        String signature = clazz.getName() + "#" + (method.name.equals("<init>") ? clazz.getSimpleName() : method.name)
                + "(" + describe(args) + ")";

        Type returnType = Type.getReturnType(method.desc);
        if (isReference(returnType)) {
            int count = countNullability(method.visibleAnnotations) + countNullability(method.invisibleAnnotations);
            if (count != 1) {
                offenders.add(signature + " return type " + returnType.getClassName()
                        + (count == 0 ? " is not annotated" : " has both @NonNull and @Nullable"));
            }
        }

        // javac omits synthetic leading parameters (e.g. the outer instance of an inner class
        // constructor) from the parameter annotation table, so align on the trailing parameters.
        int annotable = Math.max(method.visibleAnnotableParameterCount, method.invisibleAnnotableParameterCount);
        int skipped = annotable == 0 ? syntheticLeadingParams(clazz, method) : args.length - annotable;
        for (int i = skipped; i < args.length; i++) {
            if (!isReference(args[i])) {
                continue;
            }
            int annotationIndex = i - skipped;
            int count = countNullability(paramAnnotations(method.visibleParameterAnnotations, annotationIndex))
                    + countNullability(paramAnnotations(method.invisibleParameterAnnotations, annotationIndex));
            if (count != 1) {
                offenders.add(signature + " parameter " + i + " (" + args[i].getClassName() + ")"
                        + (count == 0 ? " is not annotated" : " has both @NonNull and @Nullable"));
            }
        }
    }

    private static int syntheticLeadingParams(Class<?> clazz, MethodNode method) {
        if (!method.name.equals("<init>")) {
            return 0;
        }
        if (clazz.isEnum()) {
            return 2;
        }
        return clazz.getEnclosingClass() != null && !Modifier.isStatic(clazz.getModifiers()) ? 1 : 0;
    }

    private static List<AnnotationNode> paramAnnotations(List<AnnotationNode>[] table, int index) {
        if (table == null || index < 0 || index >= table.length) {
            return null;
        }
        return table[index];
    }

    private static int countNullability(List<AnnotationNode> annotations) {
        if (annotations == null) {
            return 0;
        }
        int count = 0;
        for (AnnotationNode annotation : annotations) {
            if (NONNULL.equals(annotation.desc) || NULLABLE.equals(annotation.desc)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isReference(Type type) {
        return type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY;
    }

    private static String describe(Type[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            String name = args[i].getClassName();
            sb.append(name.substring(name.lastIndexOf('.') + 1));
        }
        return sb.toString();
    }

    /**
     * @return true if the method is part of the public API contract: public or protected, not
     * compiler generated, not an enum built-in and not an override of a method declared outside
     * com.androidplot (android.* / java.* framework methods keep their framework contract).
     */
    private static boolean isApiMember(Class<?> clazz, ClassNode node, MethodNode method) {
        if ((method.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
            return false;
        }
        if ((method.access & (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE)) != 0) {
            return false;
        }
        if (method.name.equals("<clinit>") || method.name.startsWith("lambda$")) {
            return false;
        }
        if (clazz.isEnum() && (method.name.equals("values") || method.name.equals("valueOf"))) {
            return false;
        }
        if (method.name.equals("<init>")) {
            return true;
        }
        if (overridesForeignMethod(clazz, method.name, method.desc)) {
            return false;
        }
        // an override of a generic framework method (e.g. Comparator.compare(T, T)) is reached
        // through a bridge method whose descriptor carries the erased framework signature
        for (MethodNode other : node.methods) {
            if ((other.access & Opcodes.ACC_BRIDGE) != 0 && other.name.equals(method.name)
                    && Type.getArgumentTypes(other.desc).length == Type.getArgumentTypes(method.desc).length
                    && overridesForeignMethod(clazz, other.name, other.desc)) {
                return false;
            }
        }
        return true;
    }

    private static boolean overridesForeignMethod(Class<?> clazz, String name, String desc) {
        Type[] args = Type.getArgumentTypes(desc);
        Class<?>[] params = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            params[i] = load(args[i], clazz.getClassLoader());
            if (params[i] == null) {
                return false;
            }
        }
        List<Class<?>> supertypes = new ArrayList<>();
        collectSupertypes(clazz, supertypes);
        for (Class<?> supertype : supertypes) {
            if (supertype.getName().startsWith(PACKAGE_PREFIX)) {
                continue;
            }
            try {
                supertype.getDeclaredMethod(name, params);
                return true;
            } catch (NoSuchMethodException ignored) {
                // keep looking
            }
        }
        return false;
    }

    private static void collectSupertypes(Class<?> clazz, List<Class<?>> out) {
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && !out.contains(superclass)) {
            out.add(superclass);
            collectSupertypes(superclass, out);
        }
        for (Class<?> iface : clazz.getInterfaces()) {
            if (!out.contains(iface)) {
                out.add(iface);
                collectSupertypes(iface, out);
            }
        }
        if (clazz.isInterface() && !out.contains(Object.class)) {
            out.add(Object.class);
        }
    }

    private static Class<?> load(Type type, ClassLoader loader) {
        switch (type.getSort()) {
            case Type.BOOLEAN: return boolean.class;
            case Type.BYTE: return byte.class;
            case Type.CHAR: return char.class;
            case Type.SHORT: return short.class;
            case Type.INT: return int.class;
            case Type.LONG: return long.class;
            case Type.FLOAT: return float.class;
            case Type.DOUBLE: return double.class;
            case Type.ARRAY: {
                Class<?> element = load(type.getElementType(), loader);
                if (element == null) {
                    return null;
                }
                int[] dims = new int[type.getDimensions()];
                return java.lang.reflect.Array.newInstance(element, dims).getClass();
            }
            default:
                try {
                    return Class.forName(type.getClassName(), false, loader);
                } catch (ClassNotFoundException e) {
                    return null;
                }
        }
    }

    private static ClassNode read(Class<?> clazz) throws IOException {
        String resource = clazz.getName().replace('.', '/') + ".class";
        try (InputStream in = clazz.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException("no bytecode found for " + clazz.getName());
            }
            ClassNode node = new ClassNode();
            new ClassReader(in).accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return node;
        }
    }

    /**
     * @return every public class / interface under com.androidplot in the main source tree,
     * including public nested classes of public classes, excluding anonymous, local and synthetic
     * classes.
     */
    private static List<Class<?>> publicApiClasses() throws Exception {
        List<Class<?>> result = new ArrayList<>();
        try (Stream<Path> files = Files.walk(SOURCE_ROOT)) {
            for (Path file : (Iterable<Path>) files::iterator) {
                String name = file.getFileName().toString();
                if (!name.endsWith(".java") || name.equals("package-info.java")) {
                    continue;
                }
                String relative = SOURCE_ROOT.relativize(file).toString();
                String className = relative.substring(0, relative.length() - ".java".length())
                        .replace(File.separatorChar, '.');
                if (!className.startsWith(PACKAGE_PREFIX)) {
                    continue;
                }
                collectPublicClasses(Class.forName(className, false, NullabilityAnnotationsTest.class.getClassLoader()), result);
            }
        }
        Collections.sort(result, (a, b) -> a.getName().compareTo(b.getName()));
        return result;
    }

    private static void collectPublicClasses(Class<?> clazz, List<Class<?>> out) {
        if (!Modifier.isPublic(clazz.getModifiers()) || clazz.isAnonymousClass()
                || clazz.isLocalClass() || clazz.isSynthetic()) {
            return;
        }
        out.add(clazz);
        for (Class<?> nested : clazz.getDeclaredClasses()) {
            collectPublicClasses(nested, out);
        }
    }
}
