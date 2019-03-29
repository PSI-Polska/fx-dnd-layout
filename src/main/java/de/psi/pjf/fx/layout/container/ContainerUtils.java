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
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
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

    public static Optional< ContainerIf< ? > > findAscendantExclusively( final ContainerIf< ? > aContainer,
        final Predicate< ContainerIf< ? > > aPredicate )
    {
        if( aContainer == null )
        {
            return Optional.empty();
        }
        return findAscendantInclusively( aContainer.getParent(), aPredicate );
    }

    public static Optional< ContainerIf< ? > > findAscendantInclusively( final ContainerIf< ? > aContainer,
        final Predicate< ContainerIf< ? > > aPredicate )
    {
        ContainerIf< ? > c = aContainer;
        while( c != null )
        {
            if( aPredicate.test( c ) )
            {
                return Optional.of( c );
            }
            c = c.getParent();
        }
        return Optional.empty();
    }

    public static < T extends ContainerIf< ? > > Optional< T > findAscendantInclusively(
        final ContainerIf< ? > aContainer, final Class< T > aClass )
    {
        return findAscendantInclusively( aContainer, aClass::isInstance ).map( aClass::cast );
    }

    public static < T extends ContainerIf< ? > > Optional< T > findDescendantExclusively(
        final ContainerIf< ? > aContainer, final Class< T > aClass )
    {
        return findDescendantExclusively( aContainer, aClass::isInstance ).map( aClass::cast );
    }

    public static void traverseDescendantExclusively( final ContainerIf< ? > aContainer,
        final Consumer< ContainerIf< ? > > aConsumer )
    {
        findDescendantExclusively( aContainer, aContainerIf -> {
            aConsumer.accept( aContainerIf );
            return false;
        } );
    }

    public static Optional< ContainerIf< ? > > findDescendantExclusively( final ContainerIf< ? > aContainer,
        final Predicate< ContainerIf< ? > > aPredicate )
    {
        if( aContainer == null )
        {
            return Optional.empty();
        }
        for( final ContainerIf< ? > child : aContainer.getChildren() )
        {
            if( aPredicate.test( child ) )
            {
                return Optional.of( child );
            }
            final Optional< ContainerIf< ? > > ret = findDescendantExclusively( child, aPredicate );
            if( ret.isPresent() )
            {
                return ret;
            }
        }
        return Optional.empty();
    }

    public static < T extends ContainerIf< ? > > Optional< T > getContainer( final Node aNode )
    {
        return getContainer( aNode.getProperties() );
    }

    public static < T extends ContainerIf< ? > > Optional< T > getContainer( final Tab aTab )
    {
        return getContainer( aTab.getProperties() );
    }

    public static String getNodeId( final Node aNode )
    {
        return String.valueOf( System.identityHashCode( aNode ) );
    }

    public static ReadOnlyObjectWrapper< FocusedState > restrictValueToContainer(
        final ReadOnlyProperty< FocusedState > focusedContainer,
        final ContainerIf< ? > upperExclusiveRestriction )
    {
        Objects.requireNonNull( upperExclusiveRestriction );
        final ReadOnlyObjectWrapper< FocusedState > ret = new ReadOnlyObjectWrapper<>();
        final Consumer< FocusedState > changedNodeConsumer = aFocusedState -> {
            if( aFocusedState.getType() == FocusedState.Type.NORMAL )
            {
                final ContainerIf< ? > aContainer = aFocusedState.getFocusedContainer();
                if( ContainerUtils.findAscendantExclusively( aContainer, p -> p == upperExclusiveRestriction )
                    .isPresent() )
                {
                    ret.set( new FocusedState( aContainer, FocusedState.Type.NORMAL ) );
                }
                else
                {
                    ret.set( new FocusedState( aContainer, FocusedState.Type.OUT_OF_RESTRICTION ) );
                }
            }
            else
            {
                ret.set( aFocusedState );
            }
        };
        changedNodeConsumer.accept( focusedContainer.getValue() );
        focusedContainer.addListener( ( o, oldV, newV ) -> changedNodeConsumer.accept( newV ) );
        return ret;
    }

    public static void storeContainer( final ContainerIf< ? > aContainer, final Node aNode )
    {
        storeContainer( aContainer, aNode.getProperties() );
    }

    public static void storeContainer( final ContainerIf< ? > aContainer, final Tab aTab )
    {
        storeContainer( aContainer, aTab.getProperties() );
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
