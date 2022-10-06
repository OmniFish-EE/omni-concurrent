/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2022 OmniFish
 * Copyright (c) [2022] Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package ee.omnifish.concurrent.data;

import java.util.Properties;

import ee.omnifish.concurrent.api.GfManagedScheduledExecutorDefinition;

public class ManagedScheduledExecutorDefinitionData implements GfManagedScheduledExecutorDefinition {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String context;
    // FIXME: rename to hungAfterSeconds
    private long hungTaskThreshold;
    // FIXME: rename to maximumPoolSize
    private int maxAsync = Integer.MAX_VALUE;
    private Properties properties = new Properties();

    @Override
    public String getName() {
        return name;
    }


    @Override
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String getDescription() {
        return description;
    }


    @Override
    public String getContext() {
        return context;
    }


    @Override
    public void setContext(String context) {
        this.context = context;
    }


    @Override
    public long getHungTaskThreshold() {
        return hungTaskThreshold;
    }


    @Override
    public void setHungTaskThreshold(long hungTaskThreshold) {
        this.hungTaskThreshold = hungTaskThreshold;
    }


    @Override
    public int getMaxAsync() {
        return maxAsync;
    }


    @Override
    public void setMaxAsync(int maxAsync) {
        this.maxAsync = maxAsync;
    }


    @Override
    public Properties getProperties() {
        return properties;
    }


    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }


    @Override
    public void addManagedScheduledExecutorDefinitionDescriptor(String name, String value) {
        properties.put(name, value);
    }


    @Override
    public String toString() {
        return super.toString() + "[" + getName() + "]";
    }
}