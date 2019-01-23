// ******************************************************************
//
// FxUtils.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * @author created: pkruszczynski on 22.01.2019 10:18
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public final class FxUtils
{

    private FxUtils()
    {
    }

    /**
     * @see com.sun.javafx.scene.control.skin.Utils#executeOnceWhenPropertyIsNonNull(ObservableValue, Consumer)
     */
    public static < T > void executeOnceWhenPropertyIsNonNull( final ObservableValue< T > aObservable,
        final Consumer< T > aConsumer )
    {
        if( aObservable == null )
        {
            return;
        }
        T value = aObservable.getValue();
        if( value != null )
        {
            aConsumer.accept( value );
        }
        else
        {
            final InvalidationListener listener = new InvalidationListener()
            {
                @Override
                public void invalidated( Observable observable )
                {
                    T value = aObservable.getValue();

                    if( value != null )
                    {
                        aObservable.removeListener( this );
                        aConsumer.accept( value );
                    }
                }
            };
            aObservable.addListener( listener );
        }
    }

    /**
     * Similar to {@link #executeOnceWhenPropertyIsNonNull(ObservableValue, Consumer)}, but invokes
     * <code>aConsumer</code> only if <code>aObservable</code> has a <code>expectedValue</code>.
     */
    public static < T > void executeOnceWhenPropertyHasExpectedValue( final ObservableValue< T > aObservable,
        final T expectedValue, final Consumer< T > aConsumer )
    {
        if( aObservable == null )
        {
            return;
        }
        T value = aObservable.getValue();
        if( Objects.equals( value, expectedValue ) )
        {
            aConsumer.accept( value );
        }
        else
        {
            final InvalidationListener listener = new InvalidationListener()
            {
                @Override
                public void invalidated( Observable observable )
                {
                    T value = aObservable.getValue();

                    if( Objects.equals( value, expectedValue ) )
                    {
                        aObservable.removeListener( this );
                        aConsumer.accept( value );
                    }
                }
            };
            aObservable.addListener( listener );
        }
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
                final Node ret = findDescendantInclusively( node, aContainerMatcher );
                if( ret != null )
                {
                    return ret;
                }
            }
        }
        return null;
    }

    public static Node findNodeWithId( final Parent root, final String id )
    {
        return findDescendantInclusively( root, getContainerMatcher( id ) );
    }

    public static Predicate< Node > getContainerMatcher( final String id )
    {
        Objects.requireNonNull( id );
        return node -> id.equals( String.valueOf( System.identityHashCode( node ) ) );
    }
}
