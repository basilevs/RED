/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import com.google.common.collect.ImmutableMap;

public class ResolvedImportPathTest {

    @Test
    public void exceptionIsThrown_whenGivenPathIsInvalidUri() {
        assertThatExceptionOfType(URISyntaxException.class)
                .isThrownBy(() -> createResolved(":path/somewhere", Collections.emptyMap()));
    }

    @Test
    public void noResolvedPathIsProvided_whenSomeParametersAreNotPossibleToBeResolved() throws URISyntaxException {
        final Map<String, String> parameters = ImmutableMap.of("${var1}", "val1", "${var2}", "val2");

        assertThat(createResolved("${var}/path/somewhere", parameters)).isNotPresent();
        assertThat(createResolved("${var}/${var1}/path/somewhere", parameters)).isNotPresent();
        assertThat(createResolved("${var}/${var2}/path/somewhere", parameters)).isNotPresent();
    }

    @Test
    public void resolvedPathIsProvided_whenAllParametersAreResolvable() throws URISyntaxException {
        final Map<String, String> parameters = ImmutableMap.of("${var1}", "val1", "${var2}", "val2");

        assertThat(createResolved("val/path/somewhere", parameters))
                .hasValue(new ResolvedImportPath(new URI("val/path/somewhere")));
        assertThat(createResolved("${var1}/path/somewhere", parameters))
                .hasValue(new ResolvedImportPath(new URI("val1/path/somewhere")));
        assertThat(createResolved("${var2}/path/somewhere", parameters))
                .hasValue(new ResolvedImportPath(new URI("val2/path/somewhere")));
        assertThat(createResolved("${var1}/${var2}/path/somewhere", parameters))
                .hasValue(new ResolvedImportPath(new URI("val1/val2/path/somewhere")));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void testPathsResolution_whenResolvedPathIsRelative_inWindows() throws URISyntaxException {
        final Map<String, String> parameters = Collections.emptyMap();
        final ResolvedImportPath resolvedPath = createResolved("relative/path", parameters).get();
        final URI uri = resolvedPath.resolveInRespectTo(new File("c:/some/location").toURI());

        assertThat(uri).isEqualTo(new File("c:/some/relative/path/").toURI());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void testPathsResolution_whenResolvedPathIsRelative_inUnix() throws URISyntaxException {
        final Map<String, String> parameters = Collections.emptyMap();
        final ResolvedImportPath resolvedPath = createResolved("relative/path", parameters).get();
        final URI uri = resolvedPath.resolveInRespectTo(new File("/some/location").toURI());

        assertThat(uri).isEqualTo(new File("/some/relative/path/").toURI());
    }

    private static Optional<ResolvedImportPath> createResolved(final String path, final Map<String, String> parameters)
            throws URISyntaxException {
        return ResolvedImportPath.from(ImportPath.from(path), parameters);
    }
}
