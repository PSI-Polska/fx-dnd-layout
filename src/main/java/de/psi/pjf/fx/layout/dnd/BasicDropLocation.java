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

/**
 * The drop type
 */
public enum BasicDropLocation implements DropLocation
{
    /**
     * Before the reference element
     */
    BEFORE,
    /**
     * After the reference element
     */
    AFTER,
    /**
     * Detach the reference element
     */
    DETACH,
    /**
     * Insert the element into the reference
     */
    INSERT,
    /**
     * Split vertical and put the new item above the other
     */
    SPLIT_TOP,
    /**
     * Split vertical and put the new item below the other
     */
    SPLIT_BOTTOM,
    /**
     * split horizontal and put the new item left to the
     * other
     */
    SPLIT_LEFT,
    /**
     * split horizontal and put the new item right to the other
     */
    SPLIT_RIGHT;

    @Override
    public boolean isReorder()
    {
        return this == BEFORE || this == AFTER;
    }

    @Override
    public boolean isSplit()
    {
        return this == SPLIT_TOP || this == SPLIT_BOTTOM || this == SPLIT_LEFT || this == SPLIT_RIGHT;
    }

    @Override
    public boolean isInsert()
    {
        return this == INSERT;
    }

    @Override
    public boolean isDetach()
    {
        return this == DETACH;
    }

    @Override
    public boolean isCustom()
    {
        return false;
    }}
