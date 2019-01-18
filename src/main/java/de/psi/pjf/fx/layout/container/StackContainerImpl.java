// ******************************************************************
//
// StackContainerImpl.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * @author created: pkruszczynski on 14.01.2019 12:19
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class StackContainerImpl extends AbstractContainerImpl< TabPane, TabContainerWrapperIf< ? > >
    implements StackContainerIf< TabPane >
{
    @JsonIgnore
    private final List< TabContainerWrapperIf< ? > > tabsUnmodifiable =
        Collections.unmodifiableList( getChildrenInternal() );
    private TabContainerWrapperIf< ? > selectedTab;
    private Side side;

    protected TabPane createTabPane()
    {
        return new TabPane();
    }

    @Override
    protected TabPane createNode()
    {
        final TabPane tabPane = createTabPane();
        if( side != null )
        {
            tabPane.setSide( side );
        }
        tabPane.getTabs().addListener( (ListChangeListener< ? super Tab >)aChange -> {
            while( aChange.next() )
            {
                aChange.getRemoved().stream().map( ContainerUtils::getContainer ).flatMap( Optional::stream )
                    .map( TabContainerWrapperIf.class::cast ).forEach( getChildrenInternal()::remove );
                final List< TabContainerWrapperIf< ? > > addedTabWrappers =
                    aChange.getAddedSubList().stream().map( ContainerUtils::getContainer )
                        .flatMap( Optional::stream ).map( e -> (TabContainerWrapperIf< ? >)e )
                        .collect( Collectors.toList() );
                getChildrenInternal().addAll( aChange.getFrom(), addedTabWrappers );
            }
        } );
        final List< Tab > tabsToBeSet = getChildrenInternal().stream().map( TabContainerWrapperIf::getTab )
            .collect( Collectors.toList() );
        tabPane.getTabs().setAll( tabsToBeSet );
        if( selectedTab != null )
        {
            tabPane.getSelectionModel().select( selectedTab.getTab() );
        }
        tabPane.getSelectionModel().selectedItemProperty()
            .addListener( ( observable, oldValue, newValue ) -> {
                if( newValue != null )
                {
                    selectedTab =
                        ContainerUtils.< TabContainerWrapperIf< ? > >getContainer( newValue ).orElseThrow();
                }
                else
                {
                    selectedTab = null;
                }
            } );
        return tabPane;
    }

    @Override
    public int indexOf( final TabContainerWrapperIf< ? > aTab )
    {
        return getChildrenInternal().indexOf( aTab );
    }

    @Override
    public boolean remove( final TabContainerWrapperIf< ? > aTab )
    {
        if( isNodeCreated() )
        {
            return getNode().getTabs().remove( aTab.getTab() );
        }
        else
        {
            return getChildrenInternal().remove( aTab );
        }
    }

    @Override
    public void add( final TabContainerWrapperIf< ? > aTab )
    {
        aTab.setParent( this );
        if( isNodeCreated() )
        {
            getNode().getTabs().add( aTab.getTab() );
        }
        else
        {
            getChildrenInternal().add( aTab );
        }
    }

    @Override
    public void add( final int index, final TabContainerWrapperIf< ? > aTab )
    {
        aTab.setParent( this );
        if( isNodeCreated() )
        {
            getNode().getTabs().add( index, aTab.getTab() );
        }
        else
        {
            getChildrenInternal().add( index, aTab );
        }
    }

    @JsonIgnore
    @Override
    public int getTabNumber()
    {
        return getChildrenInternal().size();
    }

    @Override
    public void select( final TabContainerWrapperIf< ? > aTab )
    {
        if( isNodeCreated() )
        {
            getNode().getSelectionModel().select( aTab.getTab() );
        }
        else
        {
            selectedTab = aTab;
        }
    }

    @JsonIgnore
    @Override
    public List< TabContainerWrapperIf< ? > > getTabWrappers()
    {
        return tabsUnmodifiable;
    }

    protected Optional< Tab > findTab( final Predicate< Tab > aMatcher )
    {
        return getNode().getTabs().stream().filter( aMatcher ).findAny();
    }

    private class MappingTransformationList< S, T > extends TransformationList< T, S >
    {

        private final Function< S, T > mappingFunction;

        public MappingTransformationList( final ObservableList< ? extends S > aObservableList,
            final Function< S, T > mappingFunction )
        {
            super( aObservableList );
            this.mappingFunction = Objects.requireNonNull( mappingFunction );
        }

        private List< T > getRemovedElements( ListChangeListener.Change< ? extends S > c )
        {
            return convertToTarget( c.getRemoved() );
        }

        @Override
        protected void sourceChanged( final ListChangeListener.Change< ? extends S > c )
        {
            beginChange();

            while( c.next() )
            {
                if( c.wasReplaced() )
                {
                    List< ? extends S > removed = c.getRemoved();
                    List< ? extends S > added = c.getAddedSubList();
                    if( !removed.equals( added ) )
                    {
                        nextReplace( c.getFrom(), c.getTo(), convertToTarget( removed ) );
                    }
                }
                else if( c.wasAdded() )
                {
                    nextAdd( c.getFrom(), c.getTo() );
                }
                else if( c.wasRemoved() )
                {
                    int removedSize = c.getRemovedSize();
                    if( removedSize == 1 )
                    {
                        nextRemove( c.getFrom(), getRemovedElements( c ).get( 0 ) );
                    }
                    else
                    {
                        nextRemove( c.getFrom(), getRemovedElements( c ) );
                    }
                }
                else if( c.wasPermutated() )
                {
                    int[] permutation = new int[ size() ];
                    for( int i = 0; i < size(); i++ )
                    {
                        permutation[ i ] = c.getPermutation( i );
                    }
                    nextPermutation( c.getFrom(), c.getTo(), permutation );
                }
                else if( c.wasUpdated() )
                {
                    for( int i = c.getFrom(); i < c.getTo(); i++ )
                    {
                        nextUpdate( i );
                    }
                }
            }
            endChange();
        }

        protected final List< T > convertToTarget( final List< ? extends S > aSource )
        {
            return aSource.stream().map( mappingFunction ).collect( Collectors.toList() );
        }

        @Override
        public int getSourceIndex( final int index )
        {
            return index;
        }

        @Override
        public int getViewIndex( final int index )
        {
            return index;
        }

        @Override
        public T get( final int index )
        {
            return mappingFunction.apply( getSource().get( getSourceIndex( index ) ) );
        }

        @Override
        public int size()
        {
            return getSource().size();
        }
    }

    @Override
    public void setSide( final Side aSide )
    {
        side = aSide;
        if( isNodeCreated() )
        {
            getNode().setSide( aSide );
        }
    }

    @Override
    public Side getSide()
    {
        return side;
    }
}
