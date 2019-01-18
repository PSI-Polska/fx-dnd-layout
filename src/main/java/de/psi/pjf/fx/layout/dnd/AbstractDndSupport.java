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

/**
 * Base support for DnD
 */
public abstract class AbstractDndSupport
{

    private static DndFeedbackService.MarkerFeedback CURRENT_MARKER_FEEDBACK = null;
    private final DndFeedbackService feedbackService;

    /**
     * Create a new base support
     *
     * @param feedbackService
     *     the feedback service
     */
    public AbstractDndSupport( DndFeedbackService feedbackService )
    {
        this.feedbackService = feedbackService;
    }

    /**
     * Update the feedback with new data
     *
     * @param data
     *     the data
     */
    protected void updateFeedback( DndFeedbackService.DnDFeedbackData data )
    {
        DndFeedbackService.MarkerFeedback f = CURRENT_MARKER_FEEDBACK;
        if( f == null || !f.data.equals( data ) )
        {
            cleanup();
            CURRENT_MARKER_FEEDBACK = this.feedbackService.showFeedback( data );
        }
    }

    /**
     * Clean up the feedback data
     */
    protected static void cleanup()
    {
        if( CURRENT_MARKER_FEEDBACK != null )
        {
            CURRENT_MARKER_FEEDBACK.hide();
            CURRENT_MARKER_FEEDBACK = null;
        }
    }
}
