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

import java.util.Arrays;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMException;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMExceptionType;
import org.openhab.binding.pelletburnermonitor.internal.protocol.Response;

/**
 * The {@link Response_NBE_V13_1005} class defines the response for this specific protocol version.
 *
 * @author Jonas Overgaard - Initial contribution
 */
@NonNullByDefault
public class Response_NBE_V13_1005 extends Response {
    // FIXME The get* methods could be redone so that they take the actual byte indices as input, instead of the current
    // code which takes startindex and endindex+1
    // FIXME The fetching of data could probably be done differently, as currently, if an index needs to be changed, a
    // lot of lines below needs to be updated as well

    /**
     * Returns the appID
     *
     * @return
     * @throws PBMException
     */
    public byte[] getAppId() throws PBMException {
        return getRawDataPart(0, 12);
    }

    /**
     * Returns the serial of the burner
     *
     * @return
     * @throws PBMException
     */
    public byte[] getSerial() throws PBMException {
        return getRawDataPart(12, 18);
    }

    /**
     * Returns the start byte. Specifies the start of the response
     *
     * @return
     * @throws PBMException
     */
    public byte getStartByte() throws PBMException {
        return getRawDataPartSingleByte(18);
        // FIXME result may be null, thus creating a dump when trying to access index 0
    }

    /**
     * Returns the function code. Depends on the contents in the payload
     *
     * @return
     * @throws PBMException
     */
    public byte[] getFunctionCode() throws PBMException {
        return getRawDataPart(19, 21);
    }

    /**
     * Returns the sequence number of the response. Used to keep track of requests/responses
     *
     * @return
     * @throws PBMException
     */
    public byte[] getSequenceNumber() throws PBMException {
        return getRawDataPart(21, 23);
    }

    /**
     * Returns the reponse code. Used as a status/error code
     *
     * @return
     * @throws PBMException
     */
    public byte[] getResponseCode() throws PBMException {
        return getRawDataPart(23, 24);
    }

    /**
     * Returns the size of the payload
     *
     * @return
     * @throws PBMException
     */
    private int getPayloadSize() throws PBMException {
        byte[] payloadSize = getRawDataPart(24, 27);
        String payloadSizeAsString = new String(payloadSize);
        return Integer.valueOf(payloadSizeAsString);
    }

    /**
     * Returns the end of the payload. Used for getting {@link #getPayload()} and for determining position of
     * {@link #getEndByte()}
     *
     * @return
     * @throws PBMException
     */
    private int getPayloadEnd() throws PBMException {
        return 27 + getPayloadSize();
    }

    /**
     * Returns the payload
     */
    @Override
    protected byte[] getPayload() throws PBMException {
        return getRawDataPart(27, getPayloadEnd());
    }

    /**
     * Returns the end byte of the response
     *
     * @return
     * @throws PBMException
     */
    public byte getEndByte() throws PBMException {
        int endBytePosition = getPayloadEnd();
        return getRawDataPartSingleByte(endBytePosition);
        // FIXME result may be null, thus creating a dump when trying to access index 0
    }

    /**
     * Returns a specific part of the response
     *
     * @param startIndex Offset (first byte to include in output)
     * @param endIndex End offset+1 (byte after the last one to include in output)
     * @return
     * @throws PBMException
     */
    private byte[] getRawDataPart(int startIndex, int endIndex) throws PBMException {
        // FIXME Rewrite this method to be more logical in it's usage. When seen from the callers it looks strange, as
        // endIndex is one too high. I.e. getRawDataPart(0,2) should return the first three bytes, not the first two as
        // it currently does
        byte[] dataPart = new byte[0];
        try {
            dataPart = Arrays.copyOfRange(rawData, startIndex, endIndex);
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PBMException("Unable to access data part between " + startIndex + "-" + endIndex,
                    PBMExceptionType.ERROR_ACCESSING_DATA, e);
        }
        return dataPart;
    }

    /**
     * Returns the single byte from the response at the offset specified
     *
     * @param index
     * @return
     * @throws PBMException
     */
    private byte getRawDataPartSingleByte(int index) throws PBMException {
        return getRawDataPart(index, index + 1)[0];
    }

    /**
     * Returns the burner state
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_OPERATING_DATA
     */
    @Override
    public String getBurnerState() {
        return getItemValueByID("state");
    }

    /**
     * Returns the burner substate
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_OPERATING_DATA
     */
    @Override
    public String getBurnerSubState() {
        return getItemValueByID("substate");
    }

    /**
     * Returns the silo contents
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_OPERATING_DATA
     */
    @Override
    public String getSiloContents() {
        return getItemValueByID("content");
    }

    /**
     * Returns the current temperature
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_OPERATING_DATA
     */
    @Override
    public String getTemperatureCurrent() {
        return getItemValueByID("boiler_temp");
    }

    /**
     * Returns the current power output as a percentage of running at full power
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_OPERATING_DATA
     */
    @Override
    public String getPowerOutputPercentage() {
        return getItemValueByID("power_pct");
    }

    /**
     * Returns the current power output in kW
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_OPERATING_DATA
     */
    @Override
    public String getPowerOutputKilowatts() {
        return getItemValueByID("power_kw");
    }

    /**
     * Returns the current time of the burner
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_OPERATING_DATA
     */
    @Override
    public String getCurrentTime() {
        return getItemValueByID("time");
    }

    /**
     * Returns the target temperature
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_ADVANCED_DATA
     */
    @Override
    public String getTemperatureTarget() {
        return getItemValueByID("boiler_setpoint");
    }

    /**
     * Returns the silo warning limit
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_HOPPER
     */
    @Override
    public String getSiloMinimumContents() {
        return getItemValueByID("min_content");
    }

    /**
     * Returns the auger consumption
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_HOPPER
     */
    @Override
    public String getAugerConsumption() {
        return getItemValueByID("auger_consumption");
    }

    /**
     * Returns the limit above the target temperature on which the burner stops
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_BOILER
     */
    @Override
    public String getTemperatureLimitAbove() {
        return getItemValueByID("diff_over");
    }

    /**
     * Returns the limit below the target temperature on which the burner starts
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_BOILER
     */
    @Override
    public String getTemperatureLimitBelow() {
        return getItemValueByID("diff_under");
    }

    /**
     * Returns the counter for remaining silo contents before cleaning of ashtray is necessary
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_CLEANING
     */
    @Override
    public String getCleaningCountdown() {
        return getItemValueByID("trip_countdown");
    }

    /**
     * Returns the consumption of the full previous hour.
     * I.e. if time is 23:15, the output is for the period 22:00-22:59
     * Provided by RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_HOURS
     */
    @Override
    public String getConsumptionPreviousHour() throws PBMException {
        String consumption;
        try {
            // Determine the hour before the current one. This is relative to the time of
            // the boiler, not the actual time.
            String currentTime = getCurrentTime();
            String currentHour = currentTime.substring(9, 11);
            int previousHour = Integer.valueOf(currentHour) - 1;
            // If we get below 0, it means the time is between 0:00-0:59 and we should wrap
            // around to hour 23
            if (previousHour < 0) {
                previousHour = 23;
            }

            // Build the label for the specific hour
            String dataItem = "total_hours" + previousHour;
            consumption = getItemValueByID(dataItem);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new PBMException("PBM: Unable to determine time of day, for usage in retrieval of consumption",
                    PBMExceptionType.UNABLE_TO_DETERMINE_TIME_OF_DAY, e);
        }
        return consumption;
    }
}
