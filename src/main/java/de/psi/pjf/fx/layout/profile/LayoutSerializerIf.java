// ******************************************************************
//
// LayoutSerializerIf.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.profile;

import de.psi.pjf.fx.layout.container.LayoutContainerIf;

/**
 * @author created: pkruszczynski on 16.01.2019 17:08
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public interface LayoutSerializerIf
{
    LayoutContainerIf fromXml( String serializedString );

    String toStringValue( LayoutContainerIf aLayoutContainer );
}
