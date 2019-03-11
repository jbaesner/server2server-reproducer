package org.wildfly.application;

import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistryException;
import org.jboss.msc.value.InjectedValue;
import org.wildfly.clustering.group.Group;
import org.wildfly.clustering.group.Node;
import org.wildfly.clustering.singleton.SingletonDefaultRequirement;
import org.wildfly.clustering.singleton.SingletonPolicy;

/**
 * {@link org.jboss.msc.service.ServiceActivator} loaded by service loader mechanism (see {@code META-INF/services}) starts the
 * singleton service.
 *
 * @author Radoslav Husar
 */
public class DeploymentServiceActivator implements ServiceActivator {
    private final Logger LOG = Logger.getLogger(DeploymentServiceActivator.class);

    static final ServiceName SINGLETON_SERVICE_NAME = ServiceName.parse("org.wildfly.application.singleton.service.example");

    public void activate(ServiceActivatorContext serviceActivatorContext) {
        try {
            SingletonPolicy policy = (SingletonPolicy) serviceActivatorContext.getServiceRegistry()
                    .getRequiredService(ServiceName.parse(SingletonDefaultRequirement.SINGLETON_POLICY.getName())).awaitValue();

            InjectedValue<Group> group = new InjectedValue<>();
            Service<Node> service = new SingletonService(group);
            policy.createSingletonServiceBuilder(SINGLETON_SERVICE_NAME, service)
                    .build(serviceActivatorContext.getServiceTarget())
                    .addDependency(ServiceName.parse("org.wildfly.clustering.default-group"), Group.class, group).install();

            LOG.info("Singleton service activated.");
        } catch (InterruptedException e) {
            throw new ServiceRegistryException(e);
        }
    }
}
