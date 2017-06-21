package uk.co.riifactions.shop.common.service;

import com.google.inject.Injector;
import uk.co.riifactions.shop.common.AbstractLoader;

/**
 * Handles service loading/unloading functionality.
 *
 * @author Thortex
 */
public class ServiceLoader extends AbstractLoader<Service> {

    public ServiceLoader(Injector injector, String packageName) {
        super(injector, packageName, Service.class);
    }

    @Override
    public void startAll() {
        getInstances().forEach(Service::start);
    }

    @Override
    public void stopAll() {
        getInstances().forEach(Service::stop);
    }

}
