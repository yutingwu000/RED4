/*
 * Copyright 2018 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.mapping.tasks;

import java.util.List;
import java.util.Stack;

import org.rf.ide.core.testdata.mapping.table.IParsingMapper;
import org.rf.ide.core.testdata.mapping.table.ParsingStateHelper;
import org.rf.ide.core.testdata.model.FilePosition;
import org.rf.ide.core.testdata.model.ModelType;
import org.rf.ide.core.testdata.model.RobotFileOutput;
import org.rf.ide.core.testdata.model.RobotVersion;
import org.rf.ide.core.testdata.model.table.LocalSetting;
import org.rf.ide.core.testdata.model.table.tasks.Task;
import org.rf.ide.core.testdata.text.read.IRobotLineElement;
import org.rf.ide.core.testdata.text.read.IRobotTokenType;
import org.rf.ide.core.testdata.text.read.ParsingState;
import org.rf.ide.core.testdata.text.read.ParsingState.TableType;
import org.rf.ide.core.testdata.text.read.RobotLine;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;
import org.rf.ide.core.testdata.text.read.separators.Separator.SeparatorType;

public class TaskSettingDeclarationMapper implements IParsingMapper {

    private final RobotTokenType declarationType;

    private final ParsingState newParsingState;

    private final ModelType settingModelType;

    private final ParsingStateHelper parsingStateHelper;

    private final TaskFinder finder;

    public TaskSettingDeclarationMapper(final RobotTokenType declarationType, final ParsingState newParsingState,
            final ModelType settingModelType) {
        this.declarationType = declarationType;
        this.newParsingState = newParsingState;
        this.settingModelType = settingModelType;
        this.parsingStateHelper = new ParsingStateHelper();
        this.finder = new TaskFinder();
    }

    @Override
    public final boolean isApplicableFor(final RobotVersion robotVersion) {
        return robotVersion.isNewerOrEqualTo(new RobotVersion(3, 1));
    }

    @Override
    public final boolean checkIfCanBeMapped(final RobotFileOutput robotFileOutput, final RobotLine currentLine,
            final RobotToken rt, final String text, final Stack<ParsingState> processingState) {

        if (rt.getTypes().get(0) == declarationType
                && parsingStateHelper.getCurrentState(processingState).getTable() == TableType.TASKS) {

            final List<IRobotLineElement> lineElements = currentLine.getLineElements();
            if (lineElements.size() == 1) {
                final List<IRobotTokenType> types = lineElements.get(0).getTypes();
                return types.contains(SeparatorType.PIPE) || types.contains(SeparatorType.TABULATOR_OR_DOUBLE_SPACE);
            } else {
                for (final IRobotLineElement elem : lineElements) {
                    final List<IRobotTokenType> types = elem.getTypes();
                    if (types.contains(SeparatorType.PIPE) || types.contains(SeparatorType.TABULATOR_OR_DOUBLE_SPACE)) {
                        continue;
                    }
                    return types.contains(RobotTokenType.TASK_NAME);
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public final RobotToken map(final RobotLine currentLine, final Stack<ParsingState> processingState,
            final RobotFileOutput robotFileOutput, final RobotToken rt, final FilePosition fp, final String text) {

        final List<IRobotTokenType> types = rt.getTypes();
        types.remove(RobotTokenType.UNKNOWN);
        types.add(0, declarationType);
        rt.setText(text);

        final Task task = finder.findOrCreateNearestTask(currentLine, robotFileOutput);
        task.addElement(new LocalSetting<>(settingModelType, rt));

        processingState.push(newParsingState);
        return rt;
    }
}