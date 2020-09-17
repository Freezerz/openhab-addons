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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.pelletburnermonitor.internal.controller.Options;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMException;

/**
 * Superclass for requests to burner.
 * Override method setRequestType in subclass to set request data based on the provided request type.
 * Implement method getData in subclass to return a byte array payload.
 *
 * @author Jonas Overgaard - Initial contribution
 *
 */
@NonNullByDefault
public abstract class Request {
    /**
     * Implement in subclass to build a byte array, based on the request type set in method setRequestType
     *
     * @return Byte array with the data to be sent to the burner
     * @throws PBMException
     */
    public abstract byte[] getData() throws PBMException;

    protected int requestType = -1;

    protected Options options;

    /**
     * Constructor
     *
     * @param options Options object containing configuration like remoteaddress
     */
    public Request(Options options) {
        this.options = options;
    }

    /**
     * Override in subclass to set request attributes appropriately, according to the provided request type
     *
     * @param requestType Integer specifying the request type
     */
    public void setRequestType(int requestType) {
        this.requestType = requestType;
    };

    /**
     * Provides the request type. The possible types are implemented in subclass of RequestType
     *
     * @return Integer representing the request type
     */
    public int getRequestType() {
        return this.requestType;
    }
}
