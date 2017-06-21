package uk.co.riifactions.shop.common.util;

import java.lang.reflect.Field;

/**
 * Reflection utilities.
 *
 * @author Thortex
 */
public final class Reflector {

    private Reflector() {}

    /**
     * Set a field.
     *
     * @param clazz     the class object
     * @param fieldName the field's name
     * @param instance  the class instance
     * @param obj       the new handler
     */
    public static void setField(Class<?> clazz, String fieldName, Object instance, Object obj) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, obj);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a field.
     *
     * @param clazz     the class object
     * @param instance  the class instance
     * @param fieldName the field's name
     * @param <T>       the object type
     * @return the object or null if nothing was found
     */
    @SuppressWarnings("unchecked")
    public static <T> T getField(Class<?> clazz, Object instance, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(instance);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
