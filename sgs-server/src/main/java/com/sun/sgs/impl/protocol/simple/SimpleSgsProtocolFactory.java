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

package com.sun.sgs.impl.protocol.simple;

import com.sun.sgs.impl.sharedutil.LoggerWrapper;
import com.sun.sgs.impl.sharedutil.PropertiesWrapper;
import com.sun.sgs.impl.util.AbstractService;
import com.sun.sgs.kernel.ComponentRegistry;
import com.sun.sgs.kernel.KernelRunnable;
import com.sun.sgs.nio.channels.AsynchronousByteChannel;
import com.sun.sgs.protocol.Protocol;
import com.sun.sgs.protocol.ProtocolFactory;
import com.sun.sgs.protocol.ProtocolHandler;
import com.sun.sgs.protocol.simple.SimpleSgsProtocol;
import com.sun.sgs.service.TransactionProxy;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A protocol factory for creating new {@link SimpleSgsProtocolImpl}
 * instances, which implement the protocol messages defined in {@link
 * SimpleSgsProtocol}.
 */
public class SimpleSgsProtocolFactory
    extends AbstractService
    implements ProtocolFactory
{
    /** The package name. */
    public static final String PKG_NAME = "com.sun.sgs.impl.protocol.simple";
    
    /** The logger for this class. */
    private static final LoggerWrapper logger =
	new LoggerWrapper(Logger.getLogger(PKG_NAME));

    /** The name of the read buffer size property. */
    private static final String READ_BUFFER_SIZE_PROPERTY =
        PKG_NAME + ".buffer.read.max";

    /** The default read buffer size: {@value #DEFAULT_READ_BUFFER_SIZE} */
    private static final int DEFAULT_READ_BUFFER_SIZE = 128 * 1024;
    
    /** The read buffer size for new connections. */
    private final int readBufferSize;

    /**
     * Constructs an instance with the specified {@code properties},
     * {@code systemRegistry}, and {@code txnProxy}.
     *
     * @param	properties the configuration properties
     * @param	systemRegistry the system registry
     * @param	txnProxy a transaction proxy
     */
    public SimpleSgsProtocolFactory(
	Properties properties, ComponentRegistry systemRegistry,
	TransactionProxy txnProxy)
    {
	super(properties, systemRegistry, txnProxy, logger);
	
	logger.log(Level.CONFIG,
		   "Creating SimpleSgsProtcolFactory properties:{0}",
		   properties);
	
	if (properties == null) {
	    throw new NullPointerException("null properties");
	} else if (systemRegistry == null) {
	    throw new NullPointerException("null systemRegistry");
	} else if (txnProxy == null) {
	    throw new NullPointerException("null txnProxy");
	}
	
	PropertiesWrapper wrappedProps = new PropertiesWrapper(properties);
	try {
            readBufferSize = wrappedProps.getIntProperty(
                READ_BUFFER_SIZE_PROPERTY, DEFAULT_READ_BUFFER_SIZE,
                8192, Integer.MAX_VALUE);

	    // TBD: check service version?
	    
	} catch (RuntimeException e) {
	    if (logger.isLoggable(Level.CONFIG)) {
		logger.logThrow(
		    Level.CONFIG, e,
		    "Failed to create SimpleSgsProtcolFactory");
	    }
	    throw e;
	}
    }

    /** {@inheritDoc} */
    public Protocol newProtocol(
	AsynchronousByteChannel channel, ProtocolHandler handler)
    {
	return new SimpleSgsProtocolImpl(
	    channel, handler, this, readBufferSize);
    }

    /* -- Implement AbstractService -- */
    
    /** {@inheritDoc} */
    protected void handleServiceVersionMismatch(
	Version oldVersion, Version currentVersion)
    {
	throw new IllegalStateException(
	    "unable to convert version:" + oldVersion +
	    " to current version:" + currentVersion);
    }
    
    /** {@inheritDoc} */
    public void doReady() {
    }
    
    /** {@inheritDoc} */
    public void doShutdown() {
    }

    /* -- other methods -- */
    
    /**
     * Schedules a non-durable, non-transactional {@code task}.
     *
     * @param	task a non-durable, non-transactional task
     */
    void scheduleNonTransactionalTask(KernelRunnable task) {
        taskScheduler.scheduleTask(task, taskOwner);
    }
}
