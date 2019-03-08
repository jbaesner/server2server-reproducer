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
package org.wildfly.application;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;

@Startup
@Singleton
public class RedundancyNmsService {

    private final Logger logger = Logger.getLogger(RedundancyNmsService.class);

    private static final long SLEEP_TIME = Long.parseLong(System.getProperty("sleep.time", "30000")); // default 30sec
    private static final String JBOSS_NODE_NAME = System.getProperty("jboss.node.name");

    // app-name/module-name/distinct-name/bean-name!bean-interface
    private final String EJB_JNDI_NAME_TEMPLATE = "ejb:server2server/server2server-ejb/%s/EjbNmsRedundancyBean!org.wildfly.application.EjbNmsRedundancy";

    // get some parameters provided by system properties
    String distinctName = System.getProperty("remote.distinct.name", "");
    String jndiName = String.format(EJB_JNDI_NAME_TEMPLATE, distinctName);

    EjbNmsRedundancy bean;

    @Resource
    TimerService timerSvc;

    @PostConstruct
    public void setup() {

        // setup a timer configuration
        TimerConfig timerConfig = new TimerConfig("repeating-timer", false);
        timerSvc.createIntervalTimer(1000l, SLEEP_TIME, timerConfig);

        // lookup the EjbNmsRedundancyBean
        Context ctx = null;
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            ctx = new InitialContext(props);

            logger.debugf("used jndiName '%s'", jndiName);
            
            bean = (EjbNmsRedundancy) ctx.lookup(jndiName);

        } catch (NamingException e) {
            logger.error("Unable to lookup EjbNmsRedundancyBean", e);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    logger.trace("Unable to close Context", e);
                }
                ctx = null;
            }
        }
    }

    @Timeout
    public void timeout() {

        String jbossNodeName = bean.ping(JBOSS_NODE_NAME);
        logger.infof("got answer on ping from '%s'", jbossNodeName);

//        Context ctx = null;
//
//        try {
//            Properties props = new Properties();
//            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.application.naming.client.WildFlyInitialContextFactory");
//            ctx = new InitialContext(props);
//
//            EjbNmsRedundancy bean = (EjbNmsRedundancy) ctx.lookup(jndiName);
//
//            String jbossNodeName = bean.ping(JBOSS_NODE_NAME);
//
//            logger.infof("got answer on ping from '%s'", jbossNodeName);
//
//        } catch (NamingException e) {
//            logger.error("Unable to invoke EjbNmsRedundancyBean", e);
//        } finally {
//            if (ctx != null) {
//                try {
//                    ctx.close();
//                } catch (NamingException e) {
//                    logger.trace("Unable to close Context", e);
//                }
//                ctx = null;
//            }
//        }
    }
}
