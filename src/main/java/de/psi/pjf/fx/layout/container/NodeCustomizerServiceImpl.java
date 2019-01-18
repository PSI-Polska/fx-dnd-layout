// ******************************************************************
//
// NodeCustomizerServiceImpl.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.HashMap;
import java.util.Map;

/**
 * @author created: pkruszczynski on 17.01.2019 10:44
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class NodeCustomizerServiceImpl implements NodeCustomizerServiceIf
{
    private final Map< String, NodeCustomizerIf > map = new HashMap<>();

    @Override
    public NodeCustomizerIf getNodeCustomizer( final String aId )
    {
        return map.get( aId );
    }

    @Override
    public void registerNodeCustomizer( final String aId, final NodeCustomizerIf aCustomizer )
    {
        map.put( aId, aCustomizer );
    }

}
