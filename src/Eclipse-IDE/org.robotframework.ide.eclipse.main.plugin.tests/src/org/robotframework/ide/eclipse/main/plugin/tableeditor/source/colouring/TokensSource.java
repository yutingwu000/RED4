/*
* Copyright 2016 Nokia Solutions and Networks
* Licensed under the Apache License, Version 2.0,
* see license.txt file for details.
*/
package org.robotframework.ide.eclipse.main.plugin.tableeditor.source.colouring;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.rf.ide.core.environment.RobotVersion;
import org.rf.ide.core.testdata.model.FilePosition;
import org.rf.ide.core.testdata.text.read.IRobotLineElement;
import org.rf.ide.core.testdata.text.read.IRobotTokenType;
import org.rf.ide.core.testdata.text.read.LineReader.Constant;
import org.rf.ide.core.testdata.text.read.RobotLine;
import org.rf.ide.core.testdata.text.read.VersionAvailabilityInfo;
import org.rf.ide.core.testdata.text.read.VersionAvailabilityInfo.VersionAvailabilityInfoBuilder;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.robotframework.ide.eclipse.main.plugin.mockmodel.RobotSuiteFileCreator;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSuiteFile;

public class TokensSource {

    static List<RobotToken> createTokens() {
        return createTokensInLines().stream().flatMap(RobotLine::tokensStream).collect(toList());
    }

    static List<RobotLine> createTokensInLines() {
        final RobotSuiteFile model = new RobotSuiteFileCreator().appendLine("*** Test Cases ***")
                .appendLine("case 1")
                .appendLine("  [Documentation]  abc  def  # comment")
                .appendLine("  [Tags]  t1  t2  # comment")
                .appendLine("  [Setup]  tc_setting_call  a1  a2  # comment")
                .appendLine("  [Teardown]  tc_setting_call  a1  a2  #comment")
                .appendLine("  [Timeout]  10  a  b  c  # comment")
                .appendLine("  [UnkownTcSetting]  a  b  c  # comment")
                .appendLine("  call  arg  ${x}  # comment   comment")
                .appendLine("case 2")
                .appendLine("  call  arg  ${x}  # comment   comment")
                .appendLine("  given gherkin_call  # comment")
                .appendLine("  when then gherkin_call  # comment")
                .appendLine("  ${var_asgn}=  given gherkin_call  # comment")
                .appendLine("  call  given arg  # comment")
                .appendLine("*Keywords")
                .appendLine("userkw 1")
                .appendLine("  [arguments]  ${a}  # comment")
                .appendLine("  [documentation]  abc  def  # comment")
                .appendLine("  [tags]  t1  t2  # comment")
                .appendLine("  [teardown]  kw_setting_call  a1  a2  #comment")
                .appendLine("  [timeout]  10  a  b  c  # comment")
                .appendLine("  [unkownKwSetting]  a  b  c  # comment")
                .appendLine("  [return]  a  b  c  # comment")
                .appendLine("  call  arg  ${x}  # comment   comment")
                .appendLine("  ${var_asgn}=  call  arg  ${x}  # comment   comment")
                .appendLine("  ${var_asgn}  call  arg  ${x}  # comment   comment")
                .appendLine("  ${var_asgn_1}  ${var_asgn_2}=  call  arg  ${x}  # comment   comment")
                .appendLine("  ${var_asgn_1}  ${var_asgn_2}  call  arg  ${x}  # comment   comment")
                .appendLine("  :FOR  ${i}  IN RANGE  10")
                .appendLine("  \\  call  arg  ${x}  # comment   comment")
                .appendLine("  \\  ${var_asgn}=  call  AND  ${x}  # comment   comment")
                .appendLine("  \\  ${var_asgn}  call  arg  ${x}  # comment   comment")
                .appendLine("  \\  ${var_asgn_1}  ${var_asgn_2}=  call  ELSE  ${x}  # comment   comment")
                .appendLine("  \\  ${var_asgn_1}  ${var_asgn_2}  call  arg  ${x}  # comment   comment")
                .appendLine("  \\  given gherkin_call  # comment")
                .appendLine("  \\  when then gherkin_call  # comment")
                .appendLine("  \\  ${var_asgn}=  given gherkin_call  # comment")
                .appendLine("  \\  call  given arg  # comment")
                .appendLine("userkw 2")
                .appendLine("  [arguments]  ${b}  # comment")
                .appendLine("  call  arg  ${x}  # comment   comment")
                .appendLine("  given gherkin_call  # comment")
                .appendLine("  when then gherkin_call  # comment")
                .appendLine("  ${var_asgn}=  given gherkin_call  # comment")
                .appendLine("  call  given arg  # comment")
                .appendLine("* * * Variables * * *")
                .appendLine("${var_def_1}  1  # comment")
                .appendLine("${var_def_2}  1  2  3  # comment")
                .appendLine("@{var_def_3}  1  2  3  # comment")
                .appendLine("&{var_def_4}  k1=v1  k2=v2  # comment")
                .appendLine("{var_def_5}  1  2  3  # comment")
                .appendLine("*** unknown section ***")
                .appendLine("some stuff")
                .appendLine("*** Settings ***")
                .appendLine("Documentation  abc  def  # comment")
                .appendLine("Library  abc  def  # comment")
                .appendLine("Library  abc  WITH NAME  def  # comment")
                .appendLine("Resource  abc  def  # comment")
                .appendLine("Variables  abc  def  # comment")
                .appendLine("Metadata  abc  def  # comment")
                .appendLine("Force Tags  abc  def  # comment")
                .appendLine("Default Tags  abc  def  # comment")
                .appendLine("Suite Setup  general_setting_call  def  # comment")
                .appendLine("Suite Teardown  general_setting_call  def  # comment")
                .appendLine("Test Setup  general_setting_call  def  # comment")
                .appendLine("Test Teardown  general_setting_call  def  # comment")
                .appendLine("Test Timeout  abc  def  # comment")
                .appendLine("UnkownSetting  abc  def  # comment")
                .build();
        return model.getLinkedElement().getFileContent();
    }

