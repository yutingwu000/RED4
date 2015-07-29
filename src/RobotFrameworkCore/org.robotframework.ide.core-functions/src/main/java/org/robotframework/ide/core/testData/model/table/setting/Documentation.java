package org.robotframework.ide.core.testData.model.table.setting;

import java.util.LinkedList;
import java.util.List;

import org.robotframework.ide.core.testData.model.AModelElement;
import org.robotframework.ide.core.testData.text.read.recognizer.RobotToken;


public class Documentation extends AModelElement {

    private final RobotToken declaration;
    private final List<RobotToken> text = new LinkedList<>();
    private final List<RobotToken> comment = new LinkedList<>();


    public Documentation(final RobotToken declaration) {
        this.declaration = declaration;
    }


    public void addDocumentationText(RobotToken token) {
        text.add(token);
    }


    public List<RobotToken> getDocumentationText() {
        return text;
    }


    public List<RobotToken> getComment() {
        return comment;
    }


    public void addCommentPart(final RobotToken rt) {
        this.comment.add(rt);
    }


    public RobotToken getDeclaration() {
        return declaration;
    }


    @Override
    public boolean isPresent() {
        return true; // TODO: check if correct declaration and etc
    }
}
