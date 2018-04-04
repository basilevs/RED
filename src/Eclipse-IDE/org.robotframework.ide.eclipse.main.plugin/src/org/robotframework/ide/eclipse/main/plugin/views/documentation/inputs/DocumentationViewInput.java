/*
 * Copyright 2018 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.views.documentation.inputs;

import org.eclipse.ui.IWorkbenchPage;

public abstract class DocumentationViewInput {

    public abstract boolean contains(final Object wrappedInput);

    public abstract void prepare();

    public abstract String provideHtml();

    public abstract void showInput(IWorkbenchPage page);

}