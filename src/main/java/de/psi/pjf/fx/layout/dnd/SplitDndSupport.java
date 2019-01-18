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

import javafx.event.Event;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.TransferMode;

import de.psi.pjf.fx.layout.container.ContainerUtils;

/**
 * Implementation of splitting with DnD
 *
 * @param <M>
 *     the domain model type
 */
public class SplitDndSupport< M extends Node > extends AbstractDndSupport
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
        cleanup();
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
        cleanup();
    }

    private Node findElement( String objectId )
    {
        if( objectId != null )
        {
            return ContainerUtils.findNodeWithId( widget.getScene().getRoot(), objectId );
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
        _handleDragOver( e );
    }

    /**
     * Handle the drag over
     *
     * @param e
     *     the event
     */
    public void handleDragOver( DragEvent e )
    {
        _handleDragOver( e );
    }

    /**
     * Handle the drop
     *
     * @param e
     *     the event
     */
    public void handleDragDropped( EFXDragEvent e )
    {
        _handleDragDropped( e );
    }

    /**
     * Handle the drop
     *
     * @param e
     *     the event
     */
    public void handleDragDropped( DragEvent e )
    {
        _handleDragDropped( e );
    }

    /**
     * Handling the drag over
     *
     * @param e
     *     the event
     */
    private void _handleDragOver( Event e )
    {

        String content = DndTabPaneFactory.getDnDContent( e );
        if( content == null )
        {
            return;
        }
//        System.out.println(content);
        M m = this.widget;

        Node findElement = findElement( content );

        if( findElement == null )
        {
            return;
        }

        final DropLocation splitType = getSplitType( e );
        if( m != null && this.constraintService != null && !this.constraintService
            .splitAllowed( m, findElement, splitType ) )
        {
            return;
        }

//        if (this.widget.getDropDroppedCallback() != null) {
            if (!DndTabPaneFactory.hasDnDContent(e)) {
                return;
            }

//            if (isSplit(e)) {
//                // Do not support spliting of none tab parts
//                // if( (MUIElement)m.getParent() instanceof MGenericTile<?> ) {
//                // e.consume();
//                // e.acceptTransferModes(TransferMode.MOVE);
//                // showSplitFeedback();
//                // }
//            }
//            else
//            {
                updateFeedback(new DndFeedbackService.DnDFeedbackData(null, null, splitType, m, null));
                setAcceptTransferModes(e, TransferMode.MOVE);
                e.consume();
//            }
//        }
    }

    /**
     * Handle the drag
     *
     * @param e
     *     the event
     */
    private void _handleDragDropped( Event e )
    {

        //		M m = this.widget.getDomElement();
        //
        //		String content = DndTabPaneFactory.getDnDContent(e);
        //
        //		if (content == null) {
        //			return;
        //		}
        //
        //		MUIElement findElement = findElement(content);
        //		if (findElement == null) {
        //			return;
        //		}
        //
        //		if (m != null && this.constraintService != null && !this.constraintService.splitAllowed(m, findElement, getSplitType(e))) {
        //			return;
        //		}
        //
        //
        //		WCallback< DropData,  Void> dropDroppedCallback = this.widget.getDropDroppedCallback();
        //		if (dropDroppedCallback != null) {
        //			if (!DndTabPaneFactory.hasDnDContent(e)) {
        //				return;
        //			}
        //
        //			String objectId = DndTabPaneFactory.getDnDContent(e);
        //
        //			MUIElement draggedElement = findElement(objectId);
        //			if (draggedElement == null) {
        //				return;
        //			}
        //
        //			if (m instanceof MGenericTile<?>) {
        //				// Tiles are not split
        //				e.consume();
        //			} else if (m instanceof MPart && isSplit(e)) {
        //				e.consume();
        //				if ((MUIElement) m.getParent() instanceof MPartStack) {
        //					DropData d = new DropData(screenX(e), screenY(e), this.widget.getDomElement(), draggedElement, getSplitType(e));
        //					dropDroppedCallback.call(d);
        //					setDropComplete(e, true);
        //				}
        //			} else if (m instanceof MElementContainer<?>) {
        //				MElementContainer<?> c = (MElementContainer<?>) m;
        //				if (this.modelService.countRenderableChildren(c) == 0) {
        //					@SuppressWarnings("all")
        //					DropData d = new DropData(screenX(e), screenY(e), this.widget.getDomElement(), draggedElement, BasicDropLocation.INSERT);
        //					dropDroppedCallback.call(d);
        //					e.consume();
        //					setDropComplete(e, true);
        //				}
        //			}
        //		}
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

    private SplitAreas calculateSplitAreas()
    {
        Bounds bounds = ( (Node)this.widget ).getBoundsInLocal();

        double hSplitWidth = ( bounds.getWidth() - SPLIT_PADDING * 2 ) / 5;
        double hSplitHeight = bounds.getHeight() - SPLIT_PADDING * 2;

        double vSplitWidth = bounds.getWidth() - SPLIT_PADDING * 2;
        double vSplitHeight = ( bounds.getHeight() - SPLIT_PADDING * 2 ) / 2;

        Bounds leftSplit = new BoundingBox( SPLIT_PADDING, SPLIT_PADDING, hSplitWidth, hSplitHeight );
        Bounds rightSplit =
            new BoundingBox( bounds.getWidth() - SPLIT_PADDING - hSplitWidth, SPLIT_PADDING, hSplitWidth,
                hSplitHeight );

        Bounds topSplit = new BoundingBox( SPLIT_PADDING, SPLIT_PADDING, vSplitWidth, vSplitHeight );
        Bounds bottomSplit =
            new BoundingBox( SPLIT_PADDING, SPLIT_PADDING + vSplitHeight, vSplitWidth, vSplitHeight );

        return new SplitAreas( leftSplit, rightSplit, topSplit, bottomSplit );
    }

    private static int SPLIT_PADDING = 20;

    private boolean isSplit( Event e )
    {
        Bounds boundsInLocal = ( (Node)this.widget ).getBoundsInLocal();
        boundsInLocal =
            new BoundingBox( boundsInLocal.getMinX() + SPLIT_PADDING, boundsInLocal.getMinY() + SPLIT_PADDING,
                boundsInLocal.getWidth() - SPLIT_PADDING * 2, boundsInLocal.getHeight() - SPLIT_PADDING * 2 );
        return boundsInLocal.contains( x( e ), y( e ) );
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
