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
package ee.omnifish.concurrent.deploy;
import jakarta.enterprise.concurrent.ManagedThreadFactoryDefinition;
import jakarta.inject.Inject;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.jvnet.hk2.annotations.Service;

import ee.omnifish.concurrent.api.GlassFishDeploymentAdapter;
import ee.omnifish.concurrent.data.ManagedThreadFactoryDefinitionData;

@Service
public class ManagedThreadFactoryDefinitionConverter {
    private static final Logger LOG = System.getLogger(ManagedThreadFactoryDefinitionConverter.class.getName());

    @Inject
    private GlassFishDeploymentAdapter glassfish;

    public Set<ManagedThreadFactoryDefinitionData> convert(final ManagedThreadFactoryDefinition[] definitions) {
        LOG.log(Level.TRACE, "convert(definitions={0})", (Object) definitions);
        if (definitions == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(definitions).map(this::convert).collect(Collectors.toSet());
    }


    public ManagedThreadFactoryDefinitionData convert(ManagedThreadFactoryDefinition definition) {
        LOG.log(Level.DEBUG, "convert(definition={0})", definition);
        ManagedThreadFactoryDefinitionData mtfdd = new ManagedThreadFactoryDefinitionData();
        mtfdd.setName(glassfish.expandValue(definition.name()));
        mtfdd.setContext(glassfish.expandValue(definition.context()));
        if (definition.priority() <= 0) {
            mtfdd.setPriority(Thread.NORM_PRIORITY);
        } else {
            mtfdd.setPriority(definition.priority());
        }
        return mtfdd;
    }


    public void merge(ManagedThreadFactoryDefinitionData annotationData,
        ManagedThreadFactoryDefinitionData descriptorData) {
        LOG.log(Level.DEBUG, "merge(annotationData={0}, descriptorData={1})", annotationData, descriptorData);
        if (!annotationData.getName().equals(descriptorData.getName())) {
            throw new IllegalArgumentException("Cannot merge managed thread factories with different names: "
                + annotationData.getName() + " x " + descriptorData.getName());
        }

        if (descriptorData.getPriority() == -1 && annotationData.getPriority() != -1) {
            descriptorData.setPriority(annotationData.getPriority());
        }

        if (descriptorData.getContext() == null && annotationData.getContext() != null
            && !annotationData.getContext().isBlank()) {
            descriptorData.setContext(glassfish.expandValue(annotationData.getContext()));
        }
    }
}
