/*
 * Copyright 2007-2009 Sun Microsystems, Inc.
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
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the LICENSE file that accompanied
 * this code.
 */

package com.sun.sgs.service;

/**
 * A future to be notified when recovery operations for an associated
 * {@link IdentityRelocationListener} are complete.
 *
 * @see IdentityRelocationListener#prepareToRelocate
 */
public interface PrepareMoveCompleteFuture {
    /**
     * Notifies this future that the prepare move operations initiated by
     * the {@link IdentityRelocationListener} associated with this future are
     * complete.  This method is idempotent and can be called multiple times.
     * It may be called synchronously with the listener notification or
     * asynchronously.
     */
    void done();

    /**
     * Returns {@code true} if the {@link #done done} method of this
     * future has been invoked, and {@code false} otherwise.
     *
     * @return	{@code true} if {@code done} has been invoked, and
     *		{@code false} otherwise 
     */
    boolean isDone();
}
