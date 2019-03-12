package org.wildfly.application;

import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.CommandContextFactory;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.wildfly.clustering.group.Group;
import org.wildfly.clustering.group.Node;

/**
 * A singleton service to enable and disable a custom singleton deplyoment folder
 *
 * @author Joerg Baesner
 */
public class DeploymentSingletonService implements Service<Node> {

    private enum CliCommand {
        ADD_SINGLETON_DEPLOYMENT_FOLDER(
                "/subsystem=deployment-scanner/scanner=singleton-deployments:add(path=\"singleton-deployments\", relative-to=\"jboss.server.base.dir\", scan-interval=5000)"), //
        UNDEPLOY("/deployment=server2server.ear:undeploy()"), //
        REMOVE_SINGLETON_DEPLOYMENT_FOLDER("/subsystem=deployment-scanner/scanner=singleton-deployments:remove()");

        private String command;

        private CliCommand(String command) {
            this.command = command;
        }

        public String getCommand() {
            return this.command;
        }
    }

    private static final Logger logger = Logger.getLogger(DeploymentSingletonService.class);
    private InjectedValue<Group> group;

    DeploymentSingletonService(InjectedValue<Group> group) {
        this.group = group;
    }

    @Override
    public void start(StartContext context) throws StartException {
        logger.infof("Singleton service is starting on node '%s'.", this.group.getValue().getLocalNode());
        executeCliCommand(CliCommand.ADD_SINGLETON_DEPLOYMENT_FOLDER);
    }

    @Override
    public void stop(StopContext context) {
        logger.infof("Singleton service is stopping on node '%s'.", this.group.getValue().getLocalNode());
        executeCliCommand(CliCommand.UNDEPLOY, CliCommand.REMOVE_SINGLETON_DEPLOYMENT_FOLDER);
    }

    public Node getValue() throws IllegalStateException, IllegalArgumentException {
        return this.group.getValue().getLocalNode();
    }

    public void executeCliCommand(CliCommand... cliCommands) {
        CommandContext ctx;

        try {
            ctx = CommandContextFactory.getInstance().newCommandContext("admin", new char[] {'a', 'd', 'm', 'i', 'n', '-', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd'});
            ctx.connectController();
            for (CliCommand cliCommand : cliCommands) {
                String command = cliCommand.getCommand();
                logger.infof("CLI command='%s'", command);
                ctx.handle(command);
            }
            ctx.terminateSession();
        } catch (Exception e) {
            logger.error("Unable to execute cliCommand", e);
        }
    }
}
