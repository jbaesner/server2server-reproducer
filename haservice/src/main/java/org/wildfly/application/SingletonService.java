package org.wildfly.application;

import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.wildfly.clustering.group.Group;
import org.wildfly.clustering.group.Node;

/**
 * A singleton service
 *
 * @author Radoslav Husar
 */
public class SingletonService implements Service<Node> {

    private static final Logger logger = Logger.getLogger(SingletonService.class);
    private InjectedValue<Group> group;

    SingletonService(InjectedValue<Group> group) {
        this.group = group;
    }

    @Override
    public void start(StartContext context) throws StartException {
        logger.infof("Singleton service is starting on node '%s'.", this.group.getValue().getLocalNode());
    }

    @Override
    public void stop(StopContext context) {
        logger.infof("Singleton service is stopping on node '%s'.", this.group.getValue().getLocalNode());
    }

    public Node getValue() throws IllegalStateException, IllegalArgumentException {
        return this.group.getValue().getLocalNode();
    }
}
