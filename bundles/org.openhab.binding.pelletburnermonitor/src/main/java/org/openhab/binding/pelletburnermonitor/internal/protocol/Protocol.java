/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.pelletburnermonitor.internal.protocol;

import static java.lang.Thread.sleep;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.pelletburnermonitor.internal.controller.Options;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMException;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMExceptionType;
import org.slf4j.Logger;

/**
 * Superclass for protocols for different burner versions. Implement methods getRequest and getResponse in subclass to
 * set attributes <b>request</b>
 * and <b>response</b>
 *
 * @author Jonas Overgaard - Initial contribution
 *
 */
public abstract class Protocol {
    private static final int CONNECTION_TIMEOUT_IN_MILLISECONDS = 3000;
    private static final int PORT_LIMIT = 9999;
    protected Request request;
    protected Response response;
    protected Options options;
    protected Logger logger;

    /**
     * Implement this method in subclass, to add an instance of the subclass to the attribute <b>request</b>
     *
     * @return Request object
     */
    protected abstract Request getRequest();

    /**
     * Implement this method in subclass, to add an instance of the subclass to the attribute <b>response</b>
     *
     * @return Response object
     */
    protected abstract Response getResponse();

    /**
     * Implement this in subclass, to do the discovery of the remotehost
     *
     * @param retryOnFail If true, a failed connection attempt will be retried
     * @return True if discovery was successful
     * @throws PBMException
     */
    public abstract boolean discoverBurner(boolean retryOnFail) throws PBMException;

    /**
     * Implement this in subclass, to do the discovery of the remotehost using broadcasting
     *
     * @param retryOnFail
     * @return
     * @throws PBMException
     */
    public abstract boolean discoverBurnerByBroadcast(boolean retryOnFail) throws PBMException;

    /**
     * Implement in subclass to retrieve all relevant data from burner
     *
     * @return Response object containing all data retrieved as specified in subclass
     * @throws PBMException
     */
    public abstract Response getAllData() throws PBMException;

    /**
     * Constructor
     *
     * @param options Options object containing the configuration like remoteaddress
     */
    public Protocol(Options options) {
        this.options = options;
    }

    /**
     * Stores the logger in Protocol, so that it can be used to add log entries in the methods of this class and it's
     * subclasses. Useful for adding debug log entries
     *
     * @param logger
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * This method is used by {@link #discoverBurner(boolean)} and {@link #discoverBurnerByBroadcast(boolean)}
     *
     * @param requestType
     * @param retryOnFail
     * @return
     * @throws PBMException
     */
    protected boolean discoverBurnerByRequestType(int requestType, boolean retryOnFail) throws PBMException {
        getRequest().setRequestType(requestType);
        Response response = sendAndRecieve();

        // In case of failure, and it is allowed to retry, try contacting the server again
        if (!response.isValidResponse() && retryOnFail) {
            response = sendAndRecieve();
        }
        return response.isValidResponse();
    }

    /**
     * Will send a request to the burner, retrieve a response, validate it and finally parse it
     *
     * @return Response object containing the parsed data provided by burner
     * @throws PBMException
     */
    protected Response sendAndRecieve() throws PBMException {
        // Wait for some time before sending request, to avoid a timeout because of spamming the remote host
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new PBMException("Someone woke me during my sleep", PBMExceptionType.SLEEP_PROBLEM, e);
        }

        // Initialize request and response - created in their respective subclasses of classes Request and Response
        request = getRequest();
        response = getResponse();

        response.setRawData(new byte[0]); // Clear data from previous call

        byte[] requestData = request.getData();

        DatagramSocket clientSocket = bindToLocalPort(options);

