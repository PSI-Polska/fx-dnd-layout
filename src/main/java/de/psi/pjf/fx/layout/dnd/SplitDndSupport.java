/*******************************************************************************
 * Copyright (c) 2014 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package de.psi.pjf.fx.layout.dnd;

import java.util.Optional;
import java.util.function.Consumer;

import javafx.event.Event;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.TransferMode;

import de.psi.pjf.fx.layout.container.ContainerIf;
import de.psi.pjf.fx.layout.container.ContainerUtils;
import de.psi.pjf.fx.layout.util.FxUtils;

import static de.psi.pjf.fx.layout.dnd.DefaultDndFeedback.LEFT_RIGHT_RATIO;

/**
 * Implementation of splitting with DnD
 *
 * @param <M>
 *     the domain model type
 */
public class SplitDndSupport< M extends DndCallbackProviderIf & ContainerIf< ? > > extends AbstractDndSupport
{

    private final DndService constraintService;
    private final M widget;

    /**
     * Create new instance
     *
     * @param efxModelService
     *     efx model service
     * @param modelService
     *     the model service
     * @param constraintService
     *     the constraint service
     * @param feedbackService
     *     the feedback service
     * @param widget
     *     the widget
     */
    public SplitDndSupport( DndService constraintService, DndFeedbackService feedbackService, M widget )
    {
        super( feedbackService );
        this.constraintService = constraintService;
        this.widget = widget;
    }

    /**
     * Handle exiting of the drag
     *
     * @param e
     *     the event
     */
    @SuppressWarnings( "static-method" )
    public void handleDragExit( DragEvent e )
    {
        try
        {
            cleanup();
        }
        catch( Throwable ex )
        {
            logError( "There was exception in handleDragExit", ex );
        }
    }

    /**
     * Handle exiting of the drag
     *
     * @param e
     *     the event
     */
    @SuppressWarnings( "static-method" )
    public void handleDragExit( MouseDragEvent e )
    {
        try
        {
            cleanup();
        }
        catch( Throwable ex )
        {
            logError( "There was exception in handleDragExit", ex );
        }
    }

    private Node findElement( String objectId )
    {
        if( objectId != null )
        {
            return FxUtils.findNodeWithId( widget.getNode().getScene().getRoot(), objectId );
        }
        return null;
    }

    /**
     * Handle the drag over
     *
     * @param e
     *     the event
     */
    public void handleDragOver( EFXDragEvent e )
    {
        try
        {
            _handleDragOver( e );
        }
        catch( Throwable ex )
        {
            logError( "There was exception in handleDragOver", ex );
        }
    }

    /**
     * Handle the drag over
     *
     * @param e
     *     the event
     */
    public void handleDragOver( DragEvent e )
    {
        try
        {
            _handleDragOver( e );
        }
        catch( Throwable ex )
        {
            logError( "There was exception in handleDragOver", ex );
        }
    }

    /**
     * Handle the drop
     *
     * @param e
     *     the event
     */
    public void handleDragDropped( EFXDragEvent e )
    {
        try
        {
            _handleDragDropped( e );
        }
        catch( Throwable ex )
        {
            logError( "There was exception in handleDragDropped", ex );
        }
    }

    /**
     * Handle the drop
     *
     * @param e
     *     the event
     */
    public void handleDragDropped( DragEvent e )
    {
        try
        {
            _handleDragDropped( e );
        }
        catch( Throwable ex )
        {
            logError( "There was exception in handleDragDropped", ex );
        }
    }

