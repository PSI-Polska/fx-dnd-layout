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
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import de.psi.pjf.fx.layout.util.FxUtils;

/**
 * @author created: pkruszczynski on 15.01.2019 16:47
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class LayoutContainerImpl implements LayoutContainerIf< BorderPane >
{
    @JsonMerge
    @JsonProperty( value = "containerIdsMap" )
    private final Map< String, ContainerIf< ? > > containerIdsMap = new HashMap<>();
    @JsonIgnore
    private final FocusTracker focusTracker = new FocusTracker();
    @JsonIgnore
    private BorderPane pane;
    private ContainerIf< ? > mainContainer;
    @JsonIgnore
    private Map< Object, Object > properties;

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
        FxUtils.executeOnceWhenPropertyIsNonNull( borderPane.sceneProperty(), scene -> {
            focusTracker.setScene( scene );
            scene.focusOwnerProperty().addListener( focusTracker );
        } );
        return borderPane;
    }

    @Override
    public void dispose()
    {
        focusTracker.dispose();
    }

    @Override
    public ReadOnlyObjectProperty< ContainerIf< ? > > focusedContainerProperty()
    {
        return focusTracker.focusedContainerProperty();
    }

    @Override
    public ContainerIf< ? > getFocusedContainer()
    {
        return focusTracker.getFocusedContainer();
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
    public ContainerIf< ? > removeStored( final String id )
    {
        return containerIdsMap.remove( id );
    }

    @Override
    public ContainerIf< ? > getMainContainer()
    {
        return mainContainer;
    }

    @Override
    public void setMainContainer( final ContainerIf< ? > aMainContainer )
    {
        if( mainContainer != null )
        {
            mainContainer.setParent( null );
        }
        mainContainer = aMainContainer;
        mainContainer.setParent( this );
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

    @Override
    public int getChildrenCount()
    {
        return mainContainer == null ? 0 : 1;
    }

    @Override
    public int indexOf( final ContainerIf< ? > child )
    {
        return mainContainer == child ? 0 : -1;
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
    public Map< Object, Object > getProperties()
    {
        if( properties == null )
        {
            properties = new HashMap<>();
        }
        return properties;
    }

    private static class FocusTracker implements ChangeListener< Node >
    {
        private final ReadOnlyObjectWrapper< ContainerIf< ? > > focusedContainer =
            new ReadOnlyObjectWrapper<>();
        private Scene scene;

        @Override
        public void changed( final ObservableValue< ? extends Node > aObservableValue, final Node oldValue,
            final Node newValue )
        {
            if( newValue != null )
            {
                ContainerIf< ? > foundContainer = null;
                Node n = newValue;
                while( n != null && foundContainer == null )
                {
                    final Optional< ContainerIf< ? > > container = ContainerUtils.getContainer( n );
                    if( container.isPresent() )
                    {
                        foundContainer = container.get();
                    }
                    n = n.getParent();
                }
                focusedContainer.set( foundContainer );
            }
            else
            {
                focusedContainer.set( null );
            }
        }

        public void dispose()
        {
            focusedContainer.set( null );
            scene.focusOwnerProperty().removeListener( this );
            this.scene = null;
        }

        public ReadOnlyObjectProperty< ContainerIf< ? > > focusedContainerProperty()
        {
            return focusedContainer.getReadOnlyProperty();
        }

        public ContainerIf< ? > getFocusedContainer()
        {
            return focusedContainer.get();
        }

        public void setScene( final Scene aScene )
        {
            scene = aScene;
        }
    }
}
