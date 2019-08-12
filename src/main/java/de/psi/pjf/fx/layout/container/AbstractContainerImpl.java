// ******************************************************************
//
// AbstractContainerImpl.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.scene.Node;

/**
 * @param <N>
 *     container node type
 * @param <T>
 *     child's type
 *
 * @author created: pkruszczynski on 14.01.2019 13:27
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public abstract class AbstractContainerImpl< N extends Node, T extends ContainerIf< ? > >
    extends AbstractSimpleContainerImpl< N >
{

    @JsonProperty( value = "children" )
    private final List< T > children = new ArrayList<>();
    @JsonIgnore
    private final List< ContainerIf< ? > > childrenUnmodifiable = Collections.unmodifiableList( children );

    protected final List< T > getChildrenInternal()
    {
        return children;
    }

    @JsonSetter( "children" )
    protected void setChildrenFromDeserialization( final List< T > aChildren )
    {
        children.clear();
        children.addAll( aChildren );
    }

    @JsonGetter( "children" )
    protected List< T > getChildrenForSerialization()
    {
        return children;
    }

    @Override
    public final List< ContainerIf< ? > > getChildren()
    {
        return childrenUnmodifiable;
    }

    @Override
    public int getChildrenCount()
    {
        return children.size();
    }

    @Override
    public void addChild( final ContainerIf< ? > child )
    {
        children.add( (T)child );
        child.setParent( this );
        if( isNodeCreated() )
        {
            addChildFx( child );
        }
    }

    protected abstract void addChildFx( final ContainerIf< ? > child );

    protected abstract void addChildFx( final int index, final ContainerIf< ? > child );

    protected abstract void removeChildFx( final ContainerIf< ? > child );

    @Override
    public int indexOf( final ContainerIf< ? > child )
    {
        return children.indexOf( child );
    }

    @Override
    public void addChild( final int index, final ContainerIf< ? > child )
    {
        children.add( index, (T)child );
        child.setParent( this );
        if( isNodeCreated() )
        {
            addChildFx( index, child );
        }
    }

    @Override
    public void removeChild( final ContainerIf< ? > child )
    {
        if( !children.remove( child ) )
        {
            return;
        }
        child.setParent( null );
        if( isNodeCreated() )
        {
            removeChildFx( child );
        }
    }

}
