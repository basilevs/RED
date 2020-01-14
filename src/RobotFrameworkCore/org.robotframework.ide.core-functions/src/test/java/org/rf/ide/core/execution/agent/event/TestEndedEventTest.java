/*
* Copyright 2017 Nokia Solutions and Networks
* Licensed under the Apache License, Version 2.0,
* see license.txt file for details.
*/
package org.rf.ide.core.execution.agent.event;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.rf.ide.core.execution.agent.Status;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class TestEndedEventTest {

    @Test
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_1() {
        final Map<String, Object> eventMap = ImmutableMap.of();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> TestEndedEvent.from(eventMap));
    }

    @Test
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_2() {
        final Map<String, Object> eventMap = ImmutableMap.of("something",
                newArrayList("test", ImmutableMap.of("status", "PASS", "elapsedtime", 100, "message", "error")));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> TestEndedEvent.from(eventMap));
    }

    @Test
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_3() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_test", new Object());
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> TestEndedEvent.from(eventMap));
    }

    @Test
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_4() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_test", newArrayList());
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> TestEndedEvent.from(eventMap));
    }

    @Test
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_5() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_test", newArrayList("test"));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> TestEndedEvent.from(eventMap));
    }

    @Test
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_6() {
        final ImmutableMap<String, ? extends Object> template = ImmutableMap.of("longname", "s.test", "status", "PASS",
                "elapsedtime", 100, "message", "error");

        // from all combinations remove this which consist of all keys
        final Set<Set<String>> allKeysCombinations = newHashSet(
                Sets.powerSet(newHashSet("longname", "status", "elapsedtime", "message")));
        allKeysCombinations.remove(newHashSet("longname", "status", "elapsedtime", "message"));

        for (final Set<String> combination : allKeysCombinations) {
            final Map<String, Object> attributes = new HashMap<>();
            for (final String key : combination) {
                attributes.put(key, template.get(key));
            }
            final Map<String, Object> eventMap = ImmutableMap.of("end_test", newArrayList("test", attributes));

            assertThatIllegalArgumentException().isThrownBy(() -> TestEndedEvent.from(eventMap))
                    .withMessage(
                            "Test ended event should have long name, status, elapsed time and message attributes")
                    .withNoCause();
        }
    }

    @Test
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_7() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_test", newArrayList("test",
                ImmutableMap.of("longname", "s.test", "status", "something", "elapsedtime", 100, "message", "error")));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> TestEndedEvent.from(eventMap));
    }

    @Test
    public void exceptionIsThrownForWronglyConstructedJsonDictionary_8() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_test", newArrayList("test",
                ImmutableMap.of("longname", "s.test", "status", "PASS", "elapsedtime", "xxx", "message", "error")));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> TestEndedEvent.from(eventMap));
    }

    @Test
    public void eventIsProperlyConstructed_whenThereIsNoOriginalName() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_test", newArrayList("test",
                ImmutableMap.of("longname", "s.test", "status", "PASS", "elapsedtime", 100, "message", "error")));
        final TestEndedEvent event = TestEndedEvent.from(eventMap);

        assertThat(event.getName()).isEqualTo("test");
        assertThat(event.getResolvedName()).isEqualTo("test");
        assertThat(event.getLongName()).isEqualTo("s.test");
        assertThat(event.getStatus()).isEqualTo(Status.PASS);
        assertThat(event.getElapsedTime()).isEqualTo(100);
        assertThat(event.getErrorMessage()).isEqualTo("error");
    }

    @Test
    public void eventIsProperlyConstructed_whenThereIsOriginalName_1() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_test",
                newArrayList("test", ImmutableMap.of("longname", "s.test", "status", "PASS", "elapsedtime", 100,
                        "message", "error", "originalname", "test")));
        final TestEndedEvent event = TestEndedEvent.from(eventMap);

        assertThat(event.getName()).isEqualTo("test");
        assertThat(event.getResolvedName()).isEqualTo("test");
        assertThat(event.getLongName()).isEqualTo("s.test");
        assertThat(event.getStatus()).isEqualTo(Status.PASS);
        assertThat(event.getElapsedTime()).isEqualTo(100);
        assertThat(event.getErrorMessage()).isEqualTo("error");
    }

    @Test
    public void eventIsProperlyConstructed_whenThereIsOriginalName_2() {
        final Map<String, Object> eventMap = ImmutableMap.of("end_test",
                newArrayList("test 1", ImmutableMap.of("longname", "s.test 1", "status", "PASS", "elapsedtime", 100,
                        "message", "error", "originalname", "test ${var}")));
        final TestEndedEvent event = TestEndedEvent.from(eventMap);

        assertThat(event.getName()).isEqualTo("test ${var}");
        assertThat(event.getResolvedName()).isEqualTo("test 1");
        assertThat(event.getLongName()).isEqualTo("s.test 1");
        assertThat(event.getStatus()).isEqualTo(Status.PASS);
        assertThat(event.getElapsedTime()).isEqualTo(100);
        assertThat(event.getErrorMessage()).isEqualTo("error");
    }

    @Test
    public void equalsTests() {
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""))
                .isEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""));
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.FAIL, "error"))
                .isEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.FAIL, "error"));
        assertThat(new TestEndedEvent("test", null, "s.test", 100, Status.PASS, ""))
                .isEqualTo(new TestEndedEvent("test", null, "s.test", 100, Status.PASS, ""));

        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""))
                .isNotEqualTo(new TestEndedEvent("test1", "test1", "s.test", 100, Status.PASS, ""));
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""))
                .isNotEqualTo(new TestEndedEvent("test", "test1", "s.test", 100, Status.PASS, ""));
        assertThat(new TestEndedEvent("test", "test1", "s.test", 100, Status.PASS, ""))
                .isNotEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""));
        assertThat(new TestEndedEvent("test1", "test1", "s.test", 100, Status.PASS, ""))
                .isNotEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""));
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""))
                .isNotEqualTo(new TestEndedEvent("test", "test", "s.test1", 100, Status.PASS, ""));
        assertThat(new TestEndedEvent("test", "test", "s.test1", 100, Status.PASS, ""))
                .isNotEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""));
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""))
                .isNotEqualTo(new TestEndedEvent("test", "test", "s.test", 200, Status.PASS, ""));
        assertThat(new TestEndedEvent("test", "test", "s.test", 200, Status.PASS, ""))
                .isNotEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""));
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""))
                .isNotEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.FAIL, ""));
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.FAIL, ""))
                .isNotEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""));
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""))
                .isNotEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, "error"));
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, "error"))
                .isNotEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, ""));
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, "")).isNotEqualTo(new Object());
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, "")).isNotEqualTo(null);
    }

    @Test
    public void hashCodeTests() {
        assertThat(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, "").hashCode())
                .isEqualTo(new TestEndedEvent("test", "test", "s.test", 100, Status.PASS, "").hashCode());
        assertThat(new TestEndedEvent("test", null, "s.test", 100, Status.PASS, "").hashCode())
                .isEqualTo(new TestEndedEvent("test", null, "s.test", 100, Status.PASS, "").hashCode());
    }
}
