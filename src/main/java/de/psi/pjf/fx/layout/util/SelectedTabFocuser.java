// ******************************************************************
//
// SelectedTabFocuser.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.util;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

/**
 * Workaround for forcing focusing content node on changing tabs, but enabling to traverse tabs by keyboard.
 *
 * @author created: pkruszczynski on 22.01.2019 16:30
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class SelectedTabFocuser implements ChangeListener< Boolean >, EventHandler< MouseEvent >
{

    private final Supplier< Node > nodeToFocusProvider;
    private boolean focusContentNode = false;
    private final InvalidationListener tabStructureChangeListener = this::tabStructureChanged;
    private final ChangeListener< ? super Tab > selectedTabChangeListener = this::selectedTabChanged;

    public SelectedTabFocuser( final Supplier< Node > aNodeToFocusProvider )
    {
        nodeToFocusProvider = Objects.requireNonNull( aNodeToFocusProvider );
    }

    private boolean wasFocusedFromMouse()
    {
        if( focusContentNode )
        {
            focusContentNode = false;
            return true;
        }
        return false;
    }

    private void selectedTabChanged( final ObservableValue< ? extends Tab > aObservableValue,
        final Tab oldValue, final Tab newValue )
    {
        if( !wasFocusedFromMouse() )
        {
            return;
        }
        if( newValue != null )
        {
            Platform.runLater( () -> {
                final Node nodeToFocus = nodeToFocusProvider.get();
                FxUtils.executeOnceWhenPropertyIsNonNull( nodeToFocus.sceneProperty(),
                    $ -> nodeToFocus.requestFocus() );
            } );
        }
    }

    @Override
    public void changed( final ObservableValue< ? extends Boolean > aObservableValue, final Boolean aBoolean,
        final Boolean aT1 )
    {
        if( !wasFocusedFromMouse() )
        {
            return;
        }
        final Node nodeToFocus = nodeToFocusProvider.get();
        if( nodeToFocus != null && Boolean.TRUE.equals( aT1 ) )
        {
            FxUtils.executeOnceWhenPropertyIsNonNull( nodeToFocus.sceneProperty(),
                $ -> nodeToFocus.requestFocus() );
        }
    }

    @Override
    public void handle( final MouseEvent aEvent )
    {
        focusContentNode = true;
    }

    private void tabStructureChanged( final Observable aObservable )
    {
        focusContentNode = true;
    }

    public void install( final TabPane aTabPane )
    {
        aTabPane.focusedProperty().addListener( this );
        aTabPane.addEventFilter( MouseEvent.MOUSE_PRESSED, this );
        aTabPane.getSelectionModel().selectedItemProperty().addListener( selectedTabChangeListener );
        aTabPane.getTabs().addListener( tabStructureChangeListener );
    }

    public void uninstall( final TabPane aTabPane )
    {
        aTabPane.focusedProperty().removeListener( this );
        aTabPane.removeEventFilter( MouseEvent.MOUSE_PRESSED, this );
        aTabPane.getSelectionModel().selectedItemProperty().removeListener( selectedTabChangeListener );
        aTabPane.getTabs().removeListener( tabStructureChangeListener );
    }
}