// ******************************************************************
//
// AbstractDndService.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import javafx.geometry.Orientation;

import de.psi.pjf.fx.layout.dnd.BasicDropLocation;
import de.psi.pjf.fx.layout.dnd.DndService;
import de.psi.pjf.fx.layout.dnd.DropLocation;
import de.psi.pjf.fx.layout.util.FxUtils;

/**
 * @author created: pkruszczynski on 22.01.2019 12:06
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public abstract class AbstractDndService implements DndService
{
    protected abstract ContainerFactoryIf getContainerFactory();

    @Override
    public boolean splitAllowed( final ContainerIf< ? > element, final ContainerIf< ? > sourceElement,
        final DropLocation dropType )
    {
        return true;
    }

    @Override
    public boolean detachAllowed( final ContainerIf< ? > element )
    {
        return false;
    }

    @Override
    public boolean reorderAllowed( final ContainerIf< ? > reference, final ContainerIf< ? > sourceElement,
        final DropLocation dropLocation )
    {
        return true;
    }

    @Override
    public boolean insertAllowed( final ContainerIf< ? > aReference, final ContainerIf< ? > aSourceElement )
    {
        return true;
    }

    @Override
    public boolean handleDetach( final double x, final double y, final ContainerIf< ? > sourceElement )
    {
        return false;
    }

    @Override
    public boolean handleReorder( final ContainerIf< ? > aReference, final ContainerIf< ? > aSourceElement,
        final DropLocation dropLocation )
    {
        if( !dropLocation.isReorder() )
        {
            throw new IllegalStateException( "Unsupported drop type" );
        }
        if( !( aSourceElement instanceof TabContainerWrapperIf ) || !( aReference
            .getParent() instanceof StackContainerIf< ? > ) )
        {
            throw new IllegalStateException(
                "Dropped source element should be a tab with stack container as a parent." );
        }
        final StackContainerIf< ? > destParent = (StackContainerIf< ? >)aReference.getParent();
        final TabContainerWrapperIf< ? > sourceTabElement = (TabContainerWrapperIf< ? >)aSourceElement;
        final StackContainerIf< ? > oldParent = sourceTabElement.getParent();
        oldParent.removeChild( sourceTabElement );
        cleanupAfterRemoving( oldParent );
        final int indexOfDest = destParent.indexOf( aReference );
        final int newIndex = dropLocation == BasicDropLocation.BEFORE ? indexOfDest : indexOfDest + 1;
        destParent.addChild( newIndex, sourceTabElement );
        destParent.select( sourceTabElement );
        FxUtils.executeOnceWhenPropertyIsNonNull( aSourceElement.getNode().sceneProperty(), $ -> {
            aSourceElement.getNode().requestFocus();
        } );
        return true;
    }

    protected abstract void cleanupAfterRemoving( final ContainerIf< ? > containerToCleanup );

    @Override
    public boolean handleInsert( final ContainerIf< ? > aReference, final ContainerIf< ? > aSourceElement )
    {
        final ContainerIf< ? > oldParent = aSourceElement.getParent();
        oldParent.removeChild( aSourceElement );
        cleanupAfterRemoving( oldParent );
        aReference.addChild( aSourceElement );
        FxUtils.executeOnceWhenPropertyIsNonNull( aSourceElement.getNode().sceneProperty(), $ -> {
            aSourceElement.getNode().requestFocus();
        } );
        return true;
    }

    @Override
    public boolean handleSplit( final ContainerIf< ? > dropTargetContainer,
        final ContainerIf< ? > draggedContainer, final DropLocation dropLocation )
    {
        if( !dropLocation.isSplit() )
        {
            throw new IllegalStateException( "Unsupported drop type" );
        }
        final ContainerIf< ? > oldParent = draggedContainer.getParent();
        final ContainerIf< ? > targetParent = dropTargetContainer.getParent();
        oldParent.removeChild( draggedContainer );
        cleanupAfterRemoving( oldParent );
        final int currentIndex = targetParent.indexOf( dropTargetContainer );
        targetParent.removeChild( dropTargetContainer );
        final SplitContainerIf< ? > splitContainer = getContainerFactory().createSplitContainer();
        final StackContainerIf< ? > stackForDragged = getContainerFactory().createDndStackContainer();
        stackForDragged.addChild( draggedContainer );
        if( dropLocation == BasicDropLocation.SPLIT_RIGHT )
        {
            splitContainer.setOrientation( Orientation.HORIZONTAL );
            splitContainer.addChild( dropTargetContainer );
            splitContainer.addChild( stackForDragged );
        }
        else if( dropLocation == BasicDropLocation.SPLIT_LEFT )
        {
            splitContainer.setOrientation( Orientation.HORIZONTAL );
            splitContainer.addChild( stackForDragged );
            splitContainer.addChild( dropTargetContainer );
        }
        else if( dropLocation == BasicDropLocation.SPLIT_TOP )
        {
            splitContainer.setOrientation( Orientation.VERTICAL );
            splitContainer.addChild( stackForDragged );
            splitContainer.addChild( dropTargetContainer );
        }
        else
        {
            splitContainer.setOrientation( Orientation.VERTICAL );
            splitContainer.addChild( dropTargetContainer );
            splitContainer.addChild( stackForDragged );
        }
        targetParent.addChild( currentIndex, splitContainer );
        FxUtils.executeOnceWhenPropertyIsNonNull( draggedContainer.getNode().sceneProperty(), $ -> {
            draggedContainer.getNode().requestFocus();
        } );
        return true;
    }

}
