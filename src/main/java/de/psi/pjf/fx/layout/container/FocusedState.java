// ******************************************************************
//
// FocusedState.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.Objects;

/**
 * Represent focused state to distinguish container focus change between null and out of restricted state.
 */
public class FocusedState
{

    private final ContainerIf< ? > focusedContainer;
    private final Type type;

    public FocusedState( final ContainerIf< ? > aFocusedContainer,
        final Type aType )
    {
        focusedContainer = aFocusedContainer;
        type = aType;
    }

    public ContainerIf< ? > getFocusedContainer()
    {
        return focusedContainer;
    }

    public Type getType()
    {
        return type;
    }

    @Override
    public boolean equals( final Object aO )
    {
        if( this == aO )
        {
            return true;
        }
        if( aO == null || getClass() != aO.getClass() )
        {
            return false;
        }
        final FocusedState that = (FocusedState)aO;
        return Objects.equals( focusedContainer, that.focusedContainer )
            && type == that.type;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( focusedContainer, type );
    }

    public enum Type
    {
        NORMAL,
        OUT_OF_LAYOUT,
        OUT_OF_RESTRICTION,
        NULL
    }
}
