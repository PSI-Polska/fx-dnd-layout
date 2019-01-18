/*******************************************************************************
 * Copyright (c) 2014 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package de.psi.pjf.fx.layout.dnd;

import javafx.scene.Node;

/**
 * Drag data
 */
public class DragData
{
    /**
     * The stack
     */
    public final Node container;
    /**
     * The item
     */
    public final Node item;

    /**
     * Create a new drag data instance
     *
     * @param container
     *     the container
     * @param item
     *     the item
     */
    public DragData( Node container, Node item )
    {
        this.container = container;
        this.item = item;
    }
}