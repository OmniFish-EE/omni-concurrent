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

import jakarta.enterprise.concurrent.ManagedExecutorDefinition;
import jakarta.inject.Inject;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.jvnet.hk2.annotations.Service;

import ee.omnifish.concurrent.api.GlassFishDeploymentAdapter;
import ee.omnifish.concurrent.data.ManagedExecutorDefinitionData;

@Service
public class ManagedExecutorDefinitionConverter {

    private static final Logger LOG = System.getLogger(ManagedExecutorDefinitionConverter.class.getName());

    @Inject
    private GlassFishDeploymentAdapter glassfish;

    public Set<ManagedExecutorDefinitionData> convert(ManagedExecutorDefinition[] definitions) {
        LOG.log(Level.TRACE, "convert(definitions={0})", (Object) definitions);
        if (definitions == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(definitions).map(this::convert).collect(Collectors.toSet());
    }


    public ManagedExecutorDefinitionData convert(ManagedExecutorDefinition annotation) {
        LOG.log(Level.DEBUG, "convert(annotation={0})", annotation);
        ManagedExecutorDefinitionData data = new ManagedExecutorDefinitionData();
        data.setName(glassfish.expandValue(annotation.name()));
        data.setContext(glassfish.expandValue(annotation.context()));

        if (annotation.hungTaskThreshold() < 0) {
            data.setHungAfterSeconds(0);
        } else {
            data.setHungAfterSeconds(annotation.hungTaskThreshold());
        }

        if (annotation.maxAsync() < 0) {
            data.setMaximumPoolSize(Integer.MAX_VALUE);
        } else {
            data.setMaximumPoolSize(annotation.maxAsync());
        }
        return data;
    }


    public void merge(ManagedExecutorDefinitionData annotationData, ManagedExecutorDefinitionData descriptorData) {
        LOG.log(Level.DEBUG, "merge(annotationData={0}, descriptorData={1})", annotationData, descriptorData);
        if (!annotationData.getName().equals(descriptorData.getName())) {
            throw new IllegalArgumentException("Cannot merge managed executors with different names: "
                + annotationData.getName() + " x " + descriptorData.getName());
        }
        if (descriptorData.getHungAfterSeconds() <= 0 && annotationData.getHungAfterSeconds() != 0) {
            descriptorData.setHungAfterSeconds(annotationData.getHungAfterSeconds());
        }

        if (descriptorData.getMaximumPoolSize() <= 0 && annotationData.getMaximumPoolSize() > 0
            && annotationData.getMaximumPoolSize() < Integer.MAX_VALUE) {
            descriptorData.setMaximumPoolSize(annotationData.getMaximumPoolSize());
        }

        if (descriptorData.getContext() == null && annotationData.getContext() != null
            && !annotationData.getContext().isBlank()) {
            descriptorData.setContext(glassfish.expandValue(annotationData.getContext()));
        }
    }
}
