/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.ipojo.runtime.bad.components;

import org.apache.felix.ipojo.runtime.bad.services.CheckService;

import java.util.Properties;

public class LifecycleControllerTest implements CheckService {
    
    private boolean m_state = true;
    private String m_conf;
    
    public void setConf(String newConf) {
        if (newConf.equals("foo")) {
            m_state = true;
        } else {
            m_state = false;
        }
    }

    public boolean check() {
        return m_state;
    }

    public Properties getProps() {
       Properties props = new Properties();
       props.put("conf", m_conf);
       props.put("state", new Boolean(m_state));
       return props;
    }
}

