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
 * Interface implemented by drop locations basic types are provided by
 * {@link BasicDropLocation}
 */
public interface DropLocation
{
    /**
     * @return is it a reordering (might include a reparent)
     */
    public boolean isReorder();

    /**
     * @return if it is a split (might include a reparent)
     */
    public boolean isSplit();

    /**
     * @return if it is a insert (might include a reparent)
     */
    public boolean isInsert();

    /**
     * @return if it is a detach
     */
    public boolean isDetach();

    /**
     * @return if it is a custom type
     */
    public boolean isCustom();
}
