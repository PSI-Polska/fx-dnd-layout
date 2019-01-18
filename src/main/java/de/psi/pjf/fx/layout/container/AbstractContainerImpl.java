// ******************************************************************
//
// AbstractContainerImpl.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.Node;

/**
 * @param <N>
 *     container node type
 * @param <T>
 *     child's type
 *
 * @author created: pkruszczynski on 14.01.2019 13:27
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public abstract class AbstractContainerImpl< N extends Node, T extends ContainerIf< ? > >
    extends AbstractSimpleContainerImpl< N >
{

    @JsonMerge
    @JsonProperty( value = "children" )
    private final List< T > children = new ArrayList<>();
    @JsonIgnore
    private final List< ContainerIf< ? > > childrenUnmodifiable = Collections.unmodifiableList( children );

    protected final List< T > getChildrenInternal()
    {
        return children;
    }

    @Override
    public final List< ContainerIf< ? > > getChildren()
    {
        return childrenUnmodifiable;
    }

}
