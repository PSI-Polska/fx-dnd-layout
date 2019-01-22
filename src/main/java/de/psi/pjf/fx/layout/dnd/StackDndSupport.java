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
import java.util.function.Predicate;
import java.util.function.Supplier;

import javafx.scene.control.Tab;

import de.psi.pjf.fx.layout.container.ContainerIf;
import de.psi.pjf.fx.layout.container.ContainerUtils;
import de.psi.pjf.fx.layout.container.TabContainerWrapperIf;

/**
 * Class implementing DnD
 */
public class StackDndSupport extends AbstractDndSupport
{

    /**
     * Support detach drag and drop
     */
    public final static boolean DETACHABLE_DRAG = Boolean.getBoolean( "detachdrag.enabled" ); //$NON-NLS-1$
    private final Supplier< Predicate< DragData > > dragStartCallbackProvider;
    private final Supplier< Consumer< DropData > > dropCallbackProvider;
    private final DndService dndService;

    /**
     * Create a new dnd support instance
     *
     * @param dragStartCallbackProvider
     *     the start callback
     * @param dropCallbackProvider
     *     the drop callback
     * @param feedbackService
     *     the feedback service
     * @param dndService
     *     the dnd service
     */
    public StackDndSupport( Supplier< Predicate< DragData > > dragStartCallbackProvider,
        Supplier< Consumer< DropData > > dropCallbackProvider, DndFeedbackService feedbackService,
        DndService dndService )
    {
        super( feedbackService );
        this.dndService = dndService;
        this.dragStartCallbackProvider = dragStartCallbackProvider;
        this.dropCallbackProvider = dropCallbackProvider;
    }

    /**
     * Function to serialize tab
     *
     * @param tab
     *     the tab
     *
     * @return the tab
     */
    public String clipboardDataFunction( TabContainerWrapperIf< ? > tab )
    {
        final Tab t = tab.getTab();
        return ContainerUtils.getNodeId( t.getContent() );
    }

    /**
     * Handle the drag start
     *
     * @param tab
     *     the tab
     *
     * @return <code>true</code> if drag can start
     */
    @SuppressWarnings( "null" )
    public Boolean handleDragStart( TabContainerWrapperIf< ? > tab )
    {
        Predicate< DragData > dragStartCallback = this.dragStartCallbackProvider.get();
        if( dragStartCallback == null )
        {
            return Boolean.FALSE;
        }
        final Tab t = tab.getTab();
        if( t == null )
        {
            return Boolean.FALSE;
        }
        final DragData dragData = new DragData( tab.getParent(), tab );
        if( dragStartCallback.test( dragData ) )
        {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * Handle the drop
     *
     * @param data
     *     the data
     */
    @SuppressWarnings( "all" )
    public void handleDropped( DndTabPaneFactory.DroppedData data )
    {
        Consumer< DropData > call = this.dropCallbackProvider.get();
        if( call != null )
        {
            if( data.dropType == DndTabPaneFactory.DropType.DETACH )
            {
                final Tab tab = data.draggedTab.getTab();
                final Optional< ContainerIf< ? > > container = ContainerUtils.getContainer( tab );
                if( container.isPresent() )
                {
                    call.accept(
                        new DropData( data.x, data.y, null, container.get(), BasicDropLocation.DETACH ) );
                }
            }
            else if( data.targetTab != null )
            {
                final Tab draggedTab = data.draggedTab.getTab();
                final Tab targetTab = data.targetTab.getTab();
                if( draggedTab == targetTab )
                {
                    cleanup();
                    return;
                }

                final Optional< ContainerIf< ? > > draggedTabContainer =
                    ContainerUtils.getContainer( draggedTab );
                final Optional< ContainerIf< ? > > targetTabContainer =
                    ContainerUtils.getContainer( targetTab );
                if( draggedTabContainer.isEmpty() || targetTabContainer.isEmpty() )
                {
                    cleanup();
                    return;
                }
                final ContainerIf< ? > draggedTabContainerParent = draggedTabContainer.get().getParent();
                final ContainerIf< ? > targetTabContainerParent = targetTabContainer.get().getParent();
                if( draggedTabContainerParent == null || targetTabContainerParent == null )
                {
                    cleanup();
                    return;
                }

                if( draggedTabContainerParent != targetTabContainerParent && this.dndService != null
                    && !this.dndService
                    .reparentAllowed( draggedTabContainer.get(), draggedTabContainerParent ) )
                {
                    cleanup();
                    return;
                }

                final DropLocation dropLocation = data.dropType == DndTabPaneFactory.DropType.AFTER ?
                    BasicDropLocation.AFTER :
                    BasicDropLocation.BEFORE;
                call.accept(
                    new DropData( data.x, data.y, targetTabContainer.get(), draggedTabContainer.get(),
                        dropLocation ) );
            }
        }
    }

    /**
     * Handle the feedback event
     *
     * @param data
     *     the data
     */
    public void handleFeedback( DndTabPaneFactory.FeedbackData data )
    {
        if( data.dropType == DndTabPaneFactory.DropType.NONE )
        {
            cleanup();
            return;
        }
        final Tab draggedTab = data.draggedTab.getTab();
        final Tab targetTab = data.targetTab.getTab();
        if( draggedTab == targetTab )
        {
            cleanup();
            return;
        }

        final Optional< ContainerIf< ? > > draggedTabContainer = ContainerUtils.getContainer( draggedTab );
        final Optional< ContainerIf< ? > > targetTabContainer = ContainerUtils.getContainer( targetTab );
        if( draggedTabContainer.isEmpty() || targetTabContainer.isEmpty() )
        {
            cleanup();
            return;
        }
        final ContainerIf< ? > draggedTabContainerParent = draggedTabContainer.get().getParent();
        final ContainerIf< ? > targetTabContainerParent = targetTabContainer.get().getParent();
        if( draggedTabContainerParent == null || targetTabContainerParent == null )
        {
            cleanup();
            return;
        }

        if( draggedTabContainerParent != targetTabContainerParent && this.dndService != null
            && !this.dndService.reparentAllowed( draggedTabContainer.get(), draggedTabContainerParent ) )
        {
            cleanup();
            return;
        }

        final DropLocation dropLocation = data.dropType == DndTabPaneFactory.DropType.AFTER ?
            BasicDropLocation.AFTER :
            BasicDropLocation.BEFORE;
        updateFeedback(
            new DndFeedbackService.DnDFeedbackData( targetTabContainer.get(), draggedTabContainer.get(),
                dropLocation, targetTabContainerParent, data.bounds ) );
    }

    /**
     * Handle the finish event
     *
     * @param tab
     *     the tab
     */
    @SuppressWarnings( "static-method" )
    public void handleFinished( TabContainerWrapperIf< ? > tab )
    {
        cleanup();
    }

}
