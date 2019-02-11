// ******************************************************************
//
// ContainerFactory.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;

import de.psi.pjf.fx.layout.dnd.DndFeedbackService;
import de.psi.pjf.fx.layout.dnd.DndService;
import de.psi.pjf.fx.layout.dnd.DragData;
import de.psi.pjf.fx.layout.dnd.DropData;

/**
 * @author created: pkruszczynski on 14.01.2019 12:16
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class ContainerFactory implements ContainerFactoryIf
{

    private DndService dndService;
    private DndFeedbackService dndFeedback;

    public void setDndService( final DndService aDndService )
    {
        dndService = aDndService;
    }

    public void setDndFeedback( final DndFeedbackService aDndFeedback )
    {
        dndFeedback = aDndFeedback;
    }

    @Override
    public SplitContainerIf< SplitPane > createSplitContainer()
    {
        return new SplitContainerImpl();
    }

    @Override
    public StackContainerIf< TabPane > createDndStackContainer()
    {
        final DndStackContainer dndStackContainer = new DndStackContainer( dndService, dndFeedback );
        dndStackContainer.setSplitDropCallback( createSplitDropCallback() );
        dndStackContainer.setTabDropCallback( createTabDropCallBack() );
        dndStackContainer.setTabDragStartCallback( createTabDragStartCallback() );
        return dndStackContainer;
    }

    @Override
    public StackContainerIf< TabPane > createStackContainer()
    {
        return new StackContainerImpl();
    }

    @Override
    public ContainerIf< ? > createNodeContainer( final Supplier< Node > aNodeSupplier )
    {
        return new NodeContainerWrapper( aNodeSupplier );
    }

    @Override
    public LayoutContainerIf< ? > createLayoutContainer()
    {
        return new LayoutContainerImpl();
    }

    @Override
    public < N extends Node > TabContainerWrapperIf< N > createTabContainerWrapper(
        final ContainerIf< N > aContainer )
    {
        return new TabContainerWrapperImpl<>( aContainer );
    }

    @Override
    public ObjectMapper createObjectMapper()
    {
        final ObjectMapper mapper = new ObjectMapper();
        final InjectableValues.Std values = new InjectableValues.Std();
        mapper.setInjectableValues( values );
        values.addValue( DndService.class, dndService );
        values.addValue( DndFeedbackService.class, dndFeedback );
        values.addValue( ContainerConstants.SPLIT_DROP_CALLBACK_NAME, createSplitDropCallback() );
        values.addValue( ContainerConstants.TAB_DROP_CALLBACK_NAME, createTabDropCallBack() );
        values.addValue( ContainerConstants.TAB_DRAG_START_CALLBACK_NAME, createTabDragStartCallback() );
        return mapper;
    }

    protected Consumer< DropData > createSplitDropCallback()
    {
        return aDropData -> {
            if( aDropData.dropType.isSplit() )
            {
                dndService.handleSplit( aDropData.reference, aDropData.sourceElement, aDropData.dropType );
            }
            else if( aDropData.dropType.isInsert() )
            {
                dndService.handleInsert( aDropData.reference, aDropData.sourceElement );
            }
            else
            {
                throw new IllegalStateException( "Unsupported drop type" );
            }
        };
    }

    protected Consumer< DropData > createTabDropCallBack()
    {
        return aDropData -> {
            if( aDropData.dropType.isReorder() )
            {
                dndService.handleReorder( aDropData.reference, aDropData.sourceElement, aDropData.dropType );
            }
            else
            {
                throw new IllegalStateException( "Unsupported drop type" );
            }
        };
    }

    protected Predicate< DragData > createTabDragStartCallback()
    {
        return d -> dndService.dragAllowed( d.container, d.item );
    }

}