    /**
     * Handling the drag over
     *
     * @param e
     *     the event
     */
    private void _handleDragOver( Event e )
    {
        if( !DndTabPaneFactory.hasDnDContent( e ) )
        {
            cleanup();
            return;
        }
        String content = DndTabPaneFactory.getDnDContent( e );
        if( content == null )
        {
            cleanup();
            return;
        }
        M m = this.widget;

        Node findElement = findElement( content );

        if( findElement == null )
        {
            cleanup();
            return;
        }

        final Optional< ContainerIf< ? > > containerOpt = ContainerUtils.getContainer( findElement );
        if( containerOpt.isEmpty() )
        {
            cleanup();
            return;
        }
        final ContainerIf< ? > foundContainer = containerOpt.get();
        if( foundContainer.getParent() == this.widget && this.widget.getChildrenCount() == 1 )
        {
            // Cannot split current tab with 1 children
            cleanup();
            return;
        }

        final DropLocation splitType = getSplitType( e );
        if( m != null && this.constraintService != null && !this.constraintService
            .splitAllowed( m, foundContainer, splitType ) )
        {
            cleanup();
            return;
        }

        if( this.widget.getSplitDropCallback() != null )
        {
            final int childCount = this.widget.getChildrenCount();
            if( isNotInSplitBounds( e ) && childCount != 0 )
            {
                cleanup();
                return;
            }
            final DropLocation dropLocation = childCount == 0 ? BasicDropLocation.INSERT : splitType;
            updateFeedback( new DndFeedbackService.DnDFeedbackData( null, null, dropLocation, m,
                this.widget.getNode().getLayoutBounds() ) );
            setAcceptTransferModes( e, TransferMode.MOVE );
            e.consume();
        }
    }

    /**
     * Handle the drag
     *
     * @param e
     *     the event
     */
    private void _handleDragDropped( Event e )
    {
        M m = this.widget;

        if( !DndTabPaneFactory.hasDnDContent( e ) )
        {
            return;
        }
        String content = DndTabPaneFactory.getDnDContent( e );

        if( content == null )
        {
            return;
        }

        Node findElement = findElement( content );

        if( findElement == null )
        {
            return;
        }

        final Optional< ContainerIf< ? > > containerOpt = ContainerUtils.getContainer( findElement );
        if( containerOpt.isEmpty() )
        {
            return;
        }
        final ContainerIf< ? > foundContainer = containerOpt.get();
        if( foundContainer.getParent() == this.widget && this.widget.getChildrenCount() == 1 )
        {
            // Cannot split current tab with 1 children
            return;
        }

        final DropLocation splitType = getSplitType( e );
        if( m != null && this.constraintService != null && !this.constraintService
            .splitAllowed( m, foundContainer, splitType ) )
        {
            return;
        }
        final Consumer< DropData > dropDroppedCallback = this.widget.getSplitDropCallback();
        if( dropDroppedCallback != null )
        {
            final int childCount = this.widget.getChildrenCount();
            if( isNotInSplitBounds( e ) && childCount != 0 )
            {
                return;
            }
            final DropLocation dropLocation = childCount == 0 ? BasicDropLocation.INSERT : splitType;
            final DropData d =
                new DropData( screenX( e ), screenY( e ), this.widget, foundContainer, dropLocation );
            dropDroppedCallback.accept( d );
            e.consume();
            setDropComplete( e, true );
        }
    }

    @SuppressWarnings( "all" )
    private DropLocation getSplitType( Event e )
    {
        SplitAreas areas = calculateSplitAreas();

        if( areas.left.contains( x( e ), y( e ) ) )
        {
            return BasicDropLocation.SPLIT_LEFT;
        }
        else if( areas.right.contains( x( e ), y( e ) ) )
        {
            return BasicDropLocation.SPLIT_RIGHT;
        }
        else if( areas.top.contains( x( e ), y( e ) ) )
        {
            return BasicDropLocation.SPLIT_TOP;
        }
        else if( areas.bottom.contains( x( e ), y( e ) ) )
        {
            return BasicDropLocation.SPLIT_BOTTOM;
        }

        return BasicDropLocation.SPLIT_BOTTOM;
    }

