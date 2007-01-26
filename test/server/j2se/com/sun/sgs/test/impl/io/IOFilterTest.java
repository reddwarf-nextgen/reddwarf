package com.sun.sgs.test.impl.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.sgs.impl.io.ServerSocketEndpoint;
import com.sun.sgs.impl.io.SocketEndpoint;
import com.sun.sgs.impl.io.TransportType;
import com.sun.sgs.io.IOAcceptorListener;
import com.sun.sgs.io.IOAcceptor;
import com.sun.sgs.io.IOConnector;
import com.sun.sgs.io.IOHandle;
import com.sun.sgs.io.IOHandler;

/**
 * This suite of tests is intended to test the functionality of the 
 * {@code IOFilter}s.  As new filters are written, they can be tested here.
 */
public class IOFilterTest {

    private final static int DELAY = 2000;
    
    private final static int SERVER_PORT = 5000;
    private final SocketAddress SERVER_ADDRESS = 
        new InetSocketAddress("", SERVER_PORT);
    
    IOAcceptor<SocketAddress> acceptor;
    IOHandle connectedHandle = null;
    
    /**
     * Set up an IOAcceptor with a CompleteMessageFilter installed that echos
     * the packets back to the client.
     */
    @Before
    public void init() {
        connectedHandle = null;
        acceptor = new ServerSocketEndpoint(
                new InetSocketAddress(SERVER_PORT),
               TransportType.RELIABLE).createAcceptor();
        
        try {
            acceptor.listen(new IOAcceptorListener() {

                public IOHandler newHandle() {
                    return new IOHandlerAdapter() {
                        
                        @Override
                        public void bytesReceived(IOHandle handle, byte[] buffer) {
                            byte[] echoMessage = new byte[buffer.length];
                            try {
                                handle.sendBytes(echoMessage);
                            }
                            catch (IOException ioe) {}
                        }
                    };
                }

                public void disconnected() {
                    // TODO Auto-generated method stub
                    
                }
                
            });
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    @After
    public void cleanup() {
        if (connectedHandle != null) {
            try {
                connectedHandle.close();
            } catch (IOException e) {
                System.err.println("Ignoring exception on close");
                e.printStackTrace();
                // ignore the exception
            }
            connectedHandle = null;
        }
        acceptor.shutdown();
        acceptor = null;
    }
    
    /**
     * This test ensures that a message big enough to be split into pieces
     * arrives in one piece on the other end (and is subsequently echoed back).
     * MINA's internal buffers max out at about 8k, so a 100k message will be 
     * split into 12 or so chunks.
     *
     * @throws IOException if an unexpected IO problem occurs
     */
    @Test
    public void bigMessage() throws IOException {
        IOConnector<SocketAddress> connector = 
                    new SocketEndpoint(SERVER_ADDRESS, TransportType.RELIABLE, 
                            Executors.newCachedThreadPool()).createConnector();
        
        
        IOHandler handler = new IOHandlerAdapter() {
            
            int messageSize = 100000;
            
            public void connected(IOHandle handle) {
                IOFilterTest.this.connectedHandle = handle;
                byte[] bigMessage = new byte[messageSize];
                for (int i = 0; i < messageSize; i++) {
                    bigMessage[i] = (byte) 1;
                }
                try {
                    handle.sendBytes(bigMessage);
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            
            public void bytesReceived(IOHandle handle, byte[] buffer) {
                Assert.assertEquals(messageSize, buffer.length);
                notifyAll();
                try {
                    handle.close();
                }
                catch (IOException ioe) {}
            }

            public void disconnected(IOHandle handle) {
                IOFilterTest.this.connectedHandle = null;
            }
        };
        
        connector.connect(handler);
        
        synchronized(this) {
            try {
                wait(DELAY);
            }
            catch (InterruptedException ie) {}
        }

    } 
    
    private int bytesIn;
    
    /**
     * Tests the ability to have different filters installed on the client and
     * the server and still ultimately have the same number of bytes come
     * through.  In this case the server has a CompleteMessageFilter installed,
     * and the client has the default PassthroughFilter installed.
     *
     * @throws IOException if an unexpected IO problem occurs
     */
    @Test
    public void hybridFilter() throws IOException {
        bytesIn = 0;
        final int messageSize = 1000;
        IOConnector<SocketAddress> connector = 
                    new SocketEndpoint(SERVER_ADDRESS, TransportType.RELIABLE, 
                            Executors.newCachedThreadPool()).createConnector();
        
        
        IOHandler handler = new IOHandlerAdapter() {
            
            public void connected(IOHandle handle) {
                IOFilterTest.this.connectedHandle = handle;
                byte[] message = new byte[messageSize];
                try {
                    handle.sendBytes(message);
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            
            public void bytesReceived(IOHandle handle, byte[] buffer) {
                bytesIn += buffer.length;
                System.err.println("Got " + buffer.length +
                        " bytes, total = " + bytesIn);
            }

            public void disconnected(IOHandle handle) {
                IOFilterTest.this.connectedHandle = null;
            }
        };
        
        connector.connect(handler);
        
        synchronized(this) {
            try {
                wait(DELAY);
            }
            catch (InterruptedException ie) {}
        }
        Assert.assertEquals(messageSize, bytesIn);

    } 
    
    private static class IOHandlerAdapter implements IOHandler {
        public void bytesReceived(IOHandle handle, byte[] buffer) {
        }

        public void connected(IOHandle handle) {
        }

        public void disconnected(IOHandle handle) {
        }

        public void exceptionThrown(IOHandle handle, Throwable exception) {
        }   
    }

}
