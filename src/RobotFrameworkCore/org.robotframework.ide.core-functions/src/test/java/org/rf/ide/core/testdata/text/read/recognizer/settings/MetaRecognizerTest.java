/*
* Copyright 2018 Nokia Solutions and Networks
* Licensed under the Apache License, Version 2.0,
* see license.txt file for details.
*/
package org.rf.ide.core.testdata.text.read.recognizer.settings;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.rf.ide.core.test.helpers.CombinationGenerator;
import org.rf.ide.core.testdata.text.read.recognizer.ATokenRecognizer;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;


public class MetaRecognizerTest {

    private final MetaRecognizer recognizer = new MetaRecognizer();

    @Test
    public void patternTest() {
        assertThat(recognizer.getPattern().pattern())
                .isEqualTo("[ ]?(" + ATokenRecognizer.createUpperLowerCaseWord("Meta") + ":)");
    }

    @Test
    public void producedTypeTest() {
        assertThat(recognizer.getProducedType()).isEqualTo(RobotTokenType.SETTING_METADATA_DECLARATION);
    }

    @Test
    public void metaColonWord_allCombinations() {
        final List<String> combinations = new CombinationGenerator().combinations("Meta:");
        for (final String comb : combinations) {
            final StringBuilder textOfHeader = new StringBuilder(comb);

            assertThat(recognizer.hasNext(textOfHeader, 1, 0)).isTrue();
            final RobotToken token = recognizer.next();
            assertThat(token.getStartColumn()).isEqualTo(0);
            assertThat(token.getLineNumber()).isEqualTo(1);
            assertThat(token.getEndColumn()).isEqualTo(textOfHeader.length());
            assertThat(token.getText().toString()).isEqualTo(textOfHeader.toString());
            assertThat(token.getTypes()).containsExactly(recognizer.getProducedType());
        }
    }

    @Test
    public void twoSpacesAndMetaColonThenWord() {
        final StringBuilder text = new StringBuilder(" Meta:");
        final StringBuilder d = new StringBuilder(" ").append(text);

        assertThat(recognizer.hasNext(d, 1, 0)).isTrue();
        final RobotToken token = recognizer.next();
        assertThat(token.getStartColumn()).isEqualTo(1);
        assertThat(token.getLineNumber()).isEqualTo(1);
        assertThat(token.getEndColumn()).isEqualTo(d.length());
        assertThat(token.getText().toString()).isEqualTo(text.toString());
        assertThat(token.getTypes()).containsExactly(recognizer.getProducedType());
    }

    @Test
    public void singleSpaceAndMetaColonThenWord() {
        final StringBuilder text = new StringBuilder(" Meta:");
        final StringBuilder d = new StringBuilder(text).append("C");

        assertThat(recognizer.hasNext(d, 1, 0)).isTrue();
        final RobotToken token = recognizer.next();
        assertThat(token.getStartColumn()).isEqualTo(0);
        assertThat(token.getLineNumber()).isEqualTo(1);
        assertThat(token.getEndColumn()).isEqualTo(text.length());
        assertThat(token.getText().toString()).isEqualTo(text.toString());
        assertThat(token.getTypes()).containsExactly(recognizer.getProducedType());
    }

    @Test
    public void singleMetaColonThenLetterCWord() {
        final StringBuilder text = new StringBuilder("Meta:");
        final StringBuilder d = new StringBuilder(text).append("C");

        assertThat(recognizer.hasNext(d, 1, 0)).isTrue();
        final RobotToken token = recognizer.next();
        assertThat(token.getStartColumn()).isEqualTo(0);
        assertThat(token.getLineNumber()).isEqualTo(1);
        assertThat(token.getEndColumn()).isEqualTo(text.length());
        assertThat(token.getText().toString()).isEqualTo(text.toString());
        assertThat(token.getTypes()).containsExactly(recognizer.getProducedType());
    }

    @Test
    public void singleMetaColonWord() {
        final StringBuilder text = new StringBuilder("Meta:");

        assertThat(recognizer.hasNext(text, 1, 0)).isTrue();
        final RobotToken token = recognizer.next();
        assertThat(token.getStartColumn()).isEqualTo(0);
        assertThat(token.getLineNumber()).isEqualTo(1);
        assertThat(token.getEndColumn()).isEqualTo(text.length());
        assertThat(token.getText().toString()).isEqualTo(text.toString());
        assertThat(token.getTypes()).containsExactly(recognizer.getProducedType());
    }
}