    static List<RobotToken> createRpaTokens() {
        return createRpaTokensInLines().stream().flatMap(RobotLine::tokensStream).collect(toList());
    }

    static List<RobotLine> createRpaTokensInLines() {
        final RobotSuiteFile model = new RobotSuiteFileCreator(new RobotVersion(3, 1)).appendLine("*** Tasks ***")
                .appendLine("task 1")
                .appendLine("  [Documentation]  abc  def  # comment")
                .appendLine("  [Tags]  t1  t2  # comment")
                .appendLine("  [Setup]  tc_setting_call  a1  a2  # comment")
                .appendLine("  [Teardown]  tc_setting_call  a1  a2  #comment")
                .appendLine("  [Timeout]  10  a  b  c  # comment")
                .appendLine("  [UnkownTcSetting]  a  b  c  # comment")
                .appendLine("  call  arg  ${x}  # comment   comment")
                .appendLine("task 2")
                .appendLine("  call  arg  ${x}  # comment   comment")
                .appendLine("  given gherkin_call  # comment")
                .appendLine("  when then gherkin_call  # comment")
                .appendLine("  ${var_asgn}=  given gherkin_call  # comment")
                .appendLine("  call  given arg  # comment")
                .appendLine("*Keywords")
                .appendLine("userkw 1")
                .appendLine("  [arguments]  ${a}  # comment")
                .appendLine("  [documentation]  abc  def  # comment")
                .appendLine("  [tags]  t1  t2  # comment")
                .appendLine("  [teardown]  kw_setting_call  a1  a2  #comment")
                .appendLine("  [timeout]  10  a  b  c  # comment")
                .appendLine("  [unkownKwSetting]  a  b  c  # comment")
                .appendLine("  [return]  a  b  c  # comment")
                .appendLine("  call  arg  ${x}  # comment   comment")
                .appendLine("  ${var_asgn}=  call  arg  ${x}  # comment   comment")
                .appendLine("  ${var_asgn}  call  arg  ${x}  # comment   comment")
                .appendLine("  ${var_asgn_1}  ${var_asgn_2}=  call  arg  ${x}  # comment   comment")
                .appendLine("  ${var_asgn_1}  ${var_asgn_2}  call  arg  ${x}  # comment   comment")
                .appendLine("  :FOR  ${i}  IN RANGE  10")
                .appendLine("  \\  call  arg  ${x}  # comment   comment")
                .appendLine("  \\  ${var_asgn}=  call  AND  ${x}  # comment   comment")
                .appendLine("  \\  ${var_asgn}  call  arg  ${x}  # comment   comment")
                .appendLine("  \\  ${var_asgn_1}  ${var_asgn_2}=  call  ELSE  ${x}  # comment   comment")
                .appendLine("  \\  ${var_asgn_1}  ${var_asgn_2}  call  arg  ${x}  # comment   comment")
                .appendLine("  \\  given gherkin_call  # comment")
                .appendLine("  \\  when then gherkin_call  # comment")
                .appendLine("  \\  ${var_asgn}=  given gherkin_call  # comment")
                .appendLine("  \\  call  given arg  # comment")
                .appendLine("userkw 2")
                .appendLine("  [arguments]  ${b}  # comment")
                .appendLine("  call  arg  ${x}  # comment   comment")
                .appendLine("  given gherkin_call  # comment")
                .appendLine("  when then gherkin_call  # comment")
                .appendLine("  ${var_asgn}=  given gherkin_call  # comment")
                .appendLine("  call  given arg  # comment")
                .appendLine("* * * Variables * * *")
                .appendLine("${var_def_1}  1  # comment")
                .appendLine("${var_def_2}  1  2  3  # comment")
                .appendLine("@{var_def_3}  1  2  3  # comment")
                .appendLine("&{var_def_4}  k1=v1  k2=v2  # comment")
                .appendLine("{var_def_5}  1  2  3  # comment")
                .appendLine("*** unknown section ***")
                .appendLine("some stuff")
                .appendLine("*** Settings ***")
                .appendLine("Documentation  abc  def  # comment")
                .appendLine("Library  abc  def  # comment")
                .appendLine("Library  abc  WITH NAME  def  # comment")
                .appendLine("Resource  abc  def  # comment")
                .appendLine("Variables  abc  def  # comment")
                .appendLine("Metadata  abc  def  # comment")
                .appendLine("Force Tags  abc  def  # comment")
                .appendLine("Default Tags  abc  def  # comment")
                .appendLine("Suite Setup  general_setting_call  def  # comment")
                .appendLine("Suite Teardown  general_setting_call  def  # comment")
                .appendLine("Task Setup  general_setting_call  def  # comment")
                .appendLine("Task Teardown  general_setting_call  def  # comment")
                .appendLine("Task Timeout  abc  def  # comment")
                .appendLine("UnkownSetting  abc  def  # comment")
                .build();
        return model.getLinkedElement().getFileContent();
    }

