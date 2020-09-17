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
package org.openhab.binding.pelletburnermonitor.internal.controller;

import org.openhab.binding.pelletburnermonitor.internal.protocol.Protocol;
import org.openhab.binding.pelletburnermonitor.internal.protocol.Response;
import org.openhab.binding.pelletburnermonitor.internal.protocol.nbe.v13.b1005.Protocol_NBE_V13_1005;
import org.slf4j.Logger;

/**
 * Handles the connection to the burner.
 * Based on the provided protocol in the option, will connect to burner, and fetch the requested data
 *
 * @author Jonas Overgaard - Initial contribution
 *
 */
public class Connection {
    private static Connection connection;
    private Options options;
    private Protocol protocol;

    // TODO Figure a better way than a singleton usage of Connection class. Need to ensure somehow we're not trying to
    // contact the same remote address in two different connections
    /**
     * Singleton - we currently only want one connection active at a time. This will be an issue if trying to use two
     * burners at the same time
     *
     * @return
     */
    public static Connection getInstance() {
        if (connection == null) {
            connection = new Connection();
        }

        return connection;
    }

    /**
     * Instantiates the appropriate Protocol subclass, based on the provided option
     *
     * @param options Options provided by caller, such as protocol, remote address/port, serial and password
     * @throws PBMException
     */
    public void setOptions(Options options) throws PBMException {
        this.options = options;
        switch (options.getProtocol()) {
            case NBE_V13_1005:
                protocol = new Protocol_NBE_V13_1005(options);
                break;
            default:
                String errorMessage = "<none>";
                if (options.getProtocol() != null) {
                    errorMessage = options.getProtocol().toString();
                }
                throw new PBMException("Unknown protocol: " + errorMessage, PBMExceptionType.UNKNOWN_PROTOCOL);
        }
    }

    /**
     * Provides the logger to the Protocol class, in order to provide messages to log
     *
     * @param logger
     */
    public void setLogger(Logger logger) {
        protocol.setLogger(logger);
    }

    /**
     * Try to discover the burner. Works like pinging an IP
     *
     * @param retryOnFail If discover fails, retry or not?
     * @return True if the discover was successful
     * @throws PBMException
     */
    public boolean discoverBurner(boolean retryOnFail) throws PBMException {
        return protocol.discoverBurner(retryOnFail);
    }

    /**
     * Fetch all relevant data from the burner. Logic is implemented in Protocol subclass
     *
     * @return Response object containing the fetched data
     * @throws PBMException
     */
    public Response getAllData() throws PBMException {
        return protocol.getAllData();
    }

    /**
     * Try to discover the burner by using broadcasting.
     *
     * @return True if the discover was successful
     * @throws PBMException
     */
    public boolean discoverBurnerByBroadcast(boolean retryOnFail) throws PBMException {
        return protocol.discoverBurnerByBroadcast(retryOnFail);
    }

    /**
     * Returns the Options instance previously created
     *
     * @return Options instance containing settings like 'remoteaddress'
     */
    public Options getOptions() {
        return options;
    }
}
