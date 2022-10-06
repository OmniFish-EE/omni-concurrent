/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
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
// Portions Copyright [2016-2022] [Payara Foundation and/or its affiliates]
// Portions Copyright 2022 OmniFish

package ee.omnifish.concurrent.runtime;

import jakarta.inject.Inject;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Set;

import org.glassfish.enterprise.concurrent.ContextServiceImpl;
import org.glassfish.enterprise.concurrent.spi.ContextSetupProvider;
import org.glassfish.enterprise.concurrent.spi.TransactionSetupProvider;
import org.jvnet.hk2.annotations.Service;

import ee.omnifish.concurrent.api.ContextServiceConfig;
import ee.omnifish.concurrent.api.GlassFishDeploymentAdapter;

// FIXME: integrate directly to ConcurrentRuntime, everything rewritten or used from GF
/**
 * This class provides API to create various Concurrency Utilities objects
 */
@Service
public class OmniConcurrentRuntime {
    private static final Logger LOG = System.getLogger(OmniConcurrentRuntime.class.getName());

    @Inject
    private GlassFishDeploymentAdapter glassfish;

    public ContextServiceImpl createContextService(ContextServiceConfig config, TransactionSetupProvider provider) {
        LOG.log(Level.DEBUG, "createContextService(config={0}, provider={1})", config, provider);
        return createContextService(config.getJndiName(), config.getPropagatedContexts(), config.getClearedContexts(),
            config.getUchangedContexts(), provider);
    }


    public ContextServiceImpl createContextService(String jndiName, Set<String> propagated, Set<String> cleared,
        Set<String> unchanged, TransactionSetupProvider txSetupProvider) {
        LOG.log(Level.DEBUG,
            "createContextService(jndiName={0}, propagated={1}, cleared={2}, unchanged={3}, txSetupProvider={4})",
            jndiName, propagated, cleared, unchanged, txSetupProvider);
        ContextSetupProvider ctxSetupProvider = glassfish.createContextSetupProvider(propagated, cleared, unchanged);
        return new ContextServiceImpl(jndiName, ctxSetupProvider, txSetupProvider);
    }


    /**
     * Decide JNDI name of context service. Either it is specified or it is created from the owning
     * object.
     *
     * @param configuredContextJndiName JNDI name specified (typically context parameter in annotation)
     * @param parentObjectJndiName JNDI nam of ManagedExecutorService, ManagedThreadFactory etc. using the context service
     *
     * @return JNDI name for the context service to be stored in the JNDI tree.
     */
    public static String createContextServiceName(String configuredContextJndiName, String parentObjectJndiName) {
        if (configuredContextJndiName == null) {
            return parentObjectJndiName + "-contextservice";
        }
        return configuredContextJndiName;
    }
}
