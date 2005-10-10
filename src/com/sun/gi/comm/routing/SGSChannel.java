package com.sun.gi.comm.routing;

import java.nio.ByteBuffer;

import com.sun.gi.comm.users.server.SGSUser;



public interface SGSChannel {

	/**
	 * This call distributes a message to a single UserIDs.
	 * 
	 * @param from
	 *            UserID The User who sent the message.
	 * @param to
	 *            UserID The UserID who sent the message (a return address.)
	 * @param message
	 *            byte[] A byte array containing the data to be sent.
	 */
	public void unicastData(UserID from, UserID to, ByteBuffer message,
			boolean reliable);

	/**
	 * This call distributes a message to a set of UserIDs.
	 * 
	 * @param userID
	 *            UserID[] The UserIDs to receive the message.
	 * @param tolist
	 *            UserID The UserID who sent the message (a return address.)
	 * @param message
	 *            byte[] A byte array containing the data to be sent.
	 */
	public void multicastData(UserID userID, UserID[] tolist,
			ByteBuffer message, boolean reliable);

	/**
	 * This call distributes a message to all users in a game. If targets is a 0
	 * length array then this method will broadcast to all connected users.
	 * 
	 * @param userID
	 *            UserID The UserID who sent the message (a return address.)
	 * @param databuff
	 *            byte[] A byte array containing the data to be sent.
	 */
	public void broadcastData(UserID userID, ByteBuffer databuff, boolean reliable);

	public void join(SGSUser user);
	
	public void leave(SGSUser user);

	public ChannelID channelID();
	
	public String getName();
	
	

}
