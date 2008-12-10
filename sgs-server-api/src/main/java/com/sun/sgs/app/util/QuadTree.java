/*
 * Copyright 2007-2008 Sun Microsystems, Inc.
 *
 * This file is part of Project Darkstar Server.
 *
 * Project Darkstar Server is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * Project Darkstar Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sun.sgs.app.util;

import com.sun.sgs.app.ManagedObjectRemoval;
import com.sun.sgs.app.ObjectNotFoundException;

public interface QuadTree<T> extends ManagedObjectRemoval {

    /**
     * Adds the element to the quadtree given the coordinate values.
     * 
     * @param x the x-coordinate of the element
     * @param y the y-coordinate of the element
     * @param element the element to store
     * @return {@code true} if the element was successfully added and
     * {@code false} otherwise
     * @throws IllegalArgumentException if the coordinates are not contained
     * within the bounding box defined by the quadtree
     */
    boolean put(double x, double y, T element);
    
    /**
     * Returns an iterator for the elements which are contained within the the
     * bounding box created by the two given coordinates. The elements which can
     * be iterated are in no particular order, and there may not be any
     * elements to iterate over (empty iterator).
     * 
     * @param x1 the x-coordinate of the first corner
     * @param y1 the y-coordinate of the first corner
     * @param x2 the x-coordinate of the second corner
     * @param y2 the y-coordinate of the second corner
     * @return an iterator which can traverse over the entries within the
     * coordinates representing the bounding box
     */
    QuadTreeIterator<T> boundingBoxIterator(double x1, double y1, double x2,
	    double y2);
    
    /**
     * Asynchronously clears the tree and replaces it with an empty
     * implementation.
     */
    void clear();
    
    /**
     * Returns {@code true} if there is an element at the given coordinates,
     * or {@code false} otherwise.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return {@code true} if there is an element at the given coordinates,
     * or {@code false} otherwise
     */
    boolean contains(double x, double y);
    
    /**
     * Removes an element from the quadtree corresponding to the provided
     * coordinate and returns the result of the operation.
     * 
     * @param x the x-coordinate of the element to remove
     * @param y the y-coordinate of the element to remove
     * @return {@code true} if the object was removed, and {@code false}
     * otherwise
     */
    boolean delete(double x, double y);
    
    /**
     * Returns the element with the given Cartesian parameters. If the
     * parameters do not correspond to a stored element, then {@code null} is
     * returned.
     * 
     * @param x the x-coordinate of the arbitrary point
     * @param y the y-coordinate of the arbitrary point
     * @return the element at the given coordinates, or {@code null} if none
     * exists
     */
    T get(double x, double y);
    
    /**
     * Returns an array of four coordinates which represent the individual x
     * and y- coordinates specifying the bounding box of the quadtree.
     * In order to properly extract the values from the array, it is advised
     * to use the integer fields within the {@code Coordinate} enumeration.
     * For example, to obtain the smallest x-coordinate, the call should be:
     * <p>
     * {@code getDirectionalBoundingBox()[MIN_X_INT]}
     *  
     * 
     * @param direction the direction of interest for the bounding box
     * @return a double value representing the bounding box border for the
     * given direction, or {@code NaN} if the direction is invalid
     */
    double[] getDirectionalBoundingBox();
    
    /**
     * Determines if the tree is empty.
     * 
     * @return {@code true} if the tree is empty, and {@code false} otherwise
     */
    boolean isEmpty();
    
    /**
     * Returns an iterator over the elements contained in the
     * {@code backingMap}. The {@code backingMap} corresponds to all the
     * elements in the tree.
     * 
     * @return an {@code Iterator} over all the elements in the map
     */
    public QuadTreeIterator<T> iterator();
    
    /**
     * Removes an element from the quadtree corresponding to the provided
     * coordinate.
     * 
     * @param x the x-coordinate of the element to remove
     * @param y the y-coordinate of the element to remove
     * @return the object corresponding to the coordinate, or {@code null} if
     * none exists
     * @throws ObjectNotFoundException if the underlying element was removed
     * from the data manager
     */
    public T remove(double x, double y);
    
    /**
     * Replaces the element at the given coordinate with the given parameter.
     * 
     * @param x the x-coordinate to set the new element
     * @param y the y-coordinate to set the new element
     * @param element the new element to replace the current one
     * @return the old element which was replaced, or {@code null} if there
     * was no element existing at the supplied coordinate
     */
    public T set(double x, double y, T element);
}
