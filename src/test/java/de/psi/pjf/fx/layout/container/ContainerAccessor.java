// ******************************************************************
//
// ContainerAccessor.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.Set;

/**
 * @author created: pkruszczynski on 11.02.2019 13:24
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class ContainerAccessor
{

    public static Set< String > getNodeCustomizers( final AbstractSimpleContainerImpl< ? > container )
    {
        return Set.copyOf( container.getNodeCustomizerIds() );
    }

}
