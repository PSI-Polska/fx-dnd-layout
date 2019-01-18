/*******************************************************************************
 * Copyright (c) 2014 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package de.psi.pjf.fx.layout.dnd.markers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeType;

/**
 * Marks a Tab-Position
 */
public final class TabOutlineMarker extends Group
{

    private boolean before;

    /**
     * Create a new tab outline
     *
     * @param containerBounds
     *     the bounds of the container
     * @param referenceBounds
     *     the bounds of the reference tab
     * @param before
     *     <code>true</code> to mark the insert point before reference
     *     bounds
     */
    public TabOutlineMarker( Bounds containerBounds, Bounds referenceBounds, boolean before, Side aSide )
    {
        updateBounds( containerBounds, referenceBounds, before, aSide );
        getStyleClass().add( "tab-outline-marker" ); //$NON-NLS-1$
    }

    /**
     * Update the tab outline
     *
     * @param containerBounds
     *     the bounds of the container
     * @param referenceBounds
     *     the bounds of the reference tab
     * @param before
     *     <code>true</code> to mark the insert point before reference
     * @param aSide
     */
    public void updateBounds( Bounds containerBounds, Bounds referenceBounds, boolean before,
        final Side aSide )
    {
        this.before = before;

        final Polyline pl;
        if( aSide == Side.TOP )
        {
            pl = getTopSidePolyline( containerBounds, referenceBounds, before );
        }
        else if( aSide == Side.RIGHT )
        {
            pl = getRightSidePolyline( containerBounds, referenceBounds, before );
        }
        else if( aSide == Side.LEFT )
        {
            pl = getLeftSidePolyline( containerBounds, referenceBounds, before );
        }
        else if( aSide == Side.BOTTOM )
        {
            pl = getBottomSidePolyline( containerBounds, referenceBounds, before );
        }
        else
        {
            throw new IllegalArgumentException( "Side is not properly defined." );
        }
        pl.strokeProperty().bind( fillProperty() );
        pl.setStrokeWidth( 3 );
        pl.setStrokeType( StrokeType.INSIDE );
        getChildren().setAll( pl );
    }

    private static Polyline getTopSidePolyline( final Bounds aContainerBounds, Bounds aReferenceBounds,
        final boolean before )
    {
        final Polyline pl = new Polyline();
        if( before )
        {
            aReferenceBounds =
                new BoundingBox( Math.max( 0, aReferenceBounds.getMinX() - aReferenceBounds.getWidth() / 2 ),
                    aReferenceBounds.getMinY(), aReferenceBounds.getWidth(), aReferenceBounds.getHeight() );
        }
        else
        {
            aReferenceBounds =
                new BoundingBox( Math.max( 0, aReferenceBounds.getMaxX() - aReferenceBounds.getWidth() / 2 ),
                    aReferenceBounds.getMinY(), aReferenceBounds.getWidth(), aReferenceBounds.getHeight() );
        }

        // @formatter:off
        pl.getPoints().addAll(
            aContainerBounds.getMinX(), aReferenceBounds.getMaxY(),
            aReferenceBounds.getMinX(), aReferenceBounds.getMaxY(),
            aReferenceBounds.getMinX(), aReferenceBounds.getMinY(),
            aReferenceBounds.getMaxX(), aReferenceBounds.getMinY(),
            aReferenceBounds.getMaxX(), aReferenceBounds.getMaxY(),
            aContainerBounds.getMaxX(), aReferenceBounds.getMaxY(),
            aContainerBounds.getMaxX(), aContainerBounds.getMaxY(),
            aContainerBounds.getMinX(), aContainerBounds.getMaxY(),
            aContainerBounds.getMinX(), aReferenceBounds.getMaxY() );
        // @formatter:on
        return pl;
    }

    private static Polyline getRightSidePolyline( final Bounds aContainerBounds, Bounds aReferenceBounds,
        final boolean before )
    {
        final Polyline pl = new Polyline();
        if( before )
        {
            aReferenceBounds = new BoundingBox( aReferenceBounds.getMinX(),
                Math.max( 0, aReferenceBounds.getMinY() - aReferenceBounds.getHeight() / 2 ),
                aReferenceBounds.getWidth(), aReferenceBounds.getHeight() );
        }
        else
        {
            aReferenceBounds = new BoundingBox( aReferenceBounds.getMinX(),
                Math.max( 0, aReferenceBounds.getMaxY() - aReferenceBounds.getHeight() / 2 ),
                aReferenceBounds.getWidth(), aReferenceBounds.getHeight() );
        }
        // @formatter:off
        pl.getPoints().addAll(
            aContainerBounds.getMinX(), aContainerBounds.getMinY(),
            aReferenceBounds.getMinX(), aContainerBounds.getMinY(),
            aReferenceBounds.getMinX(), aReferenceBounds.getMinY(),
            aReferenceBounds.getMaxX(), aReferenceBounds.getMinY(),
            aReferenceBounds.getMaxX(), aReferenceBounds.getMaxY(),
            aReferenceBounds.getMinX(), aReferenceBounds.getMaxY(),
            aReferenceBounds.getMinX(), aContainerBounds.getMaxY(),
            aContainerBounds.getMinX(), aContainerBounds.getMaxY(),
            aContainerBounds.getMinX(), aContainerBounds.getMinY() );
        // @formatter:on
        return pl;
    }

