// ******************************************************************
//
// TabContainerWrapperIf.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * @author created: pkruszczynski on 14.01.2019 13:38
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public interface TabContainerWrapperIf< N extends Node > extends ContainerIf< N >
{

    String getName();

    void setName( String aName );

    ContainerIf< N > getContent();

    Tab getTab();

    @Override
    StackContainerIf< ? > getParent();
}
