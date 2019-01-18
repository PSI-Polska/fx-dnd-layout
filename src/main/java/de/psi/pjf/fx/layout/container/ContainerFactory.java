// ******************************************************************
//
// ContainerFactory.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.function.Supplier;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;

import de.psi.pjf.fx.layout.dnd.DefaultDndFeedback;
import de.psi.pjf.fx.layout.dnd.DndFeedbackService;
import de.psi.pjf.fx.layout.dnd.DndService;

/**
 * @author created: pkruszczynski on 14.01.2019 12:16
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class ContainerFactory implements ContainerFactoryIf
{

    private final DndService dndService;
    private final DndFeedbackService dndFeedback;

    public ContainerFactory( final DndService aDndService, final DndFeedbackService aDndFeedback )
    {
        dndService = aDndService;
        dndFeedback = aDndFeedback;
    }

    public ContainerFactory( final DndService aDndService )
    {
        this( aDndService, new DefaultDndFeedback() );
    }

    @Override
    public SplitContainerIf< SplitPane > createSplitContainer()
    {
        return new SplitContainerImpl();
    }

    @Override
    public StackContainerIf< TabPane > createDndStackContainer()
    {
        return new DndStackContainer( dndService, dndFeedback );
    }

    @Override
    public StackContainerIf< TabPane > createStackContainer()
    {
        return new StackContainerImpl();
    }

    @Override
    public ContainerIf< ? > createNodeContainer( final Supplier< Node > aNodeSupplier )
    {
        return new NodeContainer( aNodeSupplier );
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
        return mapper;
    }

}
