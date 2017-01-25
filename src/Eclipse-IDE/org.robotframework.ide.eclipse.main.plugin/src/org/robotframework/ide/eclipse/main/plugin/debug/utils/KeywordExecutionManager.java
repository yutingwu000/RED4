/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.debug.utils;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.robotframework.ide.eclipse.main.plugin.debug.model.RobotDebugElement;
import org.robotframework.ide.eclipse.main.plugin.debug.model.RobotDebugTarget;
import org.robotframework.ide.eclipse.main.plugin.debug.model.RobotLineBreakpoint;

import com.google.common.base.Joiner;

public class KeywordExecutionManager {

    private final List<IResource> suiteFilesToDebug;

    private IFile currentInitFile;

    private IFile currentSuiteFile;

    private IPath currentSuiteLocation;

    private String currentSuiteName = "";

    private IContainer currentSuiteParent;

    private IContainer currentResourceParent;

    private String currentResourceFile;

    private String breakpointCondition = "";

    private final Map<IBreakpoint, Integer> breakpointHitCounts;

    public KeywordExecutionManager(final List<IResource> suiteFilesToDebug) {
        this.suiteFilesToDebug = suiteFilesToDebug;
        breakpointHitCounts = new LinkedHashMap<>();
    }

    public IFile extractCurrentSuite(final IPath suiteFilePath) {
        currentSuiteName = suiteFilePath.lastSegment();
        currentSuiteFile = extractSuiteFile(currentSuiteName, suiteFilePath.removeLastSegments(1),
                suiteFilesToDebug);
        if (currentSuiteFile != null) {
            currentSuiteParent = currentSuiteFile.getParent();
        }
        currentSuiteLocation = suiteFilePath;
        return currentSuiteFile;
    }

    public String extractCurrentResourceFile(final String resourceFilePath) {
        currentResourceFile = resourceFilePath;
        if (currentResourceFile != null) {
            extractResourceParent();
        }
        return currentResourceFile;
    }

    public String extractExecutedFileNameWithParentFolderInfo(final String executedSuiteName) {
        String fileName = executedSuiteName;
        if (currentResourceFile != null && currentResourceParent != null && currentResourceParent instanceof IFolder) {
            fileName = currentResourceParent.getName() + File.separator + fileName;
        } else if (currentSuiteParent != null && currentSuiteParent instanceof IFolder) {
            fileName = currentSuiteParent.getName() + File.separator + fileName;
        }
        return fileName;
    }

    public boolean hasBreakpointAtCurrentKeywordPosition(final String executedSuite, final int keywordLineNumber,
            final RobotDebugTarget target) {
        if(keywordLineNumber < 0) {
            return false;
        }
        final IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
        if (!breakpointManager.isEnabled()) {
            return false;
        }

        boolean hasBreakpoint = false;
        final IBreakpoint[] currentBreakpoints = breakpointManager.getBreakpoints(RobotDebugElement.DEBUG_MODEL_ID);
        for (final IBreakpoint currentBreakpoint : currentBreakpoints) {
            try {
                if (currentBreakpoint.isEnabled()
                        && isBreakpointSourceFileInCurrentExecutionContext(currentBreakpoint.getMarker().getResource(),
                                executedSuite)) {
                    final int breakpointLineNum = (Integer) currentBreakpoint.getMarker().getAttribute(
                            IMarker.LINE_NUMBER);
                    if (keywordLineNumber == breakpointLineNum) {
                        final boolean hasHitCountConditionFulfilled = checkHitCountCondition(currentBreakpoint);
                        if (hasHitCountConditionFulfilled) {
                            breakpointCondition = currentBreakpoint.getMarker().getAttribute(
                                    RobotLineBreakpoint.CONDITIONAL_ATTRIBUTE, "");
                            hasBreakpoint = true;
                            target.breakpointHit(currentBreakpoint);
                        }
                    }
                }
            } catch (final CoreException e) {
                e.printStackTrace();
            }
        }
        return hasBreakpoint;
    }

    public String createJsonFromBreakpointCondition() {
        final List<String> conditionElements = newArrayList(breakpointCondition.split("(\\s{2,}|\t)"));// two or more spaces or tab
        if (conditionElements.isEmpty()) {
            return "{\"keywordCondition\":[]}";
        }

        final String keywordName = conditionElements.remove(0);
        return "{\"keywordCondition\":[\"" + keywordName + "\", [\"" + Joiner.on("\", \"").join(conditionElements)
                + "\"]]}";
    }

