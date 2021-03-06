/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.text.read.recognizer;

import java.util.regex.Pattern;


public class HashCommentRecognizer extends ATokenRecognizer {

    /**
     * must not start from '\' and contains at the beginning '#' (space optional)
     */
    public static final Pattern EXPECTED = Pattern.compile("^[\\s]*(?!\\\\)#.*$");


    public HashCommentRecognizer() {
        super(EXPECTED, RobotTokenType.COMMENT);
    }


    @Override
    public ATokenRecognizer newInstance() {
        return new HashCommentRecognizer();
    }

    @Override
    public boolean shouldContinueWithOtherRecognizers() {
        return false;
    }
}
