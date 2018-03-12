/*
* Copyright 2017 Nokia Solutions and Networks
* Licensed under the Apache License, Version 2.0,
* see license.txt file for details.
*/
package org.rf.ide.core.execution.agent.event;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.rf.ide.core.execution.agent.Status;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class SuiteEndedEventTest {

    @Test(expected = RuntimeException.class)
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_1() {
        final Map<String, Object> eventMap = ImmutableMap.of();
        SuiteEndedEvent.from(eventMap);
    }

    @Test(expected = RuntimeException.class)
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_2() {
        final Map<String, Object> eventMap = ImmutableMap.of("something",
                newArrayList("suite", ImmutableMap.of("status", "PASS", "elapsedtime", 100, "message", "error")));
        SuiteEndedEvent.from(eventMap);
    }

    @Test(expected = RuntimeException.class)
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_3() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_suite", new Object());
        SuiteEndedEvent.from(eventMap);
    }

    @Test(expected = RuntimeException.class)
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_4() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_suite", newArrayList());
        SuiteEndedEvent.from(eventMap);
    }

    @Test(expected = RuntimeException.class)
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_5() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_suite", newArrayList("suite"));
        SuiteEndedEvent.from(eventMap);
    }

    @Test
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_6() {
        final ImmutableMap<String, ? extends Object> template = ImmutableMap.of("status", "PASS", "elapsedtime", 100,
                "message", "error");

        // from all combinations remove this which consist of all keys
        final Set<Set<String>> allKeysCombinations = newHashSet(
                Sets.powerSet(newHashSet("status", "elapsedtime", "message")));
        allKeysCombinations.remove(newHashSet("status", "elapsedtime", "message"));

        for (final Set<String> combination : allKeysCombinations) {
            final Map<String, Object> attributes = new HashMap<>();
            for (final String key : combination) {
                attributes.put(key, template.get(key));
            }
            final Map<String, Object> eventMap = ImmutableMap.of("end_suite", newArrayList("suite", attributes));

            assertThatIllegalArgumentException().isThrownBy(() -> SuiteEndedEvent.from(eventMap))
                    .withMessage("Suite ended event should have status, elapsed time and message attributes")
                    .withNoCause();
        }
    }

    @Test(expected = RuntimeException.class)
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_7() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_suite",
                newArrayList("suite", ImmutableMap.of("status", "something", "elapsedtime", 100, "message", "error")));
        SuiteEndedEvent.from(eventMap);
    }

    @Test(expected = RuntimeException.class)
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_8() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_suite",
                newArrayList("suite", ImmutableMap.of("status", "PASS", "elapsedtime", "xxx", "message", "error")));
        SuiteEndedEvent.from(eventMap);
    }

    @Test
    public void eventIsProperlyConstructed() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_suite",
                newArrayList("suite", ImmutableMap.of("status", "PASS", "elapsedtime", 100, "message", "error")));
        final SuiteEndedEvent event = SuiteEndedEvent.from(eventMap);

        assertThat(event.getName()).isEqualTo("suite");
        assertThat(event.getStatus()).isEqualTo(Status.PASS);
        assertThat(event.getElapsedTime()).isEqualTo(100);
        assertThat(event.getErrorMessage()).isEqualTo("error");
    }

    @Test
    public void equalsTests() {
        assertThat(new SuiteEndedEvent("suite", 100, Status.PASS, ""))
                .isEqualTo(new SuiteEndedEvent("suite", 100, Status.PASS, ""));
        assertThat(new SuiteEndedEvent("suite", 100, Status.FAIL, "error"))
                .isEqualTo(new SuiteEndedEvent("suite", 100, Status.FAIL, "error"));

        assertThat(new SuiteEndedEvent("suite", 100, Status.PASS, ""))
                .isNotEqualTo(new SuiteEndedEvent("suite1", 100, Status.PASS, ""));
        assertThat(new SuiteEndedEvent("suite1", 100, Status.PASS, ""))
                .isNotEqualTo(new SuiteEndedEvent("suite", 100, Status.PASS, ""));
        assertThat(new SuiteEndedEvent("suite", 100, Status.PASS, ""))
                .isNotEqualTo(new SuiteEndedEvent("suite", 200, Status.PASS, ""));
        assertThat(new SuiteEndedEvent("suite", 200, Status.PASS, ""))
                .isNotEqualTo(new SuiteEndedEvent("suite", 100, Status.PASS, ""));
        assertThat(new SuiteEndedEvent("suite", 100, Status.PASS, ""))
                .isNotEqualTo(new SuiteEndedEvent("suite", 100, Status.FAIL, ""));
        assertThat(new SuiteEndedEvent("suite", 100, Status.FAIL, ""))
                .isNotEqualTo(new SuiteEndedEvent("suite", 100, Status.PASS, ""));
        assertThat(new SuiteEndedEvent("suite", 100, Status.PASS, ""))
                .isNotEqualTo(new SuiteEndedEvent("suite", 100, Status.PASS, "error"));
        assertThat(new SuiteEndedEvent("suite", 100, Status.PASS, "error"))
                .isNotEqualTo(new SuiteEndedEvent("suite", 100, Status.PASS, ""));
        assertThat(new SuiteEndedEvent("suite", 100, Status.PASS, "")).isNotEqualTo(new Object());
        assertThat(new SuiteEndedEvent("suite", 100, Status.PASS, "")).isNotEqualTo(null);
    }

    @Test
    public void hashCodeTests() {
        assertThat(new SuiteEndedEvent("suite", 100, Status.PASS, "").hashCode())
                .isEqualTo(new SuiteEndedEvent("suite", 100, Status.PASS, "").hashCode());
    }
}