        if (clientSocket != null && clientSocket.isBound()) {
            // Send packet to remote host/port
            try {
                InetAddress remoteAddress = InetAddress.getByName(options.getRemoteAddress());

                DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, remoteAddress,
                        options.getRemotePort());

                clientSocket.send(requestPacket);
            } catch (UnknownHostException e) {
                clientSocket.close(); // Ensure socket is closed
                throw new PBMException("Unknown remote address " + options.getRemoteAddress(),
                        PBMExceptionType.UNKNOWN_REMOTE_HOST, e);
            } catch (IOException e) {
                clientSocket.close(); // Ensure socket is closed
                throw new PBMException("Error sending request", PBMExceptionType.ERROR_SENDING, e);
            }

            // Retrieve response
            byte[] responseData = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
            try {
                clientSocket.receive(responsePacket);
            } catch (SocketTimeoutException e) {
                clientSocket.close(); // Ensure socket is closed
                throw new PBMException("Error receiving response because of timeout", PBMExceptionType.TIMEOUT, e);
            } catch (IOException e) {
                clientSocket.close(); // Ensure socket is closed
                throw new PBMException("Error receiving response", PBMExceptionType.ERROR_RECEIVING, e);
            }

            // Close socket
            clientSocket.close();

            // Save, validate and parse the response
            response.setRawData(responsePacket.getData());
            if (validateResponse()) {
                parseResponsePayload();
            } else {
                throw new PBMException("Invalid response", PBMExceptionType.INVALID_RESPONSE);
            }
        } else {
            throw new PBMException("Unable to use local port", PBMExceptionType.UNABLE_TO_BIND_LOCAL_PORT);
        }

        return getResponse();
    }

    // FIXME This bindToLocalPort method is too NBE specific regarding the port usage of only the four-digits ones. It
    // should either be rewritten more generically (like moving the while part into the subclass, and calling this super
    // method from there, catching SocketException and then retrying) or simply made abstract and the current code moved
    // to the subclass
    /**
     * Based on <b>options</b>, will start listening on UDP address and port.
     * If requested portnumber is unavailable, the portnumber will be increased until an available port is found, or the
     * limit of port 9999 is reached
     *
     * @param options
     * @return DatagramSocket object which will be used for the sending and recieving of data
     * @throws PBMException
     */
    private @Nullable DatagramSocket bindToLocalPort(Options options) throws PBMException {
        DatagramSocket clientSocket = null; // FIXME How do I avoid the @nullable on this method?
        int initialLocalPort = options.getLocalPort();

        boolean retryBinding = true;
        while (retryBinding) {
            try {
                // Bind to local host/port
                InetAddress localAddress = InetAddress.getByName(options.getLocalAddress());
                clientSocket = new DatagramSocket(options.getLocalPort(), localAddress);

                // Set timeout to 3 seconds, disable broadcast and reuse
                clientSocket.setSoTimeout(CONNECTION_TIMEOUT_IN_MILLISECONDS);
                clientSocket.setBroadcast(false);
                clientSocket.setReuseAddress(false);

                // Success - exit loop
                retryBinding = false;

            } catch (SocketException e) {
                // Try ports from the specified port number to 9999, as five digit ports doesn't work with NBE_V13_1005
                // firmware
                int currentLocalPort = options.getLocalPort();
                if (currentLocalPort < PORT_LIMIT) {
                    options.setLocalPort(currentLocalPort + 1);
                } else {
                    retryBinding = false;
                    throw new PBMException(
                            "Unable to use local port between " + initialLocalPort + " and " + currentLocalPort,
                            PBMExceptionType.UNABLE_TO_BIND_LOCAL_PORT, e);
                }
            } catch (UnknownHostException e) {
                throw new PBMException("Unknown local address " + options.getLocalAddress(),
                        PBMExceptionType.UNKNOWN_LOCAL_HOST, e);
            }
        }
        return clientSocket;
    }

    /**
     * Implement in subclass to determine if the response received from burner is valid
     *
     * @return True if valid response
     * @throws PBMException
     */
    protected abstract boolean validateResponse() throws PBMException;

    /**
     * Implement in subclass to parse response payload into ResponseItem objects
     *
     * @throws PBMException
     */
    protected abstract void parseResponsePayload() throws PBMException;
}
