// ******************************************************************
//
// LayoutSerializer.java
// Copyright 2019 PSI AG. All rights reserved.
// PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package de.psi.pjf.fx.layout.profile;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.psi.pjf.fx.layout.container.ContainerFactoryIf;
import de.psi.pjf.fx.layout.container.LayoutContainerIf;

/**
 * @author created: pkruszczynski on 15.01.2019 14:31
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public final class LayoutSerializer implements LayoutSerializerIf
{
    private final ContainerFactoryIf containerFactory;

    public LayoutSerializer( final ContainerFactoryIf aContainerFactory )
    {
        containerFactory = Objects.requireNonNull( aContainerFactory );
    }

    @Override
    public LayoutContainerIf< ? > fromXml( final String serializedLayout )
    {
        try
        {
            final ObjectMapper objectMapper = containerFactory.createObjectMapper();
            final LayoutContainerIf< ? > layoutContainer =
                objectMapper.readValue( serializedLayout, LayoutContainerIf.class );
            return layoutContainer;
        }
        catch( IOException ex )
        {
            throw new IllegalStateException( "Cannot deserialize layout.", ex );
        }
    }

    @Override
    public String toStringValue( final LayoutContainerIf aLayoutContainer )
    {
        final ObjectMapper objectMapper = containerFactory.createObjectMapper();
        try
        {
            final String serializedLayout = objectMapper.writeValueAsString( aLayoutContainer );
            return serializedLayout;
        }
        catch( JsonProcessingException ex )
        {
            throw new IllegalStateException( "Cannot serialize layout.", ex );
        }
    }

}