    static List<RobotLine> createTokensInLinesWithSpecialNestingKeywords() {
        final RobotSuiteFile model = new RobotSuiteFileCreator().appendLine("*** Test Cases ***")
                .appendLine("case")
                .appendLine("  [Documentation]  abc  def  # comment")
                .appendLine("  [Tags]  t1  t2  # comment")
                .appendLine(
                        "  [Setup]  Run Keyword If  cond  settings_nestedkw  arg  ELSE IF  condition  settings_nestedkw  arg  ELSE  settings_nestedkw  arg  # comment")
                .appendLine(
                        "  [Teardown]  Run Keywords  settings_nestedkw  arg  AND  settings_nestedkw  arg  AND  settings_nestedkw  arg  #comment")
                .appendLine("  [Timeout]  10  a  b  c  # comment")
                .appendLine("  [UnkownTcSetting]  a  b  c  # comment")
                .appendLine(
                        "  Run Keyword If  cond  nestedkw  arg  ELSE IF  condition  nestedkw  arg  ELSE  nestedkw  arg")
                .appendLine("  Run Keywords  nestedkw  arg  AND  nestedkw  arg  AND  nestedkw  arg")
                .appendLine("*Keywords")
                .appendLine("userkw 1")
                .appendLine("  [arguments]  ${a}  # comment")
                .appendLine("  [documentation]  abc  def  # comment")
                .appendLine("  [tags]  t1  t2  # comment")
                .appendLine(
                        "  [teardown]  Run Keyword If  cond  settings_nestedkw  arg  ELSE IF  condition  settings_nestedkw  arg  ELSE  settings_nestedkw  arg  #comment")
                .appendLine("  [timeout]  10  a  b  c  # comment")
                .appendLine("  [unkownKwSetting]  a  b  c  # comment")
                .appendLine("  [return]  a  b  c  # comment")
                .appendLine(
                        "  Run Keyword If  cond  nestedkw  arg  ELSE IF  condition  nestedkw  arg  ELSE  nestedkw  arg")
                .appendLine("  Run Keywords  nestedkw  arg  AND  nestedkw  arg  AND  nestedkw  arg")
                .appendLine(
                        "  ${var_asgn}=  Run Keyword If  cond  nestedkw  arg  ELSE IF  condition  nestedkw  arg  ELSE  nestedkw  arg")
                .appendLine("  ${var_asgn}=  Run Keywords  nestedkw  arg  AND  nestedkw  arg  AND  nestedkw  arg")
                .appendLine(
                        "  ${xvar_asgn1}  ${var_asgn2}=  Run Keyword If  cond  nestedkw  arg  ELSE IF  condition  nestedkw  arg  ELSE  nestedkw  arg")
                .appendLine(
                        "  ${var_asgn1}  ${var_asgn2}=  Run Keywords  nestedkw  arg  AND  nestedkw  arg  AND  nestedkw  arg")
                .appendLine("  :FOR  ${i}  IN RANGE  10")
                .appendLine(
                        "  \\  Run Keyword If  cond  nestedkw  arg  ELSE IF  condition  nestedkw  arg  ELSE  nestedkw  arg")
                .appendLine("  \\  Run Keywords  nestedkw  arg  AND  nestedkw  arg  AND  nestedkw  arg")
                .appendLine("*** Settings ***")
                .appendLine("Documentation  abc  def  # comment")
                .appendLine("Library  abc  def  # comment")
                .appendLine("Library  abc  WITH NAME  def  # comment")
                .appendLine("Resource  abc  def  # comment")
                .appendLine("Variables  abc  def  # comment")
                .appendLine("Metadata  abc  def  # comment")
                .appendLine("Force Tags  abc  def  # comment")
                .appendLine("Default Tags  abc  def  # comment")
                .appendLine(
                        "Suite Setup  Run Keyword If  cond  settings_nestedkw  arg  ELSE IF  condition  settings_nestedkw  arg  ELSE  settings_nestedkw  arg  #comment")
                .appendLine(
                        "Suite Teardown  Run Keyword If  cond  settings_nestedkw  arg  ELSE IF  condition  settings_nestedkw  arg  ELSE  settings_nestedkw  arg  #comment  # comment")
                .appendLine(
                        "Test Setup  Run Keyword If  cond  settings_nestedkw  arg  ELSE IF  condition  settings_nestedkw  arg  ELSE  settings_nestedkw  arg  #comment  # comment")
                .appendLine(
                        "Test Teardown  Run Keyword If  cond  settings_nestedkw  arg  ELSE IF  condition  settings_nestedkw  arg  ELSE  settings_nestedkw  arg  #comment  # comment")
                .appendLine("Test Timeout  abc  def  # comment")
                .appendLine("UnkownSetting  abc  def  # comment")
                .build();
        return model.getLinkedElement().getFileContent();
    }

