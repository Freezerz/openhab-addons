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

import org.openhab.binding.pelletburnermonitor.internal.protocol.ImplementedProtocol;

/**
 * Settings used to determine how to connect to the burner, such as protocol, remote address/port, serial and password
 *
 * @author Jonas Overgaard - Initial contribution
 *
 */
public class Options {
    private final String DEFAULT_LOCALHOST = "0.0.0.0";
    private String localAddress;
    private int localPort;
    private String remoteAddress;
    private int remotePort;
    private ImplementedProtocol protocol;
    private String serial;
    private String password;

    /**
     * Option object keeps track of the various options for connecting to a burner.
     * See the individual methods for further description of these attributes
     *
     * @param localAddress Local host
     * @param localPort Local port
     * @param remoteAddress Remote host
     * @param remotePort Remote port
     * @param protocol Protocol
     * @param serial Serial
     * @param password Password
     */
    public Options(String localAddress, int localPort, String remoteAddress, int remotePort,
            ImplementedProtocol protocol, String serial, String password) {
        setLocalAddress(localAddress);
        setLocalPort(localPort);
        setRemoteAddress(remoteAddress);
        setRemotePort(remotePort);
        setProtocol(protocol);
        setSerial(serial);
        setPassword(password);
    }

    /**
     * Address of local host. If left empty or corresponds to a variant of "localhost", use the default localhost
     * designation
     * A specific network interface IP can be used i.e. 192.168.0.4. Useful if local host has multiple IPs
     *
     * @return Address of local host
     */
    public String getLocalAddress() {
        if (localAddress == null || localAddress.equals("") || localAddress.equalsIgnoreCase("127.0.0.1")
                || localAddress.equalsIgnoreCase(("localhost"))) {
            return DEFAULT_LOCALHOST;
        } else {
            return localAddress;
        }
    }

    /**
     * Sets the address of the local host. Can be omitted (see description of method getLocalAddress
     *
     * @param localAddress Either IP or hostname, as a string
     */
    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    /**
     * Provides the specified port to be used on the local host
     *
     * @return Portnumber as an integer
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * Sets the port to be used on the local host
     *
     * @param localPort Port number as an integer
     */
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    /**
     * Provides the remote address
     *
     * @return Remote address as a string
     */
    public String getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * Sets the remote host.
     *
     * @param remoteAddress String specifying the remote host either as hostname or IP
     */
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * Provides the remote port number.
     *
     * @return Remote port number as integer
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     * Sets the remote port number. Current limit is 9999 because of NBE_V13_1005 protocol limitations
     *
     * @param remotePort Integer specifying the remote port number
     */
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * Provides the protocol being used. Available ones are present in enumerable ImplementedProtocol
     *
     * @return ImplementedProtocol enumerable specifying the protocol
     */
    public ImplementedProtocol getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol to be used. See description of method getProtocol
     *
     * @param protocol ImplementedProtocol enumerable
     */
    public void setProtocol(ImplementedProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * Provides the serial of the burner
     *
     * @return Serial of the burner as a string
     */
    public String getSerial() {
        return serial;
    }

    /**
     * Sets the serial of the burner
     *
     * @param serial String specifying the serial of the burner
     */
    public void setSerial(String serial) {
        this.serial = serial;
    }

    /**
     * Provides the password of the burner. Not required on NBE_V13_1005
     *
     * @return String specifying the password of the burner
     */
    public String getPassword() {
        if (password == null) {
            return "";
        } else {
            return password;
        }
    }

    /**
     * Sets the password of the burner. See descriptions of method getPassword
     *
     * @param password String specifying the password of the burner
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
