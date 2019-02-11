// ******************************************************************
//
// ContainerIf.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.container;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javafx.scene.Node;

/**
 * @author created: pkruszczynski on 14.01.2019 12:13
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
@JsonIdentityInfo( generator = ObjectIdGenerators.UUIDGenerator.class )
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type" )
@JsonSubTypes( { @JsonSubTypes.Type( value = SplitContainerImpl.class,
    name = ContainerConstants.SPLIT_CONTAINER_TYPE_NAME ),
    @JsonSubTypes.Type( value = DndStackContainer.class,
        name = ContainerConstants.DND_STACK_CONTAINER_TYPE_NAME ),
    @JsonSubTypes.Type( value = StackContainerImpl.class,
        name = ContainerConstants.STACK_CONTAINER_TYPE_NAME ),
    @JsonSubTypes.Type( value = NodeContainerWrapper.class,
        name = ContainerConstants.NODE_CONTAINER_WRAPPER_TYPE_NAME ),
    @JsonSubTypes.Type( value = TabContainerWrapperImpl.class,
        name = ContainerConstants.TAB_CONTAINER_WRAPPER_TYPE_NAME ),
    @JsonSubTypes.Type( value = LayoutContainerImpl.class,
        name = ContainerConstants.LAYOUT_CONTAINER_TYPE_NAME ) } )
public interface ContainerIf< N extends Node > extends Serializable
{

    @JacksonInject
    void setNodeCustomizerService( NodeCustomizerServiceIf aNodeCustomizerService );

    void addNodeCustomizer( String aId );

    ContainerIf< ? > getParent();

    void setParent( ContainerIf< ? > aParent );

    @JsonIgnore
    List< ContainerIf< ? > > getChildren();

    @JsonIgnore
    N getNode();

    @JsonIgnore
    int getChildrenCount();

    /**
     * Get the index of the tab inde
     *
     * @param aTab
     *     the tab
     *
     * @return the index
     */
    int indexOf( ContainerIf< ? > child );

    void addChild( ContainerIf< ? > child );

    void addChild( int index, ContainerIf< ? > child );

    void removeChild( ContainerIf< ? > child );

}
