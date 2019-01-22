// ******************************************************************
//
// DndCallbackProviderIf.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.dnd;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.Node;

/**
 * @author created: pkruszczynski on 19.01.2019 13:47
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public interface DndCallbackProviderIf
{

    Consumer< DropData > getTabDropCallback();

    void setTabDropCallback( Consumer< DropData > aTabDropCallback );

    @JsonIgnore
    Consumer< DropData > getSplitDropCallback();

    void setSplitDropCallback( Consumer< DropData > aSplitDropCallback );

    @JsonIgnore
    Node getNode();

    Predicate< DragData > getTabDragStartCallback();

    void setTabDragStartCallback( Predicate< DragData > aTabDragStartCallback );
}
