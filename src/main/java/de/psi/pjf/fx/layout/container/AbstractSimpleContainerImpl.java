// ******************************************************************
//
// AbstractContainerImpl.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.Node;

/**
 * @param <N>
 *     container node type
 *
 * @author created: pkruszczynski on 14.01.2019 13:27
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public abstract class AbstractSimpleContainerImpl< N extends Node > implements ContainerIf< N >
{

    @JsonIgnore
    private transient boolean nodeCreated = false;
    @JsonIgnore
    private N node;
    private ContainerIf< ? > parent;
    @JsonProperty
    private Set< String > nodeCustomizerIds;
    @JsonIgnore
    @JacksonInject
    private NodeCustomizerServiceIf nodeCustomizerService;

    protected abstract N createNode();

    protected final boolean isNodeCreated()
    {
        return nodeCreated;
    }

    protected void postNodeCreation( final Node aNode )
    {
    }

    @JsonIgnore
    Set< String > getNodeCustomizerIds()
    {
        return nodeCustomizerIds;
    }

    @Override
    public final N getNode()
    {
        if( node == null && !nodeCreated )
        {
            node = createNode();
            ContainerUtils.storeContainer( this, node );
            if( nodeCustomizerIds != null && !nodeCustomizerIds.isEmpty() )
            {
                applyCustomizers( node );
            }
            nodeCreated = true;
            postNodeCreation( node );
        }
        return node;
    }

    private void applyCustomizers( final Node aNode )
    {
        if( nodeCustomizerService == null )
        {
            throw new IllegalStateException(
                "Node customizer service should be set if customizer keys are stored in this widget." );
        }
        for( final String nodeCustomizerId : nodeCustomizerIds )
        {
            final NodeCustomizerIf nodeCustomizer =
                nodeCustomizerService.getNodeCustomizer( nodeCustomizerId );
            if( nodeCustomizer == null )
            {
                throw new IllegalStateException(
                    "Customizer with id '" + nodeCustomizerId + "' was not found." );
            }
            nodeCustomizer.customize( this, aNode );
        }
    }

    @Override
    public void setNodeCustomizerService( final NodeCustomizerServiceIf aNodeCustomizerService )
    {
        nodeCustomizerService = aNodeCustomizerService;
    }

    @Override
    public void addNodeCustomizer( final String aId )
    {
        if( nodeCustomizerIds == null )
        {
            nodeCustomizerIds = new LinkedHashSet<>();
        }
        if( nodeCustomizerIds.add( aId ) && isNodeCreated() )
        {
            final NodeCustomizerIf nodeCustomizer = nodeCustomizerService.getNodeCustomizer( aId );
            if( nodeCustomizer == null )
            {
                throw new IllegalStateException( "Customizer with id '" + aId + "' was not found." );
            }
            nodeCustomizer.customize( this, getNode() );
        }
    }

    @Override
    public ContainerIf< ? > getParent()
    {
        return parent;
    }

    @Override
    public void setParent( final ContainerIf< ? > aParent )
    {
        parent = aParent;
    }

    @Override
    public void addChild( final ContainerIf< ? > child )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addChild( final int index, final ContainerIf< ? > child )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeChild( final ContainerIf< ? > child )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dispose()
    {
    }
}
