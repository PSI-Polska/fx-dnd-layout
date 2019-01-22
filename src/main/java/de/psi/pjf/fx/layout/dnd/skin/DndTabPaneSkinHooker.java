/*******************************************************************************
 * Copyright (c) 2017 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package de.psi.pjf.fx.layout.dnd.skin;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import de.psi.pjf.fx.layout.container.ContainerUtils;
import de.psi.pjf.fx.layout.container.TabContainerWrapperIf;
import de.psi.pjf.fx.layout.dnd.DndTabPaneFactory;

/**
 * Create a hooker who use native DnD
 */
public class DndTabPaneSkinHooker implements DndTabPaneFactory.DragSetup
{
    /**
     * Custom data format for move data
     */
    public static final DataFormat TAB_MOVE = new DataFormat( "DnDTabPane:tabMove" ); //$NON-NLS-1$
    private static Tab DRAGGED_TAB;
    private final TabPane pane;
    private Predicate< TabContainerWrapperIf< ? > > startFunction;
    private Function< TabContainerWrapperIf< ? >, String > clipboardDataFunction;
    private Consumer< TabContainerWrapperIf< ? > > dragFinishedConsumer;
    private Consumer< DndTabPaneFactory.FeedbackData > feedbackConsumer;
    private Consumer< DndTabPaneFactory.DroppedData > dropConsumer;

    /**
     * Create a new hooker instance
     *
     * @param skin
     *     the skin
     */
    public DndTabPaneSkinHooker( Skin< TabPane > skin )
    {
        this.pane = skin.getSkinnable();
        Optional< Node > o_TabHeaderArea = ( (SkinBase< TabPane >)skin ).getChildren().stream()
            .filter( e -> e.getClass().getSimpleName().equals( "TabHeaderArea" ) ) //$NON-NLS-1$
            .findFirst();
        if( !o_TabHeaderArea.isPresent() || !( o_TabHeaderArea.get() instanceof Pane ) )
        {
            //            LoggerCreator.createLogger( DndTabPaneSkinHooker.class )
            //                .warning( "Could not find a supported TabHeaderArea pane. DnD is disabled." ); //$NON-NLS-1$
            return;
        }

        Pane tabHeaderArea = (Pane)o_TabHeaderArea.get();

        Optional< Node > o_HeadersRegion =
            tabHeaderArea.getChildren().stream().filter( e -> e.getStyleClass().contains( "headers-region" ) )
                .findFirst(); //$NON-NLS-1$

        if( !o_HeadersRegion.isPresent() || !( o_HeadersRegion.get() instanceof Pane ) )
        {
            //            LoggerCreator.createLogger( DndTabPaneSkinHooker.class )
            //                .warning( "Could not find a supported HeadersRegion pane. DnD is disabled." ); //$NON-NLS-1$
            return;
        }

        Pane headersRegion = (Pane)o_HeadersRegion.get();

        // Hook the nodes
        tabHeaderArea.setOnDragOver( ( e ) -> e.consume() );

        EventHandler< MouseEvent > handler = this::tabPane_handleDragStart;
        EventHandler< DragEvent > handlerFinished = this::tabPane_handleDragDone;

        for( Node tabHeaderSkin : headersRegion.getChildren() )
        {
            tabHeaderSkin.addEventHandler( MouseEvent.DRAG_DETECTED, handler );
            tabHeaderSkin.addEventHandler( DragEvent.DRAG_DONE, handlerFinished );
        }

        headersRegion.getChildren()
            .addListener( ( javafx.collections.ListChangeListener.Change< ? extends Node > change ) -> {
                while( change.next() )
                {
                    if( change.wasRemoved() )
                    {
                        change.getRemoved()
                            .forEach( ( e ) -> e.removeEventHandler( MouseEvent.DRAG_DETECTED, handler ) );
                        change.getRemoved()
                            .forEach( ( e ) -> e.removeEventHandler( DragEvent.DRAG_DONE, handlerFinished ) );
                    }
                    if( change.wasAdded() )
                    {
                        change.getAddedSubList()
                            .forEach( ( e ) -> e.addEventHandler( MouseEvent.DRAG_DETECTED, handler ) );
                        change.getAddedSubList()
                            .forEach( ( e ) -> e.addEventHandler( DragEvent.DRAG_DONE, handlerFinished ) );
                    }
                }
            } );

        tabHeaderArea.addEventHandler( DragEvent.DRAG_OVER,
            ( e ) -> tabPane_handleDragOver( tabHeaderArea, headersRegion, e ) );
        tabHeaderArea.addEventHandler( DragEvent.DRAG_DROPPED,
            ( e ) -> tabPane_handleDragDropped( tabHeaderArea, headersRegion, e ) );
        tabHeaderArea.addEventHandler( DragEvent.DRAG_EXITED, this::tabPane_handleDragDone );

    }