    static List<RobotLine> createTokensOfNoneAwareSettings() {
        final RobotSuiteFile model = new RobotSuiteFileCreator(new RobotVersion(3, 1)).appendLine("*** Test Cases ***")
                .appendLine("case 1")
                .appendLine("  [Documentation]  NONE")
                .appendLine("  [Tags]  NONE")
                .appendLine("  [Setup]  NONE")
                .appendLine("  [Teardown]  NONE")
                .appendLine("  [Timeout]  NONE")
                .appendLine("  [UnkownTcSetting]  NONE")
                .appendLine("  [Template]  NONE")
                .appendLine("*** Tasks ***")
                .appendLine("task 1")
                .appendLine("  [Documentation]  NONE")
                .appendLine("  [Tags]  NONE")
                .appendLine("  [Setup]  NONE")
                .appendLine("  [Teardown]  NONE")
                .appendLine("  [Timeout]  NONE")
                .appendLine("  [UnkownTaSetting]  NONE")
                .appendLine("  [Template]  NONE")
                .appendLine("*** Keywords ***")
                .appendLine("userkw 1")
                .appendLine("  [Arguments]  NONE")
                .appendLine("  [Documentation]  NONE")
                .appendLine("  [Tags]  NONE")
                .appendLine("  [Teardown]  NONE")
                .appendLine("  [Timeout]  NONE")
                .appendLine("  [UnkownKwSetting]  NONE")
                .appendLine("  [Return]  NONE")
                .appendLine("*** Settings ***")
                .appendLine("Documentation  NONE")
                .appendLine("Library  NONE")
                .appendLine("Resource  NONE")
                .appendLine("Variables  NONE")
                .appendLine("Metadata  NONE")
                .appendLine("Force Tags  NONE")
                .appendLine("Default Tags  NONE")
                .appendLine("Suite Setup  NONE")
                .appendLine("Suite Teardown  NONE")
                .appendLine("Test Setup  NONE")
                .appendLine("Test Teardown  NONE")
                .appendLine("Test Template  NONE")
                .appendLine("Test Timeout  NONE")
                .appendLine("Task Setup  NONE")
                .appendLine("Task Teardown  NONE")
                .appendLine("Task Template  NONE")
                .appendLine("Task Timeout  NONE")
                .appendLine("UnkownSetting  NONE")
                .build();
        return model.getLinkedElement().getFileContent();
    }

