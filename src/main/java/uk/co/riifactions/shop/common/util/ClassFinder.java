package uk.co.riifactions.shop.common.util;

import uk.co.riifactions.shop.common.Blacklisted;
import uk.co.riifactions.shop.common.Prioritized;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for finding classes.
 *
 * @author Thortex
 */
public final class ClassFinder {

    private ClassFinder() {}

    /**
     * Get the subtypes of a class excluding {@link Blacklisted} classes and
     * adding {@link Prioritized} classes at the start so they are loaded first.
     * <p>
     * This depends completely on order of insertion for which we use {@link ArrayList}.
     *
     * @param packageName the package name
     * @param clazz       the class object
     * @param <T>         the type
     * @return the set of classes
     */
    public static <T> List<Class<? extends T>> getFixedSubtypesOf(String packageName, Class<T> clazz) {
        List<Class<? extends T>> result = new ArrayList<>();
        Set<Class<? extends T>> classes = getAllUnBlacklistedSubtypesOf(packageName, clazz);
        Iterator<Class<? extends T>> iterator = classes.iterator();
        // Add all Prioritized classes first
        iterator.forEachRemaining(o -> {
            if (o.isAnnotationPresent(Prioritized.class)) {
                result.add(o);
                iterator.remove();
            }
        });

        // Add the rest of the classes at the end
        result.addAll(classes);
        return result;
    }

    /**
     * Get all subtypes of a class excluding {@link Blacklisted} classes.
     *
     * @param packageName the package name
     * @param clazz       the class object
     * @param <T>         the type
     * @return the set of classes
     */
    public static <T> Set<Class<? extends T>> getAllUnBlacklistedSubtypesOf(String packageName, Class<T> clazz) {
        return getAllSubtypesOf(packageName, clazz).stream()
                .filter(o -> !o.isAnnotationPresent(Blacklisted.class))
                .collect(Collectors.toSet());
    }

    /**
     * Get all the subtypes of a class.
     *
     * @param packageName the package name
     * @param clazz       the class object
     * @param <T>         the type
     * @return the set of class subtypes
     */
    public static <T> Set<Class<? extends T>> getAllSubtypesOf(String packageName, Class<T> clazz) {
        return getReflection(packageName).getSubTypesOf(clazz);
    }

    /**
     * Get all classes in a package recursively.
     *
     * @param packageName the package name
     * @return the set of classes
     */
    public static Set<Class<?>> getAllClasses(String packageName) {
        return getReflection(packageName).getSubTypesOf(Object.class);
    }

    private static Reflections getReflection(String packageName) {
        List<ClassLoader> classLoadersList = new LinkedList<>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());
        return new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));
    }

}