    @Override
    public void setClipboardDataFunction(
        Function< TabContainerWrapperIf< ? >, String > clipboardDataFunction )
    {
        this.clipboardDataFunction = clipboardDataFunction;
    }

    @Override
    public void setDragFinishedConsumer( Consumer< TabContainerWrapperIf< ? > > dragFinishedConsumer )
    {
        this.dragFinishedConsumer = dragFinishedConsumer;
    }

    @Override
    public void setDropConsumer( Consumer< DndTabPaneFactory.DroppedData > dropConsumer )
    {
        this.dropConsumer = dropConsumer;
    }

    @Override
    public void setFeedbackConsumer( Consumer< DndTabPaneFactory.FeedbackData > feedbackConsumer )
    {
        this.feedbackConsumer = feedbackConsumer;
    }

    @Override
    public void setStartFunction( Predicate< TabContainerWrapperIf< ? > > startFunction )
    {
        this.startFunction = startFunction;
    }

    private Tab getTab( Node n )
    {
        int tabIdx = n.getParent().getChildrenUnmodifiable().indexOf( n ); // The
        // order
        // in
        // the
        // parent
        // ==
        // order
        // in
        // pane.getTabs()
        return this.pane.getTabs().get( tabIdx );
    }

    @SuppressWarnings( "all" )
    void tabPane_handleDragDropped( Pane tabHeaderArea, Pane headersRegion, DragEvent event )
    {
        Tab draggedTab = DRAGGED_TAB;
        if( draggedTab == null )
        {
            return;
        }

        double x = event.getX() - headersRegion.getBoundsInParent().getMinX();

        Node referenceNode = null;
        DndTabPaneFactory.DropType type = DndTabPaneFactory.DropType.AFTER;
        for( Node n : headersRegion.getChildren() )
        {
            Bounds b = n.getBoundsInParent();
            if( b.getMaxX() > x )
            {
                if( b.getMinX() + b.getWidth() / 2 > x )
                {
                    referenceNode = n;
                    type = DndTabPaneFactory.DropType.BEFORE;
                }
                else
                {
                    referenceNode = n;
                    type = DndTabPaneFactory.DropType.AFTER;
                }
                break;
            }
        }

        if( referenceNode == null && headersRegion.getChildren().size() > 0 )
        {
            referenceNode = headersRegion.getChildren().get( headersRegion.getChildren().size() - 1 );
            type = DndTabPaneFactory.DropType.AFTER;
        }

        if( referenceNode != null )
        {
            try
            {
                Tab tab = getTab( referenceNode );

                boolean noMove = false;
                if( tab == null )
                {
                    event.setDropCompleted( false );
                    return;
                }
                else if( tab == draggedTab )
                {
                    noMove = true;
                }
                else if( type == DndTabPaneFactory.DropType.BEFORE )
                {
                    int idx = pane.getTabs().indexOf( tab );
                    if( idx > 0 )
                    {
                        if( pane.getTabs().get( idx - 1 ) == draggedTab )
                        {
                            noMove = true;
                        }
                    }
                }
                else
                {
                    int idx = pane.getTabs().indexOf( tab );

                    if( idx + 1 < pane.getTabs().size() )
                    {
                        if( pane.getTabs().get( idx + 1 ) == draggedTab )
                        {
                            noMove = true;
                        }
                    }
                }

                if( !noMove )
                {
                    efx_dropped( event.getScreenX(), event.getScreenY(),
                        ContainerUtils.< TabContainerWrapperIf< ? > >getContainer( draggedTab )
                            .orElseThrow(),
                        ContainerUtils.< TabContainerWrapperIf< ? > >getContainer( tab ).orElseThrow(),
                        type );
                    event.setDropCompleted( true );
                }
                else
                {
                    event.setDropCompleted( false );
                }
            }
            catch( Throwable e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            event.consume();
        }
    }

    void tabPane_handleDragStart( MouseEvent event )
    {
        try
        {
            Tab t = getTab( (Node)event.getSource() );

            if( t != null && ContainerUtils.< TabContainerWrapperIf< ? > >getContainer( t )
                .filter( this::efx_canStartDrag ).isPresent() )
            {
                DRAGGED_TAB = t;
                Node node = (Node)event.getSource();
                Dragboard db = node.startDragAndDrop( TransferMode.MOVE );

                WritableImage snapShot = node.snapshot( new SnapshotParameters(), null );
                PixelReader reader = snapShot.getPixelReader();
                int padX = 10;
                int padY = 10;
                int width = (int)snapShot.getWidth();
                int height = (int)snapShot.getHeight();
                WritableImage image = new WritableImage( width + padX, height + padY );
                PixelWriter writer = image.getPixelWriter();

                int h = 0;
                int v = 0;
                while( h < width + padX )
                {
                    v = 0;
                    while( v < height + padY )
                    {
                        if( h >= padX && h <= width + padX && v >= padY && v <= height + padY )
                        {
                            writer.setColor( h, v, reader.getColor( h - padX, v - padY ) );
                        }
                        else
                        {
                            writer.setColor( h, v, Color.TRANSPARENT );
                        }

                        v++;
                    }
                    h++;
                }

                db.setDragView( image, image.getWidth(), image.getHeight() * -1 );

                ClipboardContent content = new ClipboardContent();
                String data = efx_getClipboardContent(
                    ContainerUtils.< TabContainerWrapperIf< ? > >getContainer( t ).orElseThrow() );
                if( data != null )
                {
                    content.put( TAB_MOVE, data );
                }
                db.setContent( content );
            }
        }
        catch( Throwable t )
        {
            // // TODO Auto-generated catch block
            t.printStackTrace();
        }
    }

    @SuppressWarnings( "all" )
    void tabPane_handleDragOver( Pane tabHeaderArea, Pane headersRegion, DragEvent event )
    {
        Tab draggedTab = DRAGGED_TAB;
        final Optional< TabContainerWrapperIf< ? > > draggedTabContainer =
            ContainerUtils.< TabContainerWrapperIf< ? > >getContainer( draggedTab );
        if( draggedTab == null || draggedTabContainer.isEmpty() )
        {
            return;
        }

        // Consume the drag in any case
        event.consume();

        double x = event.getX() - headersRegion.getBoundsInParent().getMinX();

        Node referenceNode = null;
        DndTabPaneFactory.DropType type = DndTabPaneFactory.DropType.AFTER;
        for( Node n : headersRegion.getChildren() )
        {
            Bounds b = n.getBoundsInParent();
            if( b.getMaxX() > x )
            {
                if( b.getMinX() + b.getWidth() / 2 > x )
                {
                    referenceNode = n;
                    type = DndTabPaneFactory.DropType.BEFORE;
                }
                else
                {
                    referenceNode = n;
                    type = DndTabPaneFactory.DropType.AFTER;
                }
                break;
            }
        }

        if( referenceNode == null && headersRegion.getChildren().size() > 0 )
        {
            referenceNode = headersRegion.getChildren().get( headersRegion.getChildren().size() - 1 );
            type = DndTabPaneFactory.DropType.AFTER;
        }

        if( referenceNode != null )
        {
            try
            {
                Tab tab = getTab( referenceNode );
                final Optional< TabContainerWrapperIf< ? > > tabContainer =
                    ContainerUtils.< TabContainerWrapperIf< ? > >getContainer( tab );
                boolean noMove = false;
                if( tab == draggedTab || tabContainer.isEmpty() )
                {
                    noMove = true;
                }
                else if( type == DndTabPaneFactory.DropType.BEFORE )
                {
                    int idx = pane.getTabs().indexOf( tab );
                    if( idx > 0 )
                    {
                        if( pane.getTabs().get( idx - 1 ) == draggedTab )
                        {
                            noMove = true;
                        }
                    }
                }
                else
                {
                    int idx = pane.getTabs().indexOf( tab );

                    if( idx + 1 < pane.getTabs().size() )
                    {
                        if( pane.getTabs().get( idx + 1 ) == draggedTab )
                        {
                            noMove = true;
                        }
                    }
                }

                if( noMove )
                {
                    efx_dragFeedback( draggedTabContainer.orElseThrow(), null, null,
                        DndTabPaneFactory.DropType.NONE );
                    return;
                }

                Bounds b = referenceNode.getBoundsInLocal();
                b = referenceNode.localToScene( b );
                b = pane.sceneToLocal( b );

                efx_dragFeedback( draggedTabContainer.orElseThrow(), tabContainer.orElseThrow(), b, type );
            }
            catch( Throwable e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            event.acceptTransferModes( TransferMode.MOVE );
        }
        else
        {
            efx_dragFeedback( draggedTabContainer.orElseThrow(), null, null,
                DndTabPaneFactory.DropType.NONE );
        }
    }

    void tabPane_handleDragDone( DragEvent event )
    {
        Tab tab = DRAGGED_TAB;
        if( tab == null )
        {
            return;
        }

        efx_dragFinished( ContainerUtils.< TabContainerWrapperIf< ? > >getContainer( tab ).orElseThrow() );
    }

    private boolean efx_canStartDrag( TabContainerWrapperIf< ? > tab )
    {
        if( this.startFunction != null )
        {
            return this.startFunction.test( tab );
        }
        return true;
    }

    private void efx_dragFinished( TabContainerWrapperIf< ? > tab )
    {
        if( this.dragFinishedConsumer != null )
        {
            this.dragFinishedConsumer.accept( tab );
        }
    }

    private String efx_getClipboardContent( TabContainerWrapperIf< ? > t )
    {
        if( this.clipboardDataFunction != null )
        {
            return this.clipboardDataFunction.apply( t );
        }
        return System.identityHashCode( t ) + ""; //$NON-NLS-1$
    }

    private void efx_dragFeedback( TabContainerWrapperIf< ? > draggedTab,
        TabContainerWrapperIf< ? > targetTab, Bounds bounds, DndTabPaneFactory.DropType aDropType )
    {
        if( this.feedbackConsumer != null )
        {
            this.feedbackConsumer.accept( new DndTabPaneFactory.FeedbackData( draggedTab, targetTab, bounds, aDropType ) );
        }
    }

    private void efx_dropped( double x, double y, TabContainerWrapperIf< ? > draggedTab,
        TabContainerWrapperIf< ? > targetTab, DndTabPaneFactory.DropType aDropType )
    {
        if( this.dropConsumer != null )
        {
            this.dropConsumer.accept( new DndTabPaneFactory.DroppedData( x, y, draggedTab, targetTab, aDropType ) );
        }
    }
}
