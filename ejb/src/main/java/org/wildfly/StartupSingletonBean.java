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
package org.wildfly;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class StartupSingletonBean {

    private final Logger logger = LoggerFactory.getLogger(StartupSingletonBean.class);

    @EJB(lookup="ejb:server2server/server2server-ejb/SimpleRemoteBean!org.wildfly.SimpleRemote")
    SimpleRemote bean;
    
    @Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)
    public void timer() {
        
        String destination = System.getProperty("jboss.node.name");
        String target = bean.logMessageAndReturnJBossNodeName(String.format("called from destination '%s'", destination));
        
        logger.info("called SimpleRemote from destination '{}' on target '{}'", destination, target);
    }
}