    private SplitAreas calculateSplitAreas()
    {
        Bounds bounds = this.widget.getNode()
            .getBoundsInLocal();

        Bounds leftSplit = new BoundingBox( bounds.getMinX(), bounds.getMinY(),
            bounds.getWidth() * LEFT_RIGHT_RATIO, bounds.getHeight() );

        Bounds rightSplit = new BoundingBox( bounds.getMaxX() - bounds.getWidth() * LEFT_RIGHT_RATIO,
            bounds.getMinY(), bounds.getWidth() * LEFT_RIGHT_RATIO, bounds.getHeight() );

        Bounds topSplit =
            new BoundingBox( bounds.getMinX() + bounds.getWidth() * LEFT_RIGHT_RATIO, bounds.getMinY(),
                bounds.getWidth() - 2 * LEFT_RIGHT_RATIO * bounds.getWidth(), bounds.getHeight() / 2 );

        Bounds bottomSplit = new BoundingBox( bounds.getMinX() + bounds.getWidth() * LEFT_RIGHT_RATIO * 2,
            bounds.getMaxY() - bounds.getWidth() * LEFT_RIGHT_RATIO,
            bounds.getWidth() - bounds.getWidth() * LEFT_RIGHT_RATIO * 2, bounds.getHeight() / 2 );

        return new SplitAreas( leftSplit, rightSplit, topSplit, bottomSplit );
    }

    private boolean isNotInSplitBounds( Event e )
    {
        Bounds boundsInLocal = this.widget.getNode()
            .getBoundsInLocal();
        return !boundsInLocal.contains( x( e ), y( e ) );
    }

    public void install()
    {
        widget.getNode().addEventHandler( DragEvent.DRAG_OVER, this::handleDragOver );
        widget.getNode().addEventHandler( DragEvent.DRAG_EXITED, this::handleDragExit );
        widget.getNode().addEventHandler( DragEvent.DRAG_DROPPED, this::handleDragDropped );

        widget.getNode().addEventHandler( EFXDragEvent.DRAG_OVER, this::handleDragOver );
        //		widget.addEventHandler(EFXDragEvent.DRAG_EXITED, dndSupport::handleDragExit);
        widget.getNode().addEventHandler( EFXDragEvent.DRAG_DROPPED, this::handleDragDropped );
        if( StackDndSupport.DETACHABLE_DRAG )
        {
            widget.getNode().addEventHandler( MouseDragEvent.MOUSE_DRAG_EXITED, this::handleDragExit );
        }
    }

    private static double x( Event e )
    {
        if( e instanceof DragEvent )
        {
            return ( (DragEvent)e ).getX();
        }
        return ( (EFXDragEvent)e ).getX();
    }

    private static double y( Event e )
    {
        if( e instanceof DragEvent )
        {
            return ( (DragEvent)e ).getY();
        }
        return ( (EFXDragEvent)e ).getY();
    }

    private static double screenX( Event e )
    {
        if( e instanceof DragEvent )
        {
            return ( (DragEvent)e ).getScreenX();
        }
        return ( (EFXDragEvent)e ).getScreenX();
    }

    private static double screenY( Event e )
    {
        if( e instanceof DragEvent )
        {
            return ( (DragEvent)e ).getScreenY();
        }
        return ( (EFXDragEvent)e ).getScreenY();
    }

    private static void setDropComplete( Event e, boolean complete )
    {
        if( e instanceof EFXDragEvent )
        {
            ( (EFXDragEvent)e ).setComplete( complete );
        }
        else
        {
            ( (DragEvent)e ).setDropCompleted( complete );
        }
    }

    private static void setAcceptTransferModes( Event e, TransferMode mode )
    {
        if( e instanceof DragEvent )
        {
            ( (DragEvent)e ).acceptTransferModes( mode );
        }
    }

    private static class SplitAreas
    {
        final Bounds left;
        final Bounds right;
        final Bounds top;
        final Bounds bottom;

        SplitAreas( Bounds left, Bounds right, Bounds top, Bounds bottom )
        {
            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
        }
    }
}
