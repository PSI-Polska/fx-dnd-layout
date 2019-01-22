/*******************************************************************************
 * Copyright (c) 2014 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package de.psi.pjf.fx.layout.dnd;

import javafx.geometry.Bounds;

import de.psi.pjf.fx.layout.container.ContainerIf;

/**
 * Service that needs to be implemented to provide feedback for drag and drop
 */
public interface DndFeedbackService
{
    /**
     * Show feedback
     *
     * @param data
     *     the feedback data
     *
     * @return the marker
     */
    MarkerFeedback showFeedback( DnDFeedbackData data );

    /**
     * Feedback data
     */
    class DnDFeedbackData
    {
        /**
         * The reference element currently dragged over
         */
        public final ContainerIf< ? > reference;
        /**
         * The dragged element
         */
        public final ContainerIf< ? > sourceElement;
        /**
         * The drop type
         */
        public final DropLocation dropType;

        /**
         * The container to show the feedback on
         */
        public final ContainerIf< ? > feedbackContainerElement;
        /**
         * The region of the reference element
         */
        public final Bounds containerRegion;

        /**
         * Create a new feedback data instance
         *
         * @param reference
         *     the reference element currently dragged over
         * @param sourceElement
         *     the element dragged
         * @param dropType
         *     the drop type
         * @param feedbackContainerElement
         *     the container element
         * @param containerRegion
         *     the region of the reference element
         */
        public DnDFeedbackData( ContainerIf< ? > reference, ContainerIf< ? > sourceElement,
            DropLocation dropType, ContainerIf< ? > feedbackContainerElement, Bounds containerRegion )
        {
            this.reference = reference;
            this.sourceElement = sourceElement;
            this.dropType = dropType;

            this.feedbackContainerElement = feedbackContainerElement;
            this.containerRegion = containerRegion;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result =
                prime * result + ( ( this.containerRegion == null ) ? 0 : this.containerRegion.hashCode() );
            result = prime * result + ( ( this.dropType == null ) ? 0 : this.dropType.hashCode() );
            result = prime * result + ( ( this.feedbackContainerElement == null ) ?
                0 :
                this.feedbackContainerElement.hashCode() );
            result = prime * result + ( ( this.reference == null ) ? 0 : this.reference.hashCode() );
            result = prime * result + ( ( this.sourceElement == null ) ? 0 : this.sourceElement.hashCode() );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if( this == obj )
            {
                return true;
            }
            if( obj == null )
            {
                return false;
            }
            if( getClass() != obj.getClass() )
            {
                return false;
            }
            DnDFeedbackData other = (DnDFeedbackData)obj;
            if( this.containerRegion == null )
            {
                if( other.containerRegion != null )
                {
                    return false;
                }
            }
            else if( !this.containerRegion.equals( other.containerRegion ) )
            {
                return false;
            }
            if( this.dropType != other.dropType )
            {
                return false;
            }
            if( this.feedbackContainerElement == null )
            {
                if( other.feedbackContainerElement != null )
                {
                    return false;
                }
            }
            else if( !this.feedbackContainerElement.equals( other.feedbackContainerElement ) )
            {
                return false;
            }
            if( this.reference == null )
            {
                if( other.reference != null )
                {
                    return false;
                }
            }
            else if( !this.reference.equals( other.reference ) )
            {
                return false;
            }
            if( this.sourceElement == null )
            {
                return other.sourceElement == null;
            }
            else
            {
                return this.sourceElement.equals( other.sourceElement );
            }
        }
    }

    /**
     * Marker feedback
     */
    abstract class MarkerFeedback
    {
        /**
         * The feedback data
         */
        public final DnDFeedbackData data;

        /**
         * Create a new feedback
         *
         * @param data
         *     the data
         */
        public MarkerFeedback( DnDFeedbackData data )
        {
            this.data = data;
        }

        /**
         * hide the feedback
         */
        public abstract void hide();
    }
}
