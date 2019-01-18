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
package de.psi.pjf.fx.layout.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Utility methods
 *
 * @since 1.2
 */
public class Util
{
    /**
     * Tag used to exclude a node from finding
     */
    public static final String FIND_NODE_EXCLUDE = "findNodeExclude"; //$NON-NLS-1$

    /**
     * Dump the scene graph to a formatted string
     *
     * @param n
     *     the node to start with
     *
     * @return the dump as a formatted XML
     */
    public static String dumpSceneGraph( Node n )
    {
        return new SceneGraphDumper().dump( n ).toString();
    }

    static class SceneGraphDumper
    {
        private StringBuilder sb = new StringBuilder();
        private int ident = 0;

        public StringBuilder dump( Node n )
        {
            for( int i = 0; i < this.ident; i++ )
            {
                this.sb.append( "    " ); //$NON-NLS-1$
            }
            this.ident++;

            this.sb.append( "<" + n.getClass().getName() + " styleClass=\"" //$NON-NLS-1$ //$NON-NLS-2$
                + n.getStyleClass() + "\">\n" ); //$NON-NLS-1$
            if( n instanceof Parent )
            {
                for( Node subNode : ( (Parent)n ).getChildrenUnmodifiable() )
                {
                    dump( subNode );
                }
            }

            this.ident--;
            for( int i = 0; i < this.ident; i++ )
            {
                this.sb.append( "    " ); //$NON-NLS-1$
            }
            this.sb.append( "</" + n.getClass().getName() + ">\n" ); //$NON-NLS-1$ //$NON-NLS-2$

            return this.sb;
        }
    }

    /**
     * Find a node in all windows
     *
     * @param w
     *     the preferred window
     * @param screenX
     *     the screen x
     * @param screenY
     *     the screen y
     *
     * @return the node or <code>null</code>
     */
    @SuppressWarnings( "deprecation" )
    public static Node findNode( Window w, double screenX, double screenY )
    {
        if( w != null && new BoundingBox( w.getX(), w.getY(), w.getWidth(), w.getHeight() )
            .contains( screenX, screenY ) )
        {
            return findNode( w.getScene().getRoot(), screenX, screenY );
        }

        Iterator< Window > impl_getWindows = Window.getWindows().iterator();

        List< Window > sortedWindows = new ArrayList<>();
        Map< Window, List< Window > > parentChildRelation = new HashMap<>();

        while( impl_getWindows.hasNext() )
        {
            Window window = impl_getWindows.next();
            Window owner;
            if( window instanceof Stage )
            {
                owner = ( (Stage)window ).getOwner();
            }
            else if( window instanceof PopupWindow )
            {
                owner = ( (PopupWindow)window ).getOwnerWindow();
            }
            else
            {
                owner = null;
            }

            if( owner == null )
            {
                sortedWindows.add( window );
            }
            else
            {
                List< Window > list = parentChildRelation.get( owner );
                if( list == null )
                {
                    list = new ArrayList<>();
                    parentChildRelation.put( owner, list );
                }
                list.add( window );
            }
        }

        while( !parentChildRelation.isEmpty() )
        {
            for( Window rw : sortedWindows.toArray( new Window[ 0 ] ) )
            {
                List< Window > list = parentChildRelation.remove( rw );
                if( list != null )
                {
                    sortedWindows.addAll( list );
                }
            }
        }

        Collections.reverse( sortedWindows );

        for( Window window : sortedWindows )
        {
            if( !FIND_NODE_EXCLUDE.equals( window.getUserData() ) && new BoundingBox( window.getX(),
                window.getY(), window.getWidth(), window.getHeight() ).contains( screenX, screenY ) )
            {
                return findNode( window.getScene().getRoot(), screenX, screenY );
            }
        }

        return null;
    }

    /**
     * Find all node at the given x/y location starting the search from the given
     * node
     *
     * @param n
     *     the node to use as the start
     * @param screenX
     *     the screen x
     * @param screenY
     *     the screen y
     *
     * @return the node or <code>null</code>
     */
    public static Node findNode( Node n, double screenX, double screenY )
    {
        Node rv = null;
        if( !n.isVisible() )
        {
            return rv;
        }
        Point2D b = n.screenToLocal( screenX, screenY );
        if( n.getBoundsInLocal().contains( b ) && !FIND_NODE_EXCLUDE.equals( n.getUserData() ) )
        {
            rv = n;
            if( n instanceof Parent )
            {
                List< Node > cList =
                    ( (Parent)n ).getChildrenUnmodifiable().stream().filter( no -> no.isVisible() )
                        .collect( Collectors.toList() );

                for( Node c : cList )
                {
                    Node cn = findNode( c, screenX, screenY );
                    if( cn != null )
                    {
                        rv = cn;
                        break;
                    }
                }
            }
        }
        return rv;
    }

}