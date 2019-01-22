// ******************************************************************
//
// SplitContainerIf.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

/**
 * @author created: pkruszczynski on 14.01.2019 12:11
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public interface SplitContainerIf< N extends SplitPane > extends ContainerIf< N >
{
    void setDividerPositions( double... aV );

    double[] getDividerPositions();

    Orientation getOrientation();

    void setOrientation( Orientation aOrientation );
}
