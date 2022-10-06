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

package ee.omnifish.concurrent.runtime;

import jakarta.enterprise.concurrent.spi.ThreadContextRestorer;
import jakarta.enterprise.concurrent.spi.ThreadContextSnapshot;

import java.io.Serializable;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class InvocationContextThreadManagement implements Serializable {
    private static final long serialVersionUID = -5362240277800112912L;
    private static final Logger LOG = System.getLogger(InvocationContextThreadManagement.class.getName());

    private final List<ThreadContextSnapshot> threadContextSnapshots;
    private final List<ThreadContextRestorer> threadContextRestorers;
    private final boolean useTransactionOfExecutionThread;

    public static InvocationContextThreadManagement createNextGeneration(InvocationContextThreadManagement oldGen) {
        List<ThreadContextRestorer> newRestorers = oldGen.threadContextSnapshots.stream()
            .map(snapshot -> snapshot.begin()).collect(toList());
        return new InvocationContextThreadManagement(oldGen.useTransactionOfExecutionThread, emptyList(), newRestorers);
    }


    public InvocationContextThreadManagement(boolean useTxOfExecutionThread,
        List<ThreadContextSnapshot> snapshots, List<ThreadContextRestorer> restorers) {
        LOG.log(Level.TRACE,
            "InvocationContextThreadManagement(useTxOfExecutionThread={0}, snapshots={1}, restorers={2})",
            useTxOfExecutionThread, snapshots, restorers);
        this.useTransactionOfExecutionThread = useTxOfExecutionThread;
        this.threadContextSnapshots = snapshots;
        this.threadContextRestorers = restorers;
    }


    public List<ThreadContextSnapshot> getThreadContextSnapshots() {
        return threadContextSnapshots;
    }


    public List<ThreadContextRestorer> getThreadContextRestorers() {
        return threadContextRestorers;
    }


    public boolean isUseTransactionOfExecutionThread() {
        return useTransactionOfExecutionThread;
    }
}