    static List<RobotLine> createTokensOfTemplatedCases() {
        final RobotSuiteFile model = new RobotSuiteFileCreator(new RobotVersion(3, 1)).appendLine("*** Test Cases ***")
                .appendLine("case 1")
                .appendLine("  [Template]  tc_setting_template  tc_setting_template_arg1  tc_setting_template_arg2  # comment")
                .appendLine("  arg1  arg2  # comment   comment")
                .appendLine("  other1  other2")
                .appendLine("  a1  a2  a3  a4  # comment")
                .appendLine("case 2")
                .appendLine("  arg1  arg2  # comment   comment")
                .appendLine("  other1  other2")
                .appendLine("  a1  a2  a3  a4  # comment")
                .appendLine("case 3")
                .appendLine("  FOR  ${item}  IN  @{ITEMS}")
                .appendLine("    a  b  c")
                .appendLine("    other")
                .appendLine("  END")
                .appendLine("*** Settings ***")
                .appendLine("Test Template  general_setting_template  general_setting_template_arg  # comment")
                .build();
        return model.getLinkedElement().getFileContent();
    }

    static List<RobotLine> lines(final RobotLine... lines) {
        return newArrayList(lines);
    }

    static RobotLine line(final int no, final LineElement... elements) {
        final RobotLine line = new RobotLine(no, null);
        final int firstOffset = elements[0].getStartOffset();
        int currentColumn = 0;
        for (final LineElement element : elements) {
            line.addLineElement(element);

            currentColumn += element.getText().length();
        }
        line.setEndOfLine(newArrayList(Constant.LF), firstOffset + currentColumn, currentColumn);
        return line;
    }

    static class LineElement implements IRobotLineElement {

        private final FilePosition filePosition;

        private final String text;

        public LineElement(final int line, final int column, final int offset, final String content) {
            this.filePosition = new FilePosition(line, column, offset);
            this.text = content;
        }

        @Override
        public int getLineNumber() {
            return filePosition.getLine();
        }

        @Override
        public int getStartColumn() {
            return filePosition.getColumn();
        }

        @Override
        public int getEndColumn() {
            return filePosition.getColumn() + text.length();
        }

        @Override
        public int getStartOffset() {
            return filePosition.getOffset();
        }

        @Override
        public int getEndOffset() {
            return getStartOffset() + getEndColumn() - getStartColumn();
        }

        @Override
        public FilePosition getFilePosition() {
            return filePosition;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public List<IRobotTokenType> getTypes() {
            return new ArrayList<>();
        }

        @Override
        public boolean isDirty() {
            return false;
        }

        @Override
        public VersionAvailabilityInfo getVersionInformation() {
            return VersionAvailabilityInfoBuilder.create().build();
        }

        @Override
        public LineElement copyWithoutPosition() {
            return copy(FilePosition.createNotSet());
        }

        @Override
        public LineElement copy() {
            return copy(getFilePosition());
        }

        private LineElement copy(final FilePosition fp) {
            return new LineElement(fp.getLine(), fp.getColumn(), fp.getOffset(), getText());
        }
    }
}
