// ******************************************************************
//
// SplitContainerImpl.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

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

    public SplitContainerImpl()
    {
    }

//    @JsonCreator
//    private SplitContainerImpl(
//        @JsonProperty( value = "children" ) final List< ContainerIf< ? > > aChildren )
//    {
//        super( aChildren );
//    }

    @Override
    protected SplitPane createNode()
    {
        final SplitPane splitPane = new SplitPane();
        if( dividerPositions != null )
        {
            splitPane.setDividerPositions( dividerPositions );
        }
        for( final ContainerIf< ? > child : getChildrenInternal() )
        {
            splitPane.getItems().add( child.getNode() );
        }
        return splitPane;
    }

    @Override
    public void addChild( final ContainerIf< ? > child )
    {
        getChildrenInternal().add( child );
        child.setParent( this );
        if( isNodeCreated() )
        {
            getNode().getItems().add( child.getNode() );
        }
    }

    @Override
    public void addChild( int index, final ContainerIf< ? > child )
    {
        getChildrenInternal().add( index, child );
        child.setParent( this );
        if( isNodeCreated() )
        {
            getNode().getItems().add( index, child.getNode() );
        }
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
            return getNode().getDividerPositions();
        }
        return dividerPositions;
    }
}
