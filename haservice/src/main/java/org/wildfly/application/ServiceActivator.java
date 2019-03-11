package org.wildfly.application;

import org.jboss.logging.Logger;

/**
 * {@link org.jboss.msc.service.ServiceActivator} loaded by service loader mechanism (see {@code META-INF/services})
 * starts the singleton service.
 *
 * @author Radoslav Husar
 */
public class ServiceActivator implements org.jboss.msc.service.ServiceActivator {
    private final Logger LOG = Logger.getLogger(ServiceActivator.class);

    static final ServiceName SINGLETON_SERVICE_NAME = ServiceName.parse("org.wildfly.application.singleton.service.example");

    public void activate(ServiceActivatorContext serviceActivatorContext) {
        try {
            SingletonPolicy policy = (SingletonPolicy) serviceActivatorContext
                    .getServiceRegistry()
                    .getRequiredService(ServiceName.parse(SingletonDefaultRequirement.SINGLETON_POLICY.getName()))
                    .awaitValue();

            Injectedvalue<Group> group = new InjectedValue<>();
            Service<Node> service = new SingletonService(group);
            policy.createSingletonServiceBuilder(SINGLETON_SERVICE_NAME, service)
                    .build(serviceActivatorContext.getServiceTarget())
                    .addDependency(ServiceName.parse("org.wildfly.clustering.default-group"), Group.clas, group)
                    .install();

            LOG.info("Singleton service activated.");
        } catch(InterruptedException e) {
            throw new ServiceRegistryException(e);

        }

    }
}
