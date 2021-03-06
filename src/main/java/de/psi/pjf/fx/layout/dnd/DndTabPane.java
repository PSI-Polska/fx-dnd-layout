/*******************************************************************************
 * Copyright (c) 2014 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Adrodoc55<adrodoc55@googlemail.com> - initial API and implementation
 *******************************************************************************/
package de.psi.pjf.fx.layout.dnd;

import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TabPane;

import de.psi.pjf.fx.layout.dnd.skin.DnDTabPaneSkinHookerFullDrag;
import de.psi.pjf.fx.layout.dnd.skin.DndTabPaneSkinHooker;

/**
 * A TabPane with Drag and Drop Support
 *
 * @since 3.0.0
 */
public class DndTabPane extends TabPane
{
    public static final String CSS_FILE_NAME = "css/fx-dnd.css";
    private static final DndTabPaneFactory.FeedbackType DEFAULT_FEEDBACK_TYPE =
        DndTabPaneFactory.FeedbackType.MARKER;
    private BooleanProperty allowDetach;
    private ObjectProperty< DndTabPaneFactory.FeedbackType > feedbackType;

    private DndTabPaneFactory.DragSetup setup;

    /**
     * Create a tab pane with a default setup for Drag and Drop Feedback
     */
    public DndTabPane()
    {
        this( DEFAULT_FEEDBACK_TYPE, false );
    }

    /**
     * Create a tab pane with a default setup for Drag and Drop Feedback
     *
     * @param feedbackType
     *     the feedback type
     * @param allowDetach
     *     allow detaching
     */
    public DndTabPane( DndTabPaneFactory.FeedbackType feedbackType, boolean allowDetach )
    {
        setFeedbackType( feedbackType );
        setAllowDetach( allowDetach );
        initListeners( setup -> DndTabPaneFactory
            .setup( feedbackTypeProperty()::get, this, getChildren(), setup, null ) );
    }

    /**
     * Create a tab pane and set the drag strategy
     *
     * @param allowDetach
     *     allow detaching
     * @param setup
     *     the setup instance for the pane
     */
    public DndTabPane( boolean allowDetach, Consumer< DndTabPaneFactory.DragSetup > setup )
    {
        this( DEFAULT_FEEDBACK_TYPE, allowDetach, setup );
    }

    /**
     * Create a tab pane and set the drag strategy
     *
     * @param feedbackType
     *     the feedback type
     * @param allowDetach
     *     allow detaching
     * @param setup
     *     the setup instance for the pane
     */
    public DndTabPane( DndTabPaneFactory.FeedbackType feedbackType, boolean allowDetach,
        Consumer< DndTabPaneFactory.DragSetup > setup )
    {
        setFeedbackType( feedbackType );
        setAllowDetach( allowDetach );
        initListeners( setup );
    }

    private void initListeners( Consumer< DndTabPaneFactory.DragSetup > setup )
    {
        skinProperty().addListener( ( observable, oldValue, newValue ) -> {
            if( oldValue != null && this.setup != null )
            {
                DndTabPaneFactory.teardown( this.setup );
            }
            if( newValue != null )
            {
                setupDnd( newValue, setup );
            }
        } );
        allowDetachProperty().addListener( ( observable, oldValue, newValue ) -> {
            if( newValue != null )
            {
                Skin< ? > skin = getSkin();
                if( skin != null )
                {
                    if( this.setup != null )
                    {
                        DndTabPaneFactory.teardown( this.setup );
                    }
                    setupDnd( skin, setup );
                }
            }
        } );
    }

    private void setupDnd( Skin< ? > skin, Consumer< DndTabPaneFactory.DragSetup > setup )
    {
        if( isAllowDetach() )
        {
            this.setup = new DnDTabPaneSkinHookerFullDrag( (SkinBase< TabPane >)skin );
        }
        else
        {
            this.setup = new DndTabPaneSkinHooker( (Skin< TabPane >)skin );
        }
        setup.accept( this.setup );
    }

    /**
     * @return a property indicating whether Tabs can be detached or not
     */
    public BooleanProperty allowDetachProperty()
    {
        if( this.allowDetach == null )
        {
            this.allowDetach = new SimpleBooleanProperty( this, "allowDetach" ); //$NON-NLS-1$
        }
        return this.allowDetach;
    }

    /**
     * @return whether Tabs can be detached or not
     */
    public boolean isAllowDetach()
    {
        return this.allowDetach != null && this.allowDetach.get();
    }

    /**
     * @param allowDetach
     *     whether Tabs can be detached or not
     */
    public void setAllowDetach( boolean allowDetach )
    {
        allowDetachProperty().set( allowDetach );
    }

    /**
     * @return the type of visual Feedback for the User
     */
    public ObjectProperty< DndTabPaneFactory.FeedbackType > feedbackTypeProperty()
    {
        if( this.feedbackType == null )
        {
            this.feedbackType =
                new SimpleObjectProperty<>( this, "feedbackType", DEFAULT_FEEDBACK_TYPE ); //$NON-NLS-1$
        }
        return this.feedbackType;
    }

    /**
     * @return the type of visual Feedback for the User
     */
    public DndTabPaneFactory.FeedbackType getFeedbackType()
    {
        return this.feedbackType != null ? this.feedbackType.get() : DEFAULT_FEEDBACK_TYPE;
    }

    /**
     * @param feedbackType
     *     the type of visual Feedback for the User
     */
    public void setFeedbackType( DndTabPaneFactory.FeedbackType feedbackType )
    {
        feedbackTypeProperty().set( feedbackType );
    }
}
