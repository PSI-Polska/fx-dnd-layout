// ******************************************************************
//
// NodeCustomizerServiceIf.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

/**
 * @author created: pkruszczynski on 17.01.2019 10:42
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public interface NodeCustomizerServiceIf
{

    NodeCustomizerIf getNodeCustomizer( final String aId );

    void registerNodeCustomizer( String aId, NodeCustomizerIf aCustomizer );
}
