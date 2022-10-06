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

package ee.omnifish.concurrent.data;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import ee.omnifish.concurrent.api.StandardContextType;

import static ee.omnifish.concurrent.api.StandardContextType.Classloader;
import static ee.omnifish.concurrent.api.StandardContextType.JNDI;
import static ee.omnifish.concurrent.api.StandardContextType.Security;
import static ee.omnifish.concurrent.api.StandardContextType.WorkArea;

/**
 * @author David Matejcek
 */
public final class ContextInfoParser {

    private ContextInfoParser() {
        // utility class
    }


    public static Set<String> parseContextInfo(String contextInfo, boolean isContextInfoEnabled) {
        Set<String> contextTypeArray = new HashSet<>();
        if (contextInfo == null) {
            // by default, if no context info is passed, we propagate all context types
            contextTypeArray.add(Classloader.name());
            contextTypeArray.add(JNDI.name());
            contextTypeArray.add(Security.name());
            contextTypeArray.add(WorkArea.name());
        } else if (isContextInfoEnabled) {
            StringTokenizer st = new StringTokenizer(contextInfo, ",", false);
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                StandardContextType standardContextType = StandardContextType.parse(token);
                contextTypeArray.add(standardContextType == null ? token : standardContextType.name());
            }
        }
        return contextTypeArray;
    }
}
