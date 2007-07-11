/*
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved
 *
 * THIS PRODUCT CONTAINS CONFIDENTIAL INFORMATION AND TRADE SECRETS OF SUN
 * MICROSYSTEMS, INC. USE, DISCLOSURE OR REPRODUCTION IS PROHIBITED WITHOUT
 * THE PRIOR EXPRESS WRITTEN PERMISSION OF SUN MICROSYSTEMS, INC.
 */

/*
 * This file provides declarations relating to session contexts.
 */

#ifndef SGS_CONTEXT_H
#define SGS_CONTEXT_H 1

/*
 * sgs_context_impl provides the implementation for the sgs_context interface
 * (declare before any #includes)
 */
typedef struct sgs_context_impl sgs_context;

/*
 * INCLUDES
 */
#include "sgs_connection.h"
#include "sgs_channel.h"
#include "sgs_id.h"
#include "sgs_session.h"

/*
 * FUNCTION DECLARATIONS
 */

/*
 * function: sgs_ctx_free()
 *
 * Performs any necessary memory deallocations to dispose of an sgs_context.
 */
void sgs_ctx_free(sgs_context *ctx);

/*
 * function: sgs_ctx_new()
 *
 * Creates a new login context.  Returns null on failure.
 *
 * args:
 *  hostname: the hostname of the server to connect to
 *      port: the network port of the server to connect to
 *    reg_fd: a callback function used by a sgs_connection (when created with
 *            this sgs_context) to register interest in a file descriptor
 *  unreg_fd: a callback function used by a sgs_connection (when created with
 *            this sgs_context) to unregister interest in a file descriptor 
 *
 * arguments to reg_fd and unreg_fd:
 *   sgs_connection*: the connection making this callback
 *               int: a file descriptor
 *             short: events for which interest is being (un)registered for the
 *                    specified file descriptor
 *
 * Note that for compatibility with select(2) instead of just poll(2), the only
 * events that will be ever be specified in reg_fd and unreg_fd will be POLLIN,
 * POLLOUT, and/or POLLERR.
 */
sgs_context *sgs_ctx_new(const char *hostname, const int port,
    void (*reg_fd)(sgs_connection*, int, short),
    void (*unreg_fd)(sgs_connection*, int, short));

/*
 * function: sgs_ctx_set_channel_joined_cb()
 *
 * Registers a function to be called whenever a channel is joined.  The function
 * should take the following arguments:
 *   sgs_connection*: the connection making this callback
 *      sgs_channel*: the channel that was joined
 */
void sgs_ctx_set_channel_joined_cb(sgs_context *ctx,
    void (*callback)(sgs_connection*, sgs_channel*));

/*
 * function: sgs_ctx_set_channel_left_cb()
 *
 * Registers a function to be called whenever a channel is joined.  The function
 * should take the following arguments:
 *   sgs_connection*: the connection making this callback
 *      sgs_channel*: the channel that was left
 */
void sgs_ctx_set_channel_left_cb(sgs_context *ctx,
    void (*callback)(sgs_connection*, sgs_channel*));

/*
 * function: sgs_ctx_set_channel_recv_msg_cb()
 *
 * Registers a function to be called whenever a message is received on a
 * channel.  The function should take the following arguments:
 *   sgs_connection*: the connection making this callback
 *      sgs_channel*: the channel on which the message was received
 *     const sgs_id*: the ID of the sender of the message
 *    const uint8_t*: the received message (not null-terminated)
 *            size_t: the length of the received message
 */
void sgs_ctx_set_channel_recv_msg_cb(sgs_context *ctx,
    void (*callback)(sgs_connection*, sgs_channel*, const sgs_id*,
        const uint8_t*, size_t));

/*
 * function: sgs_ctx_set_disconnected_cb()
 *
 * Registers a function to be called whenever the connection is disconnected.
 * The function should take the following arguments:
 *   sgs_connection: the connection making this callback
 */
void sgs_ctx_set_disconnected_cb(sgs_context *ctx,
    void (*callback)(sgs_connection*));

/*
 * function: sgs_ctx_set_logged_in_cb()
 *
 * Registers a function to be called whenever a session login completes with the
 * server.  The function should take the following arguments:
 *   sgs_connection: the connection making this callback
 *      sgs_session: a handle for making method calls on the session
 */
void sgs_ctx_set_logged_in_cb(sgs_context *ctx,
    void (*callback)(sgs_connection*, sgs_session*));

/*
 * function: sgs_ctx_set_login_failed_cb()
 *
 * Registers a function to be called whenever a login request fails.  The
 * function should take the following arguments:
 *   sgs_connection: the connection making this callback
 *   const uint8_t*: an explanatory message from the server (not \0-terminated)
 *           size_t: the length of the message from the server
 */
void sgs_ctx_set_login_failed_cb(sgs_context *ctx,
    void (*callback)(sgs_connection*, const uint8_t*, size_t));

/*
 * function: sgs_ctx_set_reconnected_cb()
 *
 * Registers a function to be called whenever the session with the server is
 * reconnected after losing connection.  The function should take the following
 * arguments:
 *   sgs_connection: the connection making this callback
 */
void sgs_ctx_set_reconnected_cb(sgs_context *ctx,
    void (*callback)(sgs_connection*));

/*
 * function: sgs_ctx_set_recv_msg_cb()
 *
 * Registers a function to be called whenever a message is received directly
 * from the server (i.e. not on a channel).  The function should take the
 * following arguments:
 *   sgs_connection: the connection making this callback
 *   const uint8_t*: the message sent by the server (not \0-terminated)
 *           size_t: the length of the message sent by the server
 */
void sgs_ctx_set_recv_msg_cb(sgs_context *ctx,
    void (*callback)(sgs_connection*, const uint8_t*, size_t));

/*
 * function: sgs_ctx_unset_all_cbs()
 *
 * Unregisters all event callback functions on the specified login context.
 */
void sgs_ctx_unset_all_cbs(sgs_context *ctx);

#endif  /** #ifndef SGS_CONTEXT_H */
