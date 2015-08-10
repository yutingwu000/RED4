package org.robotframework.ide.core.testData.model.table.userKeywords.mapping;

import java.util.List;
import java.util.Stack;

import org.robotframework.ide.core.testData.model.FilePosition;
import org.robotframework.ide.core.testData.model.RobotFileOutput;
import org.robotframework.ide.core.testData.model.table.userKeywords.KeywordDocumentation;
import org.robotframework.ide.core.testData.model.table.userKeywords.UserKeyword;
import org.robotframework.ide.core.testData.text.read.IRobotTokenType;
import org.robotframework.ide.core.testData.text.read.ParsingState;
import org.robotframework.ide.core.testData.text.read.RobotLine;
import org.robotframework.ide.core.testData.text.read.recognizer.RobotToken;
import org.robotframework.ide.core.testData.text.read.recognizer.RobotTokenType;


public class KeywordDocumentationMapper extends
        AKeywordSettingDeclarationMapper {

    public KeywordDocumentationMapper() {
        super(RobotTokenType.KEYWORD_SETTING_DOCUMENTATION);
    }


    @Override
    public RobotToken map(RobotLine currentLine,
            Stack<ParsingState> processingState,
            RobotFileOutput robotFileOutput, RobotToken rt, FilePosition fp,
            String text) {
        List<IRobotTokenType> types = rt.getTypes();
        types.remove(RobotTokenType.UNKNOWN);
        types.add(0, RobotTokenType.KEYWORD_SETTING_DOCUMENTATION);
        rt.setRaw(new StringBuilder(text));
        rt.setText(new StringBuilder(text));

        UserKeyword keyword = findOrCreateNearestKeyword(currentLine,
                processingState, robotFileOutput, rt, fp);
        KeywordDocumentation doc = new KeywordDocumentation(rt);
        keyword.addDocumentation(doc);
        processingState
                .push(ParsingState.KEYWORD_SETTING_DOCUMENTATION_DECLARATION);

        return rt;
    }
}
