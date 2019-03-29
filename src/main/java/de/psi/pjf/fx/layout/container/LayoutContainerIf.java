// ******************************************************************
//
// LayoutContainerIf.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.BorderPane;

/**
 * @author created: pkruszczynski on 15.01.2019 16:43
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
@JsonIdentityInfo( generator = ObjectIdGenerators.UUIDGenerator.class )
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type" )
@JsonSubTypes( { @JsonSubTypes.Type( value = LayoutContainerImpl.class, name = "layoutContainer" ) } )
public interface LayoutContainerIf< N extends BorderPane > extends ContainerIf< N >
{

    @JsonIgnore
    ReadOnlyObjectProperty< FocusedState > focusedContainerProperty();

    void clearFocus();

    @JsonIgnore
    FocusedState getFocusedContainer();

    Map< String, ContainerIf< ? > > getContainerIdsMap();

    @JsonIgnore
    ContainerIf< ? > getContainerById( String id );

    @JsonIgnore
    void storeContainerId( String id, ContainerIf< ? > aContainer );

    ContainerIf< ? > removeStoredContainer( String id );

    ContainerIf< ? > getMainContainer();

    void setMainContainer( ContainerIf< ? > aMainContainer );

    void dispose();

    Map< Object, Object > getProperties();
}
