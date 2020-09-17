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
package org.openhab.binding.pelletburnermonitor.internal.protocol.nbe.v13.b1005;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.pelletburnermonitor.internal.controller.Options;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMException;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMExceptionType;
import org.openhab.binding.pelletburnermonitor.internal.protocol.Request;
import org.openhab.binding.pelletburnermonitor.internal.protocol.RequestType;

/**
 * The {@link Request_NBE_V13_1005} class defines the request for this specific protocol version.
 *
 * @author Jonas Overgaard - Initial contribution
 */
@NonNullByDefault
public class Request_NBE_V13_1005 extends Request {
    private final String appId = "DeliciousABC"; // 12 bytes
    private @Nullable String serial; // 6 bytes
    private String encryption = " "; // 2 bytes
    private final byte STARTBYTE = 0x02; // 1 byte
    private @Nullable String functionCode; // 2 bytes
    private final String sequenceNumber = "42"; // 2 bytes
    private @Nullable String password; // 10 bytes
    private String timestamp; // 10 bytes
    private String extra = "extr"; // 4 bytes
    private @Nullable String payloadSize; // 3 bytes
    private @Nullable String payload; // 13 bytes
    private final byte ENDBYTE = 0x04; // 1 byte

    /**
     * Constructor
     *
     * @param options object containing configuration like remoteaddress
     */
    public Request_NBE_V13_1005(Options options) {
        super(options);
        setSerial(options.getSerial());
        setPassword(options.getPassword());
        timestamp = generateTimestamp();
    }

    /**
     * Returns the request data as a byte array. Used when sending request to remotehost
     */
    @Override
    public byte[] getData() throws PBMException {
        byte[] resultData = null;
        byte[] partOne = (appId + serial + encryption).getBytes();
        byte[] partTwo = (functionCode + sequenceNumber + password + timestamp + extra + payloadSize + payload)
                .getBytes();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            byteStream.write(partOne);
            byteStream.write(STARTBYTE);
            byteStream.write(partTwo);
            byteStream.write(ENDBYTE);
            // FIXME When implementing discovery by broadcast, this method would need some rewrite based on:
            // if (requestType == RequestType.REQUEST_TYPE_DISCOVERY_BROADCAST) {
            // byte[] sender1 = ":192.168.1.6:8483".getBytes();
            // byte[] sender2 = new byte[] { (byte) 0x04 };
            // byteStream.write(sender1);
            // byteStream.write(sender2);
            // }
            resultData = byteStream.toByteArray();
        } catch (IOException e) {
            throw new PBMException("Error building request", PBMExceptionType.INVALID_REQUEST, e);
        }

        return resultData;
    }

    /**
     * Returns a current time as a timestamp
     *
     * @return
     */
    private String generateTimestamp() {
        long currentTime = System.currentTimeMillis() / 1000;
        return Long.toString(currentTime);
    }

    /**
     * Sets the serial of the remotehost
     *
     * @param serial Six digits
     */
    private void setSerial(String serial) {
        this.serial = String.format("%6.6s", serial).replace(' ', '0');
    }

    /**
     * Sets the password for accessing remotehost
     *
     * @param password 10 characters
     */
    private void setPassword(String password) {
        this.password = String.format("%10.10s", password).replace(' ', '0');
    }

    /**
     * Returns appID as a byte array
     *
     * @return
     */
    public byte[] getAppId() {
        return appId.getBytes();
    }

    /**
     * Returns serial
     *
     * @return
     */
    @SuppressWarnings("null")
    public byte[] getSerial() {
        return serial.getBytes();
    }

    /**
     * Returns the encryption of the request
     *
     * @return Default is no encryption
     */
    public byte getEncryption() {
        return encryption.getBytes()[0];
    }

    /**
     * Returns the function code of the request
     *
     * @return
     */
    @SuppressWarnings("null")
    public byte[] getFunctionCode() {
        return functionCode.getBytes();
    }

    /**
     * Returns the sequence number of the request. Used to keep track of the requests/responses
     *
     * @return
     */
    public byte[] getSequenceNumber() {
        return sequenceNumber.getBytes();
    }

    /**
     * Returns the password for accessing remote host
     *
     * @return
     */
    @SuppressWarnings("null")
    public byte[] getPassword() {
        return password.getBytes();
    }

    /**
     * Returns the timestamp
     *
     * @return
     */
    public byte[] getTimestamp() {
        return timestamp.getBytes();
    }

    /**
     * Returns the extra bytes. Not used for anything AFAIK
     *
     * @return
     */
    public byte[] getExtra() {
        return extra.getBytes();
    }

    /**
     * Returns the start byte. Specifies the start of the request
     *
     * @return
     */
    public byte getStartByte() {
        return STARTBYTE;
    }

    /**
     * Returns the end byte. Specifies the end of the request
     *
     * @return
     */
    public byte getEndByte() {
        return ENDBYTE;
    }

    /**
     * Sets the request type, function code, payload and payload size based on input
     */
    @Override
    public void setRequestType(int requestType) {
        super.setRequestType(requestType);
        switch (requestType) {
            case RequestType.REQUEST_TYPE_DISCOVERY:
                functionCode = "00";
                payload = "NBE_DISCOVERY";
                setPayloadSize();
                break;
            case RequestType.REQUEST_TYPE_DISCOVERY_BROADCAST:
                functionCode = "00";
                payload = "NBE Discovery";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_OPERATING_DATA:
                functionCode = "04";
                payload = "*";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_ADVANCED_DATA:
                functionCode = "05";
                payload = "*";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_HOURS:
                functionCode = "06";
                payload = "total_hours";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_DAYS:
                functionCode = "06";
                payload = "total_days";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_MONTHS:
                functionCode = "06";
                payload = "total_months";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_YEARS:
                functionCode = "06";
                payload = "total_years";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_BOILER:
                functionCode = "01";
                payload = "boiler.*";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_CLEANING:
                functionCode = "01";
                payload = "cleaning.*";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_HOPPER:
                functionCode = "01";
                payload = "hopper.*";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_MISC:
                functionCode = "01";
                payload = "misc.*";
                setPayloadSize();
                break;
            case RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_ALARM:
                functionCode = "01";
                payload = "alarm.*";
                setPayloadSize();
                break;
            default:
                break;
        }
    }

    /**
     * Sets the size of the payload
     */
    @SuppressWarnings("null")
    private void setPayloadSize() {
        String payloadSizeAsString = Integer.toString(payload.length());
        payloadSize = String.format("%3.3s", payloadSizeAsString).replace(' ', '0');
    }
}
