// ******************************************************************
//
// LayoutContainerImpl.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.layout.BorderPane;

/**
 * @author created: pkruszczynski on 15.01.2019 16:47
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class LayoutContainerImpl implements LayoutContainerIf< BorderPane >
{
    private final Map< String, ContainerIf< ? > > containerIdsMap = new HashMap<>();
    @JsonIgnore
    private BorderPane pane;
    private ContainerIf< ? > mainContainer;

    public LayoutContainerImpl( final ContainerIf< ? > aMainContainer )
    {
        setMainContainer( aMainContainer );
    }

    public LayoutContainerImpl()
    {
    }

    protected BorderPane createNode()
    {
        final BorderPane borderPane = new BorderPane();
        borderPane.setId( "LayoutContainerImpl.BorderPane" );
        if( mainContainer != null )
        {
            borderPane.setCenter( mainContainer.getNode() );
        }
        return borderPane;
    }

    @Override
    public ContainerIf< ? > getContainerById( final String id )
    {
        return containerIdsMap.get( id );
    }

    @Override
    public void storeContainerId( final String id, final ContainerIf< ? > aContainer )
    {
        containerIdsMap.put( id, aContainer );
    }

    @Override
    public ContainerIf< ? > getMainContainer()
    {
        return mainContainer;
    }

    @Override
    public void setMainContainer( final ContainerIf< ? > aMainContainer )
    {
        mainContainer = aMainContainer;
        if( pane != null )
        {
            pane.setCenter( aMainContainer.getNode() );
        }
    }

    @JsonIgnore
    @Override
    public void setNodeCustomizerService( final NodeCustomizerServiceIf aNodeCustomizerService )
    {
    }

    @Override
    public void addNodeCustomizer( final String aId )
    {
        throw new UnsupportedOperationException( "Main container cannot have a customizer." );
    }

    @JsonIgnore
    @Override
    public ContainerIf< ? > getParent()
    {
        return null;
    }

    @JsonIgnore
    @Override
    public void setParent( final ContainerIf< ? > aParent )
    {
        throw new UnsupportedOperationException( "Main container cannot have parent." );
    }

    @Override
    public List< ContainerIf< ? > > getChildren()
    {
        return Collections.singletonList( mainContainer );
    }

    @JsonIgnore
    @Override
    public BorderPane getNode()
    {
        if( pane == null )
        {
            pane = createNode();
            ContainerUtils.storeContainer( this, pane );
        }
        return pane;
    }
}
