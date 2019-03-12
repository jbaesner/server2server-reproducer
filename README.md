# wildfly-server2server

1.) Install a fresh JBoss EAP 7.2.0

2.) copy the server configuration

    cp $PROJECT_ROOT/config/standalone-ha-720.xml $JBOSS_HOME/standalone/configuration/standalone-ha.xml
     
3.) Add a user 'remote-user' with password 'remote-password' to the server ApplicationRealm

    $JBOSS_HOME/bin/add-user.sh -a -u remote-user -p remote-password
    
4.) Add a user 'admin' with password 'admin-password' to the server ManagementRealm

    $JBOSS_HOME/bin/add-user.sh -m -u admin -p admin-password

5.) Build the project and copy the deployable:

    $PROJECT_ROOT/ear/target/server2server.ear $JBOSS_HOME/standalone/singleton-deployments
    $PROJECT_ROOT/haservice/target/server2server-haservice.jar $JBOSS_HOME/standalone/deployments
    
6.) duplicate the standalone folder twice:

    cp -a $JBOSS_HOME/standalone $JBOSS_HOME/node1
    cp -a $JBOSS_HOME/standalone $JBOSS_HOME/node2
    
7.) start two nodes

    $JBOSS_HOME/bin/standalone.sh -c standalone-ha.xml -Djboss.node.name=node1 -Djboss.server.base.dir=$JBOSS_HOME/node1 -Droc.port=4747 -Dremote.distinct.name=node2
    $JBOSS_HOME/bin/standalone.sh -c standalone-ha.xml -Djboss.node.name=node2 -Djboss.server.base.dir=$JBOSS_HOME/node2 -Djboss.socket.binding.port-offset=300 -Droc.port=4447 -Dremote.distinct.name=node1
