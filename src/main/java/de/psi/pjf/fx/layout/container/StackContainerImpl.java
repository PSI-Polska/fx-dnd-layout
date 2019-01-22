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
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import de.psi.pjf.fx.layout.util.FxUtils;
import de.psi.pjf.fx.layout.util.SelectedTabFocuser;

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
    private boolean localChange = false;

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
        final List< Tab > tabsToBeSet = getChildrenInternal().stream().map( TabContainerWrapperIf::getTab )
            .collect( Collectors.toList() );
        tabPane.getTabs().setAll( tabsToBeSet );
        tabPane.getTabs().addListener( (ListChangeListener< ? super Tab >)aChange -> {
            if( localChange )
            {
                return;
            }
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
        if( selectedTab != null )
        {
            tabPane.getSelectionModel().select( selectedTab.getTab() );
            FxUtils.executeOnceWhenPropertyIsNonNull( selectedTab.getNode().sceneProperty(),
                $ -> selectedTab.getNode().requestFocus() );
        }
        else
        {
            selectedTab = Optional.ofNullable( tabPane.getSelectionModel().getSelectedItem() )
                .flatMap( ContainerUtils::< TabContainerWrapperIf< ? > >getContainer ).orElse( null );
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
        final SelectedTabFocuser selectedTabFocuser =
            new SelectedTabFocuser( () -> selectedTab == null ? null : selectedTab.getNode() );
        selectedTabFocuser.install( tabPane );
        return tabPane;
    }

    @JsonIgnore
    @Override
    public int getTabCount()
    {
        return getChildrenInternal().size();
    }

    @Override
    public void select( final TabContainerWrapperIf< ? > aTab )
    {
        if( isNodeCreated() )
        {
            getNode().getSelectionModel().select( aTab.getTab() );
            FxUtils.executeOnceWhenPropertyIsNonNull( selectedTab.getNode().sceneProperty(),
                $ -> selectedTab.getNode().requestFocus() );
        }
        else
        {
            selectedTab = aTab;
        }
    }

    @Override
    public TabContainerWrapperIf< ? > getSelectedTab()
    {
        return selectedTab;
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

    @Override
    protected void addChildFx( final ContainerIf< ? > child )
    {
        if( !( child instanceof TabContainerWrapperIf ) )
        {
            throw new IllegalStateException();
        }
        try
        {
            localChange = true;
            getNode().getTabs().add( ( (TabContainerWrapperIf< ? >)child ).getTab() );
        }
        finally
        {
            localChange = false;
        }
    }

    @Override
    protected void addChildFx( final int index, final ContainerIf< ? > child )
    {
        if( !( child instanceof TabContainerWrapperIf ) )
        {
            throw new IllegalStateException();
        }
        try
        {
            localChange = true;
            getNode().getTabs().add( index, ( (TabContainerWrapperIf< ? >)child ).getTab() );
        }
        finally
        {
            localChange = false;
        }
    }

    @Override
    protected void removeChildFx( final ContainerIf< ? > child )
    {
        if( !( child instanceof TabContainerWrapperIf ) )
        {
            throw new IllegalStateException();
        }
        try
        {
            localChange = true;
            getNode().getTabs().remove( ( (TabContainerWrapperIf< ? >)child ).getTab() );
        }
        finally
        {
            localChange = false;
        }
    }

    @Override
    public Side getSide()
    {
        return side;
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
}
