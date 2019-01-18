// ******************************************************************
//
// ContainerFactoryIf.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Node;

/**
 * @author created: pkruszczynski on 15.01.2019 17:40
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public interface ContainerFactoryIf
{
    SplitContainerIf< ? > createSplitContainer();

    StackContainerIf< ? > createDndStackContainer();

    StackContainerIf< ? > createStackContainer();

    ContainerIf< ? > createNodeContainer( Supplier< Node > aNodeSupplier );

    LayoutContainerIf< ? > createLayoutContainer();

    < N extends Node > TabContainerWrapperIf< N > createTabContainerWrapper( ContainerIf< N > aContainer );

    ObjectMapper createObjectMapper();
}
