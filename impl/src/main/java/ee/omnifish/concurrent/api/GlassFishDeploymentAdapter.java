/*
 * Copyright (c) 2022 Eclipse Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package ee.omnifish.concurrent.api;

import java.util.Set;

import javax.naming.NamingException;

import org.glassfish.enterprise.concurrent.spi.ContextSetupProvider;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author David Matejcek
 */
@Contract
public interface GlassFishDeploymentAdapter {

    String expandValue(String value);

    String getModuleName();

    String getApplicationName();

    void publishObject(ResourceId id, Object object) throws NamingException;

    void unpublishObject(ResourceId id) throws NamingException;

    ContextSetupProvider createContextSetupProvider(Set<String> propagated, Set<String> cleared, Set<String> unchanged);

    /**
     * @return thread context {@link ClassLoader} or system classloader if thread cl is not set.
     */
    ClassLoader getClassLoader();
}
