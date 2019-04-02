// ******************************************************************
//
// DndStackContainer.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.Node;
import javafx.scene.control.TabPane;

import de.psi.pjf.fx.layout.dnd.DndCallbackProviderIf;
import de.psi.pjf.fx.layout.dnd.DndFeedbackService;
import de.psi.pjf.fx.layout.dnd.DndService;
import de.psi.pjf.fx.layout.dnd.DndTabPaneFactory;
import de.psi.pjf.fx.layout.dnd.DragData;
import de.psi.pjf.fx.layout.dnd.DropData;
import de.psi.pjf.fx.layout.dnd.SplitDndSupport;
import de.psi.pjf.fx.layout.dnd.StackDndSupport;

/**
 * @author created: pkruszczynski on 16.01.2019 12:28
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class DndStackContainer extends StackContainerImpl implements DndCallbackProviderIf
{

    private final DndService dndService;
    private final DndFeedbackService dndFeedback;
    @JsonIgnore
    @JacksonInject( value = ContainerConstants.SPLIT_DROP_CALLBACK_NAME )
    private Consumer< DropData > splitDropCallback;
    @JsonIgnore
    @JacksonInject( value = ContainerConstants.TAB_DROP_CALLBACK_NAME )
    private Consumer< DropData > tabDropCallback;
    @JsonIgnore
    @JacksonInject( value = ContainerConstants.TAB_DRAG_START_CALLBACK_NAME )
    private Predicate< DragData > tabDragStartCallback;

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
            new StackDndSupport( this::getTabDragStartCallback, this::getTabDropCallback, dndFeedback,
                dndService );
        final TabPane tabPane = DndTabPaneFactory.createDndTabPane( ( s ) -> {
            s.setStartFunction( dnd::handleDragStart );
            s.setDropConsumer( dnd::handleDropped );
            s.setFeedbackConsumer( dnd::handleFeedback );
            s.setDragFinishedConsumer( dnd::handleFinished );
            s.setClipboardDataFunction( dnd::clipboardDataFunction );
        }, false );
        return tabPane;
    }

    @Override
    protected void postNodeCreation( final Node aNode )
    {
        super.postNodeCreation( aNode );
        initSplittableDnd( this );
        aNode.getStyleClass().add( ContainerStylesConstants.DND_STACK_CONTAINER_STYLE_CLASS );
    }

    /**
     * Initialize drag and drop
     *
     * @param splittableElement
     *     the static group we attach the DnD to
     */
    protected void initSplittableDnd( DndStackContainer splittableElement )
    {
        final SplitDndSupport< DndStackContainer > dndSupport =
            new SplitDndSupport<>( dndService, dndFeedback, splittableElement );
        dndSupport.install();
    }

    @Override
    public Consumer< DropData > getTabDropCallback()
    {
        return tabDropCallback;
    }

    @Override
    public void setTabDropCallback( final Consumer< DropData > aTabDropCallback )
    {
        tabDropCallback = aTabDropCallback;
    }

    @Override
    public Consumer< DropData > getSplitDropCallback()
    {
        return splitDropCallback;
    }

    @Override
    public void setSplitDropCallback( final Consumer< DropData > aSplitDropCallback )
    {
        if( isNodeCreated() )
        {
            throw new IllegalStateException( "Node is already created, so the property won't be used" );
        }
        splitDropCallback = aSplitDropCallback;
    }

    @Override
    public Predicate< DragData > getTabDragStartCallback()
    {
        return tabDragStartCallback;
    }

    @Override
    public void setTabDragStartCallback( final Predicate< DragData > aTabDragStartCallback )
    {
        tabDragStartCallback = aTabDragStartCallback;
    }
}
