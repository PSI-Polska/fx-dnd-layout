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
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import de.psi.pjf.fx.layout.container.ContainerUtils;
import de.psi.pjf.fx.layout.container.TabContainerWrapperIf;

/**
 * Class implementing DnD
 */
public class StackDndSupport extends AbstractDndSupport
{

    private final Supplier< Function< DragData, Boolean > > dragStartCallbackProvider;

    private final Supplier< Function< DropData, Void > > dropCallbackProvider;

    //    private final TabPane stack;

    private final DndService dndService;

    //	private final ModelService modelService;

    /**
     * Support detach drag and drop
     */
    public final static boolean DETACHABLE_DRAG = Boolean.getBoolean( "detachdrag.enabled" ); //$NON-NLS-1$

    /**
     * Create a new dnd support instance
     *
     * @param dragStartCallbackProvider
     *     the start callback
     * @param dropCallbackProvider
     *     the drop callback
     * @param feedbackService
     *     the feedback service
     * @param stack
     *     the stack working for
     * @param dndService
     *     the dnd service
     */
    public StackDndSupport( Supplier< Function< DragData, Boolean > > dragStartCallbackProvider,
        Supplier< Function< DropData, Void > > dropCallbackProvider, DndFeedbackService feedbackService,
        //        TabPane stack,
        DndService dndService )//, ModelService modelService )
    {
        super( feedbackService );
        this.dndService = dndService;
        this.dragStartCallbackProvider = dragStartCallbackProvider;
        this.dropCallbackProvider = dropCallbackProvider;
        //        this.stack = stack;
        //        this.modelService = modelService;
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
        //        MStackElement domElement = ( (WStackItem< ?, ? >)tab.getUserData() ).getDomElement();
        //        String rv = null;
        //        if( domElement != null )
        //        {
        //            rv = this.modelService.getUniqueId( domElement );
        //        }
        //        if( rv != null )
        //        {
        //            return rv;
        //        }
        //        throw new IllegalStateException( "The model element has no ID" ); //$NON-NLS-1$
        final Tab t = tab.getTab();
        return Optional.ofNullable( ContainerUtils.getNodeId( t.getContent() ) ).orElse( "sampleid" );
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
        Function< DragData, Boolean > dragStartCallback = this.dragStartCallbackProvider.get();
        if( dragStartCallback != null )
        {
            final Tab t = tab.getTab();
            if( t != null )
            {
                DragData dragData = new DragData( t.getTabPane(), t.getContent() );
                if( dragStartCallback.apply( dragData ) )
                {
                    return Boolean.TRUE;
                }
            }
            //            WStackItem< ?, ? > item =
            //                (org.eclipse.fx.ui.workbench.renderers.base.widget.WStack.WStackItem< ?, ? >)tab
            //                    .getUserData();
            //            MStackElement itemElement = item.getDomElement();
            //            if( itemElement == null )
            //            {
            //                return Boolean.FALSE;
            //            }
            //            MPartStack itemContainer = (MPartStack)(MUIElement)itemElement.getParent();
            //            if( itemContainer != null )
            //            {
            //                DragData dragData = new DragData( itemContainer, itemElement );
            //                if( dragStartCallback.call( dragData ).booleanValue() )
            //                {
            //                    return Boolean.TRUE;
            //                }
            //            }
            //            else
            //            {
            //                LOGGER.error(
            //                    "Stack element '" + itemElement + "' has no container" ); //$NON-NLS-1$//$NON-NLS-2$
            //            }
            //
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
        Function< DropData, Void > call = this.dropCallbackProvider.get();
        if( call != null )
        {
            if( data.dropType == DndTabPaneFactory.DropType.DETACH )
            {
                //                WStackItem< ?, ? > sourceItem =
                //                    (org.eclipse.fx.ui.workbench.renderers.base.widget.WStack.WStackItem< ?, ? >)data.draggedTab
                //                        .getUserData();
                //                MStackElement domElement = sourceItem.getDomElement();
                //                if( domElement != null )
                //                {
                final Tab tab = data.draggedTab.getTab();
                call.apply(
                    new DropData( data.x, data.y, null, tab.getContent(), BasicDropLocation.DETACH ) );
                //                }
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

                final TabPane draggedTabParent = draggedTab.getTabPane();
                final TabPane targetTabParent = targetTab.getTabPane();

                if( targetTabParent != draggedTabParent && this.dndService != null && !this.dndService
                    .reparentAllowed( draggedTab.getContent(), draggedTabParent ) )
                {
                    cleanup();
                    return;
                }

                final DropLocation dropLocation =
                    data.dropType == DndTabPaneFactory.DropType.AFTER ? BasicDropLocation.AFTER : BasicDropLocation.BEFORE;
                call.apply( new DropData( data.x, data.y, targetTab.getContent(), draggedTab.getContent(),
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

        final TabPane draggedTabParent = draggedTab.getTabPane();
        final TabPane targetTabParent = targetTab.getTabPane();

        if( targetTabParent != draggedTabParent && this.dndService != null && !this.dndService
            .reparentAllowed( draggedTab.getContent(), draggedTabParent ) )
        {
            cleanup();
            return;
        }

        final DropLocation dropLocation =
            data.dropType == DndTabPaneFactory.DropType.AFTER ? BasicDropLocation.AFTER : BasicDropLocation.BEFORE;
        updateFeedback(
            new DndFeedbackService.DnDFeedbackData( targetTab.getContent(), draggedTab.getContent(),
                dropLocation, targetTabParent, data.bounds ) );
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
