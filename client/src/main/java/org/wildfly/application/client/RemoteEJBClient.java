/*
 * Copyright 2019 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.application.client;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.wildfly.application.EjbNmsRedundancy;

/**
 * Don't forget the below System property on starting the client ;-)
 * <pre>
 * -Djava.util.logging.manager=org.jboss.logmanager.LogManager
 * </pre>
 * 
 * @author jbaesner
 *
 */
public class RemoteEJBClient {

    private static final Logger logger = Logger.getLogger(RemoteEJBClient.class);
    
    public static void main(String[] args) {
        RemoteEJBClient instance = new RemoteEJBClient();
        instance.ping();
    }

    private void ping() {

        try {
            Context ctx = getIntialContext();

            final String lookupStringNode1 = "ejb:server2server/server2server-ejb/node1/EjbNmsRedundancyBean!org.wildfly.application.EjbNmsRedundancy";
            final String lookupStringNode2 = "ejb:server2server/server2server-ejb/node2/EjbNmsRedundancyBean!org.wildfly.application.EjbNmsRedundancy";

            EjbNmsRedundancy bean = (EjbNmsRedundancy) ctx.lookup(lookupStringNode1);
            
            String jbossNodeName = bean.ping("standalone-client");
            logger.infof("got answer on ping from '%s'", jbossNodeName);
            
        } catch (NamingException e) {
            logger.error("Unable to invoke EjbNmsRedundancyBean", e);
        }

    }

    public Context getIntialContext() throws NamingException {

        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        p.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
        p.put(Context.SECURITY_PRINCIPAL, "remote-user");
        p.put(Context.SECURITY_CREDENTIALS, "remote-password");
        Context ctx = new InitialContext(p);

        return ctx;
    }
}
