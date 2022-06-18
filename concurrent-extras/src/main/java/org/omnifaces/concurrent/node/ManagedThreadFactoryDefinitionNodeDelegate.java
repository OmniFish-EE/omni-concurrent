/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
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
// Portions Copyright [2022] [OmniFaces and/or its affiliates]
package org.omnifaces.concurrent.node;
import static org.omnifaces.concurrent.deployment.ConcurrencyConstants.MANAGED_EXECUTOR_NAME;
import static org.omnifaces.concurrent.deployment.ConcurrencyConstants.MANAGED_THREAD_FACTORY;
import static org.omnifaces.concurrent.deployment.ConcurrencyConstants.MANAGED_THREAD_FACTORY_CONTEXT_SERVICE_REF;
import static org.omnifaces.concurrent.deployment.ConcurrencyConstants.MANAGED_THREAD_FACTORY_NAME;
import static org.omnifaces.concurrent.deployment.ConcurrencyConstants.MANAGED_THREAD_FACTORY_PRIORITY;
import static org.omnifaces.concurrent.node.NodeUtils.appendChild;
import static org.omnifaces.concurrent.node.NodeUtils.appendTextChild;

import java.util.Map;

import org.omnifaces.concurrent.deployment.ManagedThreadFactoryDefinitionDescriptor;
import org.w3c.dom.Node;

public class ManagedThreadFactoryDefinitionNodeDelegate {

    ManagedThreadFactoryDefinitionDescriptor descriptor = null;

    public static String getQname() {
        return MANAGED_THREAD_FACTORY;
    }

    public String getHandlerAdMethodName() {
        return "addManagedThreadFactoryPropertyDescriptor";
    }

    public Map<String, String> getDispatchTable(Map<String, String> table) {
        table.put(MANAGED_THREAD_FACTORY_NAME, "setName");
        table.put(MANAGED_THREAD_FACTORY_CONTEXT_SERVICE_REF, "setContext");
        table.put(MANAGED_THREAD_FACTORY_PRIORITY, "setPriority");
        return table;
    }

    public Node getDescriptor(Node parent, String nodeName, ManagedThreadFactoryDefinitionDescriptor managedThreadFactoryDefinitionDescriptor) {
        Node node = appendChild(parent, nodeName);
        appendTextChild(node, MANAGED_EXECUTOR_NAME, managedThreadFactoryDefinitionDescriptor.getName());
        appendTextChild(node, MANAGED_THREAD_FACTORY_CONTEXT_SERVICE_REF, managedThreadFactoryDefinitionDescriptor.getContext());
        appendTextChild(node, MANAGED_THREAD_FACTORY_PRIORITY, String.valueOf(managedThreadFactoryDefinitionDescriptor.getPriority()));

        return node;
    }

    public ManagedThreadFactoryDefinitionDescriptor getDescriptor() {
        if (descriptor == null) {
            descriptor = new ManagedThreadFactoryDefinitionDescriptor();
        }

        return descriptor;
    }
}