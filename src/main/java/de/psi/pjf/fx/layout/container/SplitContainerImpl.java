// ******************************************************************
//
// SplitContainerImpl.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

/**
 * @author created: pkruszczynski on 14.01.2019 12:17
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class SplitContainerImpl extends AbstractContainerImpl< SplitPane, ContainerIf< ? > >
    implements SplitContainerIf< SplitPane >
{

    private double[] dividerPositions;
    private Orientation orientation;

    public SplitContainerImpl()
    {
    }

    @Override
    protected SplitPane createNode()
    {
        final SplitPane splitPane = new SplitPane();
        if( orientation != null )
        {
            splitPane.setOrientation( orientation );
        }
        final List< Node > items =
            getChildrenInternal().stream().map( ContainerIf::getNode ).collect( Collectors.toList() );
        splitPane.getItems().setAll( items );
        if( dividerPositions != null )
        {
            splitPane.setDividerPositions( dividerPositions );
        }
        return splitPane;
    }

    @Override
    protected void postNodeCreation( final Node aNode )
    {
        aNode.getStyleClass().add( ContainerStylesConstants.SPLIT_CONTAINER_STYLE_CLASS );
    }

    @Override
    protected void addChildFx( final ContainerIf< ? > child )
    {
        getNode().getItems().add( child.getNode() );
    }

    @Override
    protected void addChildFx( final int index, final ContainerIf< ? > child )
    {
        getNode().getItems().add( index, child.getNode() );
    }

    @Override
    protected void removeChildFx( final ContainerIf< ? > child )
    {
        getNode().getItems().remove( child.getNode() );
    }

    @Override
    public void setDividerPositions( final double... aPositions )
    {
        dividerPositions = aPositions;
        if( isNodeCreated() )
        {
            getNode().setDividerPositions( aPositions );
        }
    }

    @Override
    public double[] getDividerPositions()
    {
        if( isNodeCreated() )
        {
            return getNode()
                .getDividerPositions(); // FIXME pkruszczynski 21.01.2019: to check if taken on serialization
        }
        return dividerPositions;
    }

    @Override
    public Orientation getOrientation()
    {
        if( isNodeCreated() )
        {
            return getNode()
                .getOrientation(); // FIXME pkruszczynski 21.01.2019: to check if taken on serialization
        }
        return orientation;
    }

    @Override
    public void setOrientation( final Orientation aOrientation )
    {
        orientation = aOrientation;
        if( isNodeCreated() )
        {
            getNode().setOrientation( orientation );
        }
    }
}
