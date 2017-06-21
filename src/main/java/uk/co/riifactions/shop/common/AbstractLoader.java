package uk.co.riifactions.shop.common;

import com.google.inject.Injector;
import uk.co.riifactions.shop.common.util.ClassFinder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract class for loading/unloading of services and commands.
 *
 * @author Thortex
 */
public abstract class AbstractLoader<T> {

    private final Injector injector;
    private final String packageName;
    private final Class<T> clazz;

    public AbstractLoader(Injector injector, String packageName, Class<T> clazz) {
        this.injector = injector;
        this.packageName = packageName;
        this.clazz = clazz;
    }

    /**
     * Get all the instances of the classes to load.
     *
     * @return the list of instances
     */
    public List<T> getInstances() {
        return ClassFinder.getFixedSubtypesOf(packageName, clazz).stream()
            .map(injector::getInstance)
            .collect(Collectors.toList());
    }

    public abstract void startAll();

    public abstract void stopAll();

}
