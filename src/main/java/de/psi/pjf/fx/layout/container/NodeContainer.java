// ******************************************************************
//
// NodeContainer.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.Node;

/**
 * @author created: pkruszczynski on 14.01.2019 12:35
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class NodeContainer extends AbstractSimpleContainerImpl< Node >
{
    @JsonIgnore
    private Supplier< Node > contentSupplier;

    public NodeContainer()
    {
    }

    public NodeContainer( final Supplier< Node > aContentSupplier )
    {
        contentSupplier = aContentSupplier;
    }

    public Supplier< Node > getContentSupplier()
    {
        return contentSupplier;
    }

    public void setContentSupplier( final Supplier< Node > aContentSupplier )
    {
        contentSupplier = aContentSupplier;
    }

    @Override
    protected Node createNode()
    {
        return contentSupplier.get();
    }

    @Override
    public List< ContainerIf< ? > > getChildren()
    {
        return Collections.emptyList();
    }
}
