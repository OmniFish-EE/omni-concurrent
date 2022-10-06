// Copyright 2022 OmniFish

package ee.omnifish.concurrent.runtime;

import jakarta.enterprise.concurrent.spi.ThreadContextProvider;
import jakarta.enterprise.concurrent.spi.ThreadContextSnapshot;

import java.io.Serializable;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;

import ee.omnifish.concurrent.api.StandardContextType;

import static java.util.ServiceLoader.load;

public class ContextSetup implements Serializable {
    private static final long serialVersionUID = 7817957604183520917L;
    private static final Logger LOG = System.getLogger(ContextSetup.class.getName());

    private final Set<String> contextPropagate;
    private final Set<String> contextClear;
    private final Set<String> contextUnchanged;
    private transient Map<String, ThreadContextProvider> allThreadContextProviders;


    public ContextSetup(Set<String> propagated, Set<String> cleared, Set<String> unchanged) {
        this.contextPropagate = standardize(propagated);
        this.contextClear = standardize(cleared);
        this.contextUnchanged = standardize(unchanged);
    }


    public void reloadProviders(final ClassLoader loader) {
        this.allThreadContextProviders = loadAllProviders(loader);
        addRemaining(contextPropagate, contextClear, contextUnchanged, allThreadContextProviders);
        LOG.log(Level.DEBUG, "Available contexts: {0}", this);
    }


    public boolean isPropagated(StandardContextType contextType) {
        return contextPropagate.contains(contextType.name());
    }


    public boolean isClear(StandardContextType contextType) {
        return contextClear.contains(contextType.name());
    }


    public boolean isUnchanged(StandardContextType contextType) {
        return contextUnchanged.contains(contextType.name());
    }


    // FIXME: used in tests
    public Set<String> getVerifiedPropagatedContextTypes() {
        return filterVerifiedProviders(contextPropagate, allThreadContextProviders);
    }


    private Set<String> getVerifiedClearContextTypes() {
        return filterVerifiedProviders(contextClear, allThreadContextProviders);
    }


    private Set<String> getVerifiedUnchangedContextTypes() {
        return filterVerifiedProviders(contextUnchanged, allThreadContextProviders);
    }


    public List<ThreadContextSnapshot> getThreadContextSnapshots(Map<String, String> executionProperties) {
        LOG.log(Level.TRACE, "getThreadContextSnapshots(executionProperties={0})", executionProperties);
        // store the snapshots of the current state
        final List<ThreadContextSnapshot> threadContextSnapshots = new ArrayList<>();
        // remember values from propagate and clear lists
        getVerifiedPropagatedContextTypes().stream().map(allThreadContextProviders::get)
            // ignore StandardContextType enums
            .filter(Objects::nonNull).map(snapshot -> snapshot.currentContext(executionProperties))
            .forEach(threadContextSnapshots::add);
        getVerifiedClearContextTypes().stream().map(allThreadContextProviders::get)
            .filter(Objects::nonNull).map(snapshot -> snapshot.clearedContext(executionProperties))
            .forEach(threadContextSnapshots::add);
        return threadContextSnapshots;
    }


    @Override
    public String toString() {
        return super.toString() + "[propagated=" + contextPropagate + ", cleared=" + contextClear + ", unchanged="
            + contextUnchanged + "]";
    }


    // FIXME: move to the server, it should be managed on parsing descriptors and annotations.
    private static Set<String> standardize(final Set<String> contextTypes) {
        HashSet<String> standardizedTypes = new HashSet<>();
        for (String input : contextTypes) {
            StandardContextType contextType = StandardContextType.parse(input);
            standardizedTypes.add(contextType == null ? input : contextType.name());
        }
        return standardizedTypes;
    }


    private static Map<String, ThreadContextProvider> loadAllProviders(ClassLoader loader) {
        LOG.log(Level.TRACE, "Using classloader: {0}", loader);
        ServiceLoader<ThreadContextProvider> services = load(ThreadContextProvider.class, loader);
        Map<String, ThreadContextProvider> providers = new HashMap<>();
        for (ThreadContextProvider service : services) {
            String serviceName = service.getThreadContextType();
            providers.put(serviceName, service);
        }
        LOG.log(Level.DEBUG, "Detected ThreadContextProvider implementations: {0}", providers);
        return providers;
    }


    private static void addRemaining(Set<String> propagated, Set<String> clear, Set<String> unchanged,
        Map<String, ThreadContextProvider> allThreadContextProviders) {
        Set<String> remaining = chooseSet(propagated, clear, unchanged);
        for (StandardContextType contextType : StandardContextType.values()) {
            if (contextType == StandardContextType.Remaining) {
                continue;
            }
            final String name = contextType.name();
            addIfNotInAnotherSet(name, remaining, propagated, clear, unchanged);
        }
        for (String name : allThreadContextProviders.keySet()) {
            addIfNotInAnotherSet(name, remaining, propagated, clear, unchanged);
        }
    }


    private static Set<String> chooseSet(Set<String> propagated, Set<String> clear, Set<String> unchanged) {
        if (clear.contains(StandardContextType.Remaining.name())) {
            return clear;
        } else if (unchanged.contains(StandardContextType.Remaining.name())) {
            return unchanged;
        } else {
            return propagated;
        }
    }


    private static void addIfNotInAnotherSet(String name, Set<String> remaining, Set<String> propagated,
        Set<String> clear, Set<String> unchanged) {
        if (propagated.contains(name) || clear.contains(name) || unchanged.contains(name)) {
            return;
        }
        remaining.add(name);
    }


    private static Set<String> filterVerifiedProviders(Set<String> providers,
        Map<String, ThreadContextProvider> threadCtxProviders) {
        if (threadCtxProviders == null) {
            throw new IllegalStateException("Call reload method first! ThreadContextProviders were not loaded yet.");
        }
        Set<String> filtered = new HashSet<>();
        for (String provider : providers) {
            if (StandardContextType.parse(provider) != null || threadCtxProviders.containsKey(provider)) {
                filtered.add(provider);
            } else {
                LOG.log(Level.WARNING,
                    "Thread context provider {0} is not registered in "
                        + "[META-INF|WEB-INF]/services/jakarta.enterprise.concurrent.spi.ThreadContextProvider"
                        + " and will be ignored!",
                    provider);
            }
        }
        return filtered;
    }
}
