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

import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

import de.psi.pjf.fx.layout.dnd.markers.AreaOverlay;
import de.psi.pjf.fx.layout.dnd.markers.AreaOverlay.Area;
import de.psi.pjf.fx.layout.dnd.markers.PositionMarker;
import de.psi.pjf.fx.layout.dnd.markers.TabOutlineMarker;
import de.psi.pjf.fx.layout.util.FxUtils;

/**
 * Feedback for DnD
 */
public class DefaultDndFeedback implements DndFeedbackService
{

    @Override
    public MarkerFeedback showFeedback( DnDFeedbackData data )
    {
        final Node widget = data.feedbackContainerElement.getNode();
        if( data.dropType.isSplit() )
        {
            final Pane pane = (Pane)FxUtils.findAscendantInclusively( widget, Pane.class::isInstance );
            return handleSplit( pane, data );
        }
        else if( data.dropType.isReorder() && widget instanceof TabPane )
        {
            final Node sourceElement = data.sourceElement == null ? null : data.sourceElement.getNode();
            if( FxUtils.findAscendantInclusively( sourceElement, node -> node == widget ) != null )
            {
                return handleReorder( (TabPane)widget, data );
            }
            else
            {
                return handleMove( (TabPane)widget, data );
            }
        }
        return null;
    }

    private static MarkerFeedback handleSplit( Pane layoutNode, DnDFeedbackData data )
    {
        Optional< Node > first =
            layoutNode.getChildrenUnmodifiable().stream().filter( n -> n instanceof AreaOverlay ).findFirst();
        AreaOverlay overlay;
        if( first.isPresent() )
        {
            overlay = (AreaOverlay)first.get();
            overlay.toFront();
        }
        else
        {
            overlay = new AreaOverlay( 0.2 );
            overlay.setManaged( false );
            overlay.setMouseTransparent( true );
            layoutNode.getChildren().add( overlay );
            layoutNode.widthProperty().addListener(
                ( o ) -> overlay.resizeRelocate( 0, 0, layoutNode.getWidth(), layoutNode.getHeight() ) );
            layoutNode.heightProperty().addListener(
                ( o ) -> overlay.resizeRelocate( 0, 0, layoutNode.getWidth(), layoutNode.getHeight() ) );
            overlay.resizeRelocate( 0, 0, layoutNode.getWidth(), layoutNode.getHeight() );
        }

        overlay.setVisible( true );
        overlay.updateActiveArea( toArea( (BasicDropLocation)data.dropType ) );

        return new MarkerFeedback( data )
        {

            @Override
            public void hide()
            {
                overlay.setVisible( false );
            }
        };
    }

    private static Area toArea( BasicDropLocation type )
    {
        switch( type )
        {
            case SPLIT_BOTTOM:
                return Area.BOTTOM;
            case SPLIT_LEFT:
                return Area.LEFT;
            case SPLIT_RIGHT:
                return Area.RIGHT;
            case SPLIT_TOP:
                return Area.TOP;
            default:
                break;
        }
        return Area.NONE;
    }

    private static MarkerFeedback handleReorder( TabPane aTabPane, DnDFeedbackData data )
    {
        final ObservableList< Node > modifiableChildrenList =
            ( (SkinBase< ? >)aTabPane.getSkin() ).getChildren();
        PositionMarker marker = null;
        for( Node n : modifiableChildrenList )
        {
            if( n instanceof PositionMarker )
            {
                marker = (PositionMarker)n;
            }
        }

        if( marker == null )
        {
            marker = new PositionMarker();
            marker.setManaged( false );
            modifiableChildrenList.add( marker );
        }
        else
        {
            modifiableChildrenList.remove( marker );
            modifiableChildrenList.add( marker );
            marker.setVisible( true );
        }

        double w = marker.getBoundsInLocal().getWidth();
        double h = marker.getBoundsInLocal().getHeight();

        final Side side = aTabPane.getSide();
        double ratio =
            ( side.isHorizontal() ? data.containerRegion.getHeight() : data.containerRegion.getWidth() ) / h;
        ratio += 0.1;
        marker.setScaleX( ratio );
        marker.setScaleY( ratio );

        final double newMarkerWidth = w * ratio;
        final double newMarkerHeight = h * ratio;
        double wDiff = w / 2;
        //        double wDiff = ( w - newMarkerWidth ) / 2;
        double hDiff = ( h - newMarkerHeight ) / 2;

        final double x, y;
        if( side.isHorizontal() )
        {
            double _x = data.containerRegion.getMinX();
            double _y = data.containerRegion.getMinY();
            if( side == Side.TOP )
            {
                _x -= wDiff;
                _y -= hDiff;
                if( data.dropType == BasicDropLocation.AFTER )
                {
                    _x += data.containerRegion.getWidth();
                }
            }
            else
            {
                _y += hDiff;
                if( data.dropType == BasicDropLocation.BEFORE )
                {
                    _x += data.containerRegion.getWidth();
                    _x -= wDiff;
                }
            }
            y = _y;
            x = _x;
        }
        else
        {
            double _x = data.containerRegion.getMinX();
            double _y = data.containerRegion.getMinY() - hDiff;
            if( side == Side.RIGHT )
            {
                _x += newMarkerWidth;
                if( data.dropType == BasicDropLocation.AFTER )
                {
                    _y += data.containerRegion.getHeight();
                }
            }
            else
            {
                if( data.dropType == BasicDropLocation.BEFORE )
                {
                    _y += data.containerRegion.getHeight();
                }
            }
            y = _y;
            x = _x;
        }
        marker.relocate( x, y );
        marker.getTransforms().clear();
        marker.getTransforms().add( new Rotate( getRotation( side ), 0, 0 ) );
        final PositionMarker fmarker = marker;

        return new MarkerFeedback( data )
        {

            @Override
            public void hide()
            {
                fmarker.setVisible( false );
            }
        };
    }

    private static int getRotation( Side pos )
    {
        switch( pos )
        {
            case TOP:
                return 0;
            case BOTTOM:
                return 180;
            case LEFT:
                return -90;
            case RIGHT:
                return 90;
            default:
                return 0;
        }
    }

    @SuppressWarnings( "null" )
    private static MarkerFeedback handleMove( TabPane aTabPane, DnDFeedbackData data )
    {
        final ObservableList< Node > modifiableChildrenList =
            ( (SkinBase< ? >)aTabPane.getSkin() ).getChildren();
        TabOutlineMarker marker = null;

        for( Node n : modifiableChildrenList )
        {
            if( n instanceof TabOutlineMarker )
            {
                marker = (TabOutlineMarker)n;
            }
        }

        if( marker == null )
        {
            marker = new TabOutlineMarker( aTabPane.getBoundsInLocal(), data.containerRegion,
                data.dropType == BasicDropLocation.BEFORE, aTabPane.getSide() );
            marker.setManaged( false );
            marker.setMouseTransparent( true );
            modifiableChildrenList.add( marker );
        }
        else
        {
            modifiableChildrenList.remove( marker );
            modifiableChildrenList.add( marker );
            marker.updateBounds( aTabPane.getBoundsInLocal(), data.containerRegion,
                data.dropType == BasicDropLocation.BEFORE, aTabPane.getSide() );
            marker.setVisible( true );
        }

        final TabOutlineMarker fmarker = marker;

        return new MarkerFeedback( data )
        {

            @Override
            public void hide()
            {
                fmarker.setVisible( false );
            }
        };
    }
}