    private static Polyline getLeftSidePolyline( final Bounds aContainerBounds, Bounds aReferenceBounds,
        final boolean before )
    {
        final Polyline pl = new Polyline();
        if( before )
        {
            aReferenceBounds = new BoundingBox( aReferenceBounds.getMinX(),
                Math.max( 0, aReferenceBounds.getMaxY() - aReferenceBounds.getHeight() / 2 ),
                aReferenceBounds.getWidth(), aReferenceBounds.getHeight() );
        }
        else
        {
            aReferenceBounds = new BoundingBox( aReferenceBounds.getMinX(),
                Math.max( 0, aReferenceBounds.getMinY() - aReferenceBounds.getHeight() / 2 ),
                aReferenceBounds.getWidth(), aReferenceBounds.getHeight() );
        }
        // @formatter:off
        pl.getPoints().addAll(
            aReferenceBounds.getMaxX(), aContainerBounds.getMinY(),
            aContainerBounds.getMaxX(), aContainerBounds.getMinY(),
            aContainerBounds.getMaxX(), aContainerBounds.getMaxY(),
            aReferenceBounds.getMaxX(), aContainerBounds.getMaxY(),
            aReferenceBounds.getMaxX(), aReferenceBounds.getMaxY(),
            aReferenceBounds.getMinX(), aReferenceBounds.getMaxY(),
            aReferenceBounds.getMinX(), aReferenceBounds.getMinY(),
            aReferenceBounds.getMaxX(), aReferenceBounds.getMinY(),
            aReferenceBounds.getMaxX(), aContainerBounds.getMinY() );
        // @formatter:on
        return pl;
    }

    private static Polyline getBottomSidePolyline( final Bounds aContainerBounds, Bounds aReferenceBounds,
        final boolean before )
    {
        final Polyline pl = new Polyline();

        if( before )
        {
            aReferenceBounds =
                new BoundingBox( Math.max( 0, aReferenceBounds.getMaxX() - aReferenceBounds.getWidth() / 2 ),
                    aReferenceBounds.getMinY(), aReferenceBounds.getWidth(), aReferenceBounds.getHeight() );
        }
        else
        {
            aReferenceBounds =
                new BoundingBox( Math.max( 0, aReferenceBounds.getMinX() - aReferenceBounds.getWidth() / 2 ),
                    aReferenceBounds.getMinY(), aReferenceBounds.getWidth(), aReferenceBounds.getHeight() );
        }
        // @formatter:off
        pl.getPoints().addAll(
            aContainerBounds.getMinX(), aContainerBounds.getMinY(),
            aContainerBounds.getMaxX(), aContainerBounds.getMinY(),
            aContainerBounds.getMaxX(), aReferenceBounds.getMinY(),
            aReferenceBounds.getMaxX(), aReferenceBounds.getMinY(),
            aReferenceBounds.getMaxX(), aReferenceBounds.getMaxY(),
            aReferenceBounds.getMinX(), aReferenceBounds.getMaxY(),
            aReferenceBounds.getMinX(), aReferenceBounds.getMinY(),
            aContainerBounds.getMinX(), aReferenceBounds.getMinY(),
            aContainerBounds.getMinX(), aContainerBounds.getMinY() );
        // @formatter:on
        return pl;
    }

    @SuppressWarnings( "null" )
    private final ObjectProperty< Paint > fill =
        new SimpleStyleableObjectProperty<>( FILL, this, "fill", Color.ORANGE ); //$NON-NLS-1$

    /**
     * The fill property
     *
     * <p>
     * The default color {@link Color#ORANGE} <span style=
     * "background-color: orange; color: orange; border-width: 1px; border-color: black; border-style: solid; width: 15; height: 15;">__</span>
     * </p>
     *
     * @return the property
     */
    public ObjectProperty< Paint > fillProperty()
    {
        return this.fill;
    }

    /**
     * Set a new fill
     * <p>
     * The default color {@link Color#ORANGE} <span style=
     * "background-color: orange; color: orange; border-width: 1px; border-color: black; border-style: solid; width: 15; height: 15;">__</span>
     * </p>
     *
     * @param fill
     *     the fill
     */
    public void setFill( Paint fill )
    {
        fillProperty().set( fill );
    }

    /**
     * Get the current fill
     * <p>
     * The default color {@link Color#ORANGE} <span style=
     * "background-color: orange; color: orange; border-width: 1px; border-color: black; border-style: solid; width: 15; height: 15;">__</span>
     * </p>
     *
     * @return the current fill
     */
    public Paint getFill()
    {
        return fillProperty().get();
    }

    @SuppressWarnings( "null" )
    private static final CssMetaData< TabOutlineMarker, Paint > FILL =
        new CssMetaData< TabOutlineMarker, Paint >( "-fx-fill", StyleConverter.getPaintConverter(),
            Color.ORANGE )
        { //$NON-NLS-1$

            @Override
            public boolean isSettable( TabOutlineMarker node )
            {
                return !node.fillProperty().isBound();
            }

            @SuppressWarnings( "unchecked" )
            @Override
            public StyleableProperty< Paint > getStyleableProperty( TabOutlineMarker node )
            {
                return (StyleableProperty< Paint >)node.fillProperty();
            }

        };

    private static final List< CssMetaData< ? extends Styleable, ? > > STYLEABLES;

    static
    {
        @SuppressWarnings( "static-access" )
        final List< CssMetaData< ? extends Styleable, ? > > styleables =
            new ArrayList< CssMetaData< ? extends Styleable, ? > >( Group.getClassCssMetaData() );
        styleables.add( FILL );
        STYLEABLES = Collections.unmodifiableList( styleables );
    }

    public static List< CssMetaData< ? extends Styleable, ? > > getClassCssMetaData()
    {
        return STYLEABLES;
    }

    @Override
    public List< CssMetaData< ? extends Styleable, ? > > getCssMetaData()
    {
        return getClassCssMetaData();
    }
}
