/*******************************************************************************
 * Copyright (c) 2015 BestSolution.at and others.
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
 * Service to constraint drag and drop operations
 */
public interface DndService
{
    /**
     * Check if a split is allowed
     *
     * @param element
     *     the element
     * @param dropType
     *     the drop type
     * @param sourceElement
     *     the source element
     *
     * @return <code>true</code> if a split is allowed
     */
    boolean splitAllowed( ContainerIf< ? > element, ContainerIf< ? > sourceElement, DropLocation dropType );

    /**
     * Check if a detach is allowed
     *
     * @param element
     *     the element to detach
     *
     * @return <code>true</code> if a detach is allowed
     */
    boolean detachAllowed( ContainerIf< ? > element );

    /**
     * Check if reordering is allowed
     *
     * @param reference
     *     the reference element (=sibling)
     * @param sourceElement
     *     the source element
     * @param dropLocation
     *     the drop location
     *
     * @return <code>true</code> if a drop is allowed there
     */
    boolean reorderAllowed( ContainerIf< ? > reference, ContainerIf< ? > sourceElement,
        DropLocation dropLocation );

    /**
     * Check if a insert is allowed
     *
     * @param reference
     *     the reference (=container)
     * @param sourceElement
     *     the source element
     *
     * @return <code>true</code> if a drop is allowed there
     */
    boolean insertAllowed( ContainerIf< ? > reference, ContainerIf< ? > sourceElement );

    /**
     * Check if we can reparent the element
     *
     * @param element
     *     the element
     *
     * @return <code>true</code> if a reparent is allowed
     *
     * @deprecated
     */
    default boolean repartentAllowed( ContainerIf< ? > element )
    {
        return true;
    }

    /**
     * Check if we can reparent the element
     *
     * @param element
     *     the element
     *
     * @return <code>true</code> if a reparent is allowed
     */
    default boolean reparentAllowed( ContainerIf< ? > element, ContainerIf< ? > reference )
    {
        return repartentAllowed( element );
    }

    /**
     * Handle the detaching of an element
     *
     * @param x
     *     the x coordinate the new window should show up relative to the
     *     screen
     * @param y
     *     the y coordinate the new window should show up relative to the
     *     screen
     * @param sourceElement
     *     the source element
     *
     * @return <code>true</code> if detaching is handled or <code>false</code>
     * if the default should be used
     */
    boolean handleDetach( double x, double y, ContainerIf< ? > sourceElement );

    /**
     * Handle the reordering
     *
     * @param reference
     *     the reference (=sibling)
     * @param sourceElement
     *     the source element
     * @param dropLocation
     *     the drop location
     *
     * @return <code>true</code> if handled or <code>false</code> if the default
     * should be used
     */
    boolean handleReorder( ContainerIf< ? > reference, ContainerIf< ? > sourceElement,
        DropLocation dropLocation );

    /**
     * Handle the insert
     *
     * @param reference
     *     the reference (=container)
     * @param sourceElement
     *     the source element
     *
     * @return <code>true</code> if handled or <code>false</code> if the default
     * should be used
     */
    boolean handleInsert( ContainerIf< ? > reference, ContainerIf< ? > sourceElement );

    /**
     * Handle the split
     *
     * @param reference
     *     the reference
     * @param sourceElement
     *     the source element
     * @param dropLocation
     *     the location
     *
     * @return <code>true</code> if handled or <code>false</code> if the default
     * should be used
     */
    boolean handleSplit( ContainerIf< ? > reference, ContainerIf< ? > sourceElement,
        DropLocation dropLocation );

    /**
     * Check if the element can be dragged at all
     *
     * @param container
     *     the container
     * @param element
     *     the element
     *
     * @return <code>true</code> if a drag can start
     *
     * @since 2.2.0
     */
    default boolean dragAllowed( ContainerIf< ? > container, ContainerIf< ? > element )
    {
        return true;
    }

}
