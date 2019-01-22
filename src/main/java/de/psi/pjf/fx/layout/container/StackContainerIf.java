// ******************************************************************
//
// StackContainerIf.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.List;

import javafx.geometry.Side;
import javafx.scene.control.TabPane;

/**
 * @author created: pkruszczynski on 14.01.2019 12:11
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public interface StackContainerIf< N extends TabPane > extends ContainerIf< N >
{

    List< TabContainerWrapperIf< ? > > getTabWrappers();

    @Override
    N getNode();

    /**
     * @return the number of tab elements
     */
    int getTabCount();

    /**
     * Select the tab
     *
     * @param aTab
     */
    void select( TabContainerWrapperIf< ? > aTab );

    void setSide( Side aSide );

    Side getSide();

    TabContainerWrapperIf< ? > getSelectedTab();
}
