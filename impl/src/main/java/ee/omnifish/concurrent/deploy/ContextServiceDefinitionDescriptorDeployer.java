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
package ee.omnifish.concurrent.deploy;
import jakarta.enterprise.concurrent.ContextService;
import jakarta.enterprise.concurrent.ContextServiceDefinition;
import jakarta.inject.Inject;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jvnet.hk2.annotations.Service;

import ee.omnifish.concurrent.api.ConcurrentRuntimeAdapter;
import ee.omnifish.concurrent.api.ContextServiceConfig;
import ee.omnifish.concurrent.api.GlassFishDeploymentAdapter;
import ee.omnifish.concurrent.api.ResourceId;
import ee.omnifish.concurrent.api.StandardContextType;
import ee.omnifish.concurrent.data.ContextServiceDefinitionData;

/**
 * Deployer for ContextServiceDefinitionDescriptor.
 *
 * @author Petr Aubrecht &lt;aubrecht@asoftware.cz&gt;
 */
@Service
// FIXME: rename confuses me with ContextServiceDefinitionDeployer
public class ContextServiceDefinitionDescriptorDeployer {

    private static final Logger LOG = Logger.getLogger(ContextServiceDefinitionDescriptorDeployer.class.getName());

    @Inject
    private GlassFishDeploymentAdapter deployment;
    @Inject
    private ConcurrentRuntimeAdapter runtime;

    // FIXME: simplify StandardContextTypes vs. ContextServiceDefinition.* types
    public void deployResource(ResourceId id, ContextServiceDefinitionData data) throws Exception {
        LOG.log(Level.FINEST, "deployResource(id={0}, data={1})", new Object[] {id, data});
        if (data.getCleared() == null || data.getCleared().isEmpty()) {
            HashSet<String> defaultSetCleared = new HashSet<>();
            defaultSetCleared.add(ContextServiceDefinition.TRANSACTION);
            data.setCleared(defaultSetCleared);
        }
        if (data.getPropagated() == null || data.getPropagated().isEmpty()) {
            HashSet<String> defaultSetPropagated = new HashSet<>();
            defaultSetPropagated.add(ContextServiceDefinition.ALL_REMAINING);
            data.setPropagated(defaultSetPropagated);
        }
        if (data.getUnchanged() == null || data.getUnchanged().isEmpty()) {
            data.setUnchanged(new HashSet<>());
        }
        String contextInfo = renameBuiltinContexts(data.getPropagated()).stream()
            .collect(Collectors.joining(", "));
        ContextServiceConfig contextServiceConfig = runtime.toContextServiceConfig(
            data.getName(), contextInfo, "true",
            renameBuiltinContexts(data.getPropagated()),
            renameBuiltinContexts(data.getCleared()),
            renameBuiltinContexts(data.getUnchanged())
        );
        ContextService contextService = runtime.createContextService(contextServiceConfig);
        deployment.publishObject(id, contextService);
    }


    public void undeployResource(ResourceId id) throws Exception {
        LOG.log(Level.FINEST, "undeployResource(id={0})", id);
        deployment.unpublishObject(id);
        runtime.shutdownContextService(id.jndiName);
    }


    private Set<String> renameBuiltinContexts(Set<String> originalContexts) {
        Set<String> contexts = new HashSet<>();
        Set<String> unusedContexts = new HashSet<>(originalContexts);
        if (unusedContexts.contains(ContextServiceDefinition.TRANSACTION)) {
            unusedContexts.remove(ContextServiceDefinition.TRANSACTION);
            contexts.add(StandardContextType.WorkArea.name());
        }
        if (unusedContexts.contains(ContextServiceDefinition.SECURITY)) {
            unusedContexts.remove(ContextServiceDefinition.SECURITY);
            contexts.add(StandardContextType.Security.name());
        }
        if (unusedContexts.contains(ContextServiceDefinition.APPLICATION)) {
            unusedContexts.remove(ContextServiceDefinition.APPLICATION);
            contexts.add(StandardContextType.Classloader.name());
            contexts.add(StandardContextType.JNDI.name());
        }
        // add all the remaining, custom definitions
        contexts.addAll(unusedContexts);
        return contexts;
    }
}
