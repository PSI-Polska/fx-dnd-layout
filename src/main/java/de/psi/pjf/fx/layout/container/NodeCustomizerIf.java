// ******************************************************************
//
// NodeCustomizerIf.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import javafx.scene.Node;

/**
 * @author created: pkruszczynski on 17.01.2019 10:43
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
@FunctionalInterface
public interface NodeCustomizerIf
{

    void customize( final ContainerIf< ? > aContainer, final Node aNode );

}
