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

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(SimpleRemote.class)
public class SimpleRemoteBean implements SimpleRemote {

	Logger logger = LoggerFactory.getLogger(SimpleRemoteBean.class); 
	
	public String logMessageAndReturnJBossNodeName(String message) {
		
		String jbossNodeName = System.getProperty("jboss.node.name");
		
		logger.info(message);
		
		return jbossNodeName;
		
	}
}
