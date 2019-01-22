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

import de.psi.pjf.fx.layout.container.ContainerIf;

/**
 * Drop data
 */
public class DropData
{
    /**
     * The reference element
     */
    public final ContainerIf< ? > reference;
    /**
     * The source element
     */
    public final ContainerIf< ? > sourceElement;
    /**
     * The drop type
     */
    public final DropLocation dropType;

    /**
     * The x coordinate relative to the screen
     */
    public final double x;
    /**
     * The y coordinate relative to the screen
     */
    public final double y;

    /**
     * Create new drop data
     *
     * @param x
     *     the x coordinate of the drop relative to the screen
     * @param y
     *     the y coordinate of the drop relative to the screen
     * @param reference
     *     the reference
     * @param sourceElement
     *     the source element
     * @param dropType
     *     the drop type
     */
    public DropData( double x, double y, ContainerIf< ? > reference, ContainerIf< ? > sourceElement,
        DropLocation dropType )
    {
        this.x = x;
        this.y = y;
        this.reference = reference;
        this.sourceElement = sourceElement;
        this.dropType = dropType;
    }
}