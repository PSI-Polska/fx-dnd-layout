// ******************************************************************
//
// TabContainerWrapperImpl.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * @author created: pkruszczynski on 14.01.2019 13:40
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class TabContainerWrapperImpl< N extends Node > extends AbstractSimpleContainerImpl< N >
    implements TabContainerWrapperIf< N >
{

    @JsonIgnore
    private Tab tab;
    private String name;
    private final ContainerIf< N > content;
    @JsonIgnore
    private final List< ContainerIf< ? > > singleChildren;

    @JsonCreator
    public TabContainerWrapperImpl( @JsonProperty( value = "content" ) final ContainerIf< N > aContainer )
    {
        content = Objects.requireNonNull( aContainer );
        singleChildren = Collections.singletonList( content );
        content.setParent( this );
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName( final String aName )
    {
        name = aName;
        if( isNodeCreated() )
        {
            tab.setText( aName );
        }
    }

    @Override
    public ContainerIf< N > getContent()
    {
        return content;
    }

    @Override
    public Tab getTab()
    {
        if( tab == null )
        {
            tab = createTab();
            ContainerUtils.storeContainer( this, tab );
            tab.setContent( getNode() );
            tab.getStyleClass().add( ContainerStylesConstants.TAB_CONTAINER_STYLE_CLASS );
        }
        return tab;
    }

    protected Tab createTab()
    {
        final Tab tab = new Tab();
        if( name != null )
        {
            tab.setText( name );
        }
        tab.setOnCloseRequest( c -> {
            if( handleCloseRequest( tab ) )
            {
                c.consume();
            }
        } );
        return tab;
    }

    /**
     * Provides custom implementation for handling tab closing request.
     *
     * @param aTab
     *     tab which is being closed
     *
     * @return true if closing event should be consumed
     */
    protected boolean handleCloseRequest( final Tab aTab )
    {
        return false;
    }

    @Override
    public List< ContainerIf< ? > > getChildren()
    {
        return singleChildren;
    }

    @Override
    public int getChildrenCount()
    {
        return 1;
    }

    @Override
    public int indexOf( final ContainerIf< ? > child )
    {
        return child == content ? 0 : -1;
    }

    @Override
    protected N createNode()
    {
        final N node = content.getNode();
        return node;
    }

    @Override
    public void setParent( final ContainerIf< ? > aParent )
    {
        if( aParent != null && !( aParent instanceof StackContainerIf< ? > ) )
        {
            throw new IllegalArgumentException(
                "TabContainerWrapper's parent shall be a StackContainerIf implementation." );
        }
        super.setParent( aParent );
    }

    @Override
    public StackContainerIf< ? > getParent()
    {
        return (StackContainerIf< ? >)super.getParent();
    }

    @Override
    public void dispose()
    {
        if( tab != null )
        {
            tab.setOnCloseRequest( null );
            ContainerUtils.clearContainerData( tab );
        }
        super.dispose();
    }
}