    private boolean isBreakpointSourceFileInCurrentExecutionContext(final IResource breakpointSourceFile,
            final String executedSuite) {

        String currentFileParentName = "";
        if (currentResourceFile != null && currentResourceParent != null) {
            currentFileParentName = currentResourceParent.getName();
        } else {
            currentFileParentName = currentSuiteParent.getName();
        }

        return breakpointSourceFile.getName().equals(executedSuite)
                && breakpointSourceFile.getParent().getName().equalsIgnoreCase(currentFileParentName);
    }


    private boolean checkHitCountCondition(final IBreakpoint currentBreakpoint) {
        boolean hasHitCountConditionFulfilled = false;
        final int breakpointHitCount = currentBreakpoint.getMarker().getAttribute(
                RobotLineBreakpoint.HIT_COUNT_ATTRIBUTE, 1);
        if (breakpointHitCount > 1) {
            if (breakpointHitCounts.containsKey(currentBreakpoint)) {
                final int currentHitCount = breakpointHitCounts.get(currentBreakpoint) + 1;
                if (currentHitCount == breakpointHitCount) {
                    hasHitCountConditionFulfilled = true;
                }
                breakpointHitCounts.put(currentBreakpoint, currentHitCount);
            } else {
                breakpointHitCounts.put(currentBreakpoint, 1);
            }
        } else {
            hasHitCountConditionFulfilled = true;
        }
        return hasHitCountConditionFulfilled;
    }

    private IFile extractSuiteFile(final String suiteName, final IPath suiteParentPath, final List<IResource> resources) {
        for (final IResource resource : resources) {
            if (resource.getName().equalsIgnoreCase(suiteName) && resource instanceof IFile
                    && isParentsEqual(suiteParentPath, resource)) {
                return (IFile) resource;
            } else if (resource instanceof IContainer) {
                try {
                    final IFile file = extractSuiteFile(suiteName, suiteParentPath,
                            Arrays.asList(((IContainer) resource).members()));
                    if (file != null) {
                        return file;
                    }
                } catch (final CoreException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private boolean isParentsEqual(final IPath suiteParentPath, final IResource resource) {
        final IContainer suiteParent = ResourcesPlugin.getWorkspace()
                .getRoot()
                .getContainerForLocation(suiteParentPath);
        if (suiteParent != null) {
            return resource.getParent().getName().equalsIgnoreCase(suiteParent.getName());
        }

        return true;  // e.g. remote files can have different parents
    }

    private void extractResourceParent() {
        final IContainer resourceContainer = ResourcesPlugin.getWorkspace()
                .getRoot()
                .getContainerForLocation(new Path(currentResourceFile));
        if (resourceContainer != null) {
            currentResourceParent = resourceContainer.getParent();
        } else {
            currentResourceParent = null;
        }
    }

    public IFile getCurrentInitFile() {
        return currentInitFile;
    }

    public void setCurrentInitFile(final IFile currentInitFile) {
        this.currentInitFile = currentInitFile;
    }

    public IFile getCurrentSuiteFile() {
        return currentSuiteFile;
    }

    public void setCurrentSuiteFile(final IFile currentSuiteFile) {
        this.currentSuiteFile = currentSuiteFile;
    }

    public String getCurrentSuiteName() {
        return currentSuiteName;
    }

    public void setCurrentSuiteName(final String currentSuiteName) {
        this.currentSuiteName = currentSuiteName;
    }

    public IContainer getCurrentResourceParent() {
        return currentResourceParent;
    }

    public void setCurrentResourceParent(final IContainer currentResourceParent) {
        this.currentResourceParent = currentResourceParent;
    }

    public String getCurrentResourceFile() {
        return currentResourceFile;
    }

    public void setCurrentResourceFile(final String currentResourceFile) {
        this.currentResourceFile = currentResourceFile;
    }

    public String getBreakpointCondition() {
        return breakpointCondition;
    }

    public void setBreakpointCondition(final String breakpointCondition) {
        this.breakpointCondition = breakpointCondition;
    }

    public IPath getCurrentSuiteLocation() {
        return currentSuiteLocation;
    }

    public void setCurrentSuiteParent(final IContainer currentSuiteParent) {
        this.currentSuiteParent = currentSuiteParent;
    }

}