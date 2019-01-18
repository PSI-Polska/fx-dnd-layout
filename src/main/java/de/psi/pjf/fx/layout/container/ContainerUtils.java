// ******************************************************************
//
// ContainerUtils.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

/**
 * @author created: pkruszczynski on 14.01.2019 15:52
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public final class ContainerUtils
{
    private static final String CONTAINER_PROPERTY_KEY = "LayoutContainer";

    private ContainerUtils()
    {
    }

    public static Node findAscendantInclusively( final Node aRoot, final Predicate< Node > aContainerMatcher )
    {
        if( aRoot == null )
        {
            return null;
        }
        if( aContainerMatcher.test( aRoot ) )
        {
            return aRoot;
        }
        return findAscendantInclusively( aRoot.getParent(), aContainerMatcher );
    }

    public static Node findDescendantInclusively( final Node aNode,
        final Predicate< Node > aContainerMatcher )
    {
        if( aContainerMatcher.test( aNode ) )
        {
            return aNode;
        }
        if( aNode instanceof Parent )
        {
            for( final Node node : ( (Parent)aNode ).getChildrenUnmodifiable() )
            {
                if( aContainerMatcher.test( node ) )
                {
                    return node;
                }
                return findDescendantInclusively( node, aContainerMatcher );
            }
        }
        return null;
    }

    public static Node findNodeWithId( final Parent root, final String id )
    {
        return findDescendantInclusively( root, getContainerMatcher( id ) );
    }

    public static < T extends ContainerIf< ? > > Optional< T > getContainer( final Tab aTab )
    {
        return getContainer( aTab.getProperties() );
    }

    public static < T extends ContainerIf< ? > > Optional< T > getContainer( final Node aNode )
    {
        return getContainer( aNode.getProperties() );
    }

    public static Predicate< Node > getContainerMatcher( final String id )
    {
        Objects.requireNonNull( id );
        return node -> id.equals( String.valueOf( System.identityHashCode( node ) ) );
    }

    public static String getNodeId( final Node aNode )
    {
        return String.valueOf( System.identityHashCode( aNode ) );
    }

    public static void storeContainer( final ContainerIf< ? > aContainer, final Tab aTab )
    {
        storeContainer( aContainer, aTab.getProperties() );
    }

    public static void storeContainer( final ContainerIf< ? > aContainer, final Node aNode )
    {
        storeContainer( aContainer, aNode.getProperties() );
    }

    private static void storeContainer( final ContainerIf< ? > aContainer,
        final ObservableMap< Object, Object > aProperties )
    {
        aProperties.put( CONTAINER_PROPERTY_KEY, aContainer );
    }

    @SuppressWarnings( "unchecked" )
    private static < T extends ContainerIf< ? > > Optional< T > getContainer(
        final ObservableMap< Object, Object > aProperties )
    {
        final Object propValue = aProperties.getOrDefault( CONTAINER_PROPERTY_KEY, null );
        if( propValue instanceof ContainerIf< ? > )
        {
            return Optional.of( (T)propValue );
        }
        return Optional.empty();
    }

}
