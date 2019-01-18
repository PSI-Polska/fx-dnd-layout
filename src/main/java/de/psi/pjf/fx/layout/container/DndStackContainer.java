// ******************************************************************
//
// DndStackContainer.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;

import de.psi.pjf.fx.layout.dnd.DndTabPaneFactory;
import de.psi.pjf.fx.layout.dnd.BasicDropLocation;
import de.psi.pjf.fx.layout.dnd.DndFeedbackService;
import de.psi.pjf.fx.layout.dnd.DndService;
import de.psi.pjf.fx.layout.dnd.DragData;
import de.psi.pjf.fx.layout.dnd.DropData;
import de.psi.pjf.fx.layout.dnd.EFXDragEvent;
import de.psi.pjf.fx.layout.dnd.SplitDndSupport;
import de.psi.pjf.fx.layout.dnd.StackDndSupport;

/**
 * @author created: pkruszczynski on 16.01.2019 12:28
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class DndStackContainer extends StackContainerImpl
{

    private final DndService dndService;
    private final DndFeedbackService dndFeedback;

    @JsonCreator
    public DndStackContainer( @JacksonInject final DndService dndService,
        @JacksonInject final DndFeedbackService dndFeedback )
    {
        this.dndService = dndService;
        this.dndFeedback = dndFeedback;
    }

    @Override
    protected TabPane createTabPane()
    {
        final StackDndSupport dnd =
            new StackDndSupport( this::getDragStartCallback, this::getDropCallback, dndFeedback,
                dndService );//, this.modelService);
        final TabPane tabPane = DndTabPaneFactory.createDndTabPane( ( s ) -> {
            s.setStartFunction( dnd::handleDragStart );
            s.setDropConsumer( dnd::handleDropped );
            s.setFeedbackConsumer( dnd::handleFeedback );
            s.setDragFinishedConsumer( dnd::handleFinished );
            s.setClipboardDataFunction( dnd::clipboardDataFunction );
        }, false );
        initSplittableDnd( tabPane );
        return tabPane;
    }

    /**
     * Initialize drag and drop
     *
     * @param splittableElement
     *     the static group we attach the DnD to
     */
    protected void initSplittableDnd( Node splittableElement )
    {
        final SplitDndSupport< Node > dndSupport =
            new SplitDndSupport<>( dndService, dndFeedback, splittableElement );
        splittableElement.addEventHandler( DragEvent.DRAG_OVER, dndSupport::handleDragOver );
        splittableElement.addEventHandler( DragEvent.DRAG_EXITED, dndSupport::handleDragExit );
        splittableElement.addEventHandler( DragEvent.DRAG_DROPPED, dndSupport::handleDragDropped );

        splittableElement.addEventHandler( EFXDragEvent.DRAG_OVER, dndSupport::handleDragOver );
        //		splittableElement.addEventHandler(EFXDragEvent.DRAG_EXITED, dndSupport::handleDragExit);
        splittableElement.addEventHandler( EFXDragEvent.DRAG_DROPPED, dndSupport::handleDragDropped );
        if( StackDndSupport.DETACHABLE_DRAG )
        {
            splittableElement.addEventHandler( MouseDragEvent.MOUSE_DRAG_EXITED, dndSupport::handleDragExit );
        }
    }

    protected Function< DragData, Boolean > getDragStartCallback()
    {
        return d -> {
            return dndService.dragAllowed( d.container, d.item );
        };
    }

    protected Function< DropData, Void > getDropCallback()
    {
        return aDropData -> {
            final Optional< Tab > sourceTab = findTab( t -> t.getContent() == aDropData.sourceElement );
            final Optional< Tab > destTab = findTab( t -> t.getContent() == aDropData.reference );
            if( sourceTab.isEmpty() || destTab.isEmpty() )
            {
                return null;
            }
            final TabPane tabPane = getNode();
            if( !tabPane.getTabs().remove( sourceTab.get() ) )
            {
                return null;
            }
            final int indexOfDest = tabPane.getTabs().indexOf( destTab.get() );
            if( indexOfDest == -1 )
            {
                return null;
            }
            final int newIndex =
                aDropData.dropType == BasicDropLocation.BEFORE ? indexOfDest : indexOfDest + 1;
            tabPane.getTabs().add( newIndex, sourceTab.get() );
            tabPane.getSelectionModel().select( newIndex );
            return null;
        };
    }

}
