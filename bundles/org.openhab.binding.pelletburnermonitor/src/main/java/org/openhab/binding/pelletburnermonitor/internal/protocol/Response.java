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

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMException;

/**
 * Superclass for responses from burner.
 * Implement parseRawData in subclass to generate ResponseItems from response data.
 *
 * @author Jonas Overgaard - Initial contribution
 *
 */
@NonNullByDefault
public abstract class Response {
    protected byte[] rawData = new byte[0];
    protected ArrayList<ResponseItem> parameterizedResponse = new ArrayList<ResponseItem>();
    protected boolean isValidResponse = false;
    protected int alarmCode = -1;
    protected String alarmText = "";

    /**
     * Load the raw data, received from burner
     *
     * @param rawData Byte array containing the data received
     */
    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    /**
     * Generates an output string from the parsed response, suitable for output to i.e. console
     */
    @Override
    public String toString() {
        String result = "";
        for (ResponseItem ri : parameterizedResponse) {
            result += ri.toString() + "\n";
        }
        return result;
    }

    /**
     * Returns the validity of the response
     *
     * @return True if response is valid
     */
    public boolean isValidResponse() {
        return isValidResponse;
    }

    /**
     * Sets the validity of the response. This is set in Protocol subclass in method parseResponsePayload
     *
     * @param validity True if response is valid, false if response is not valid
     */
    public void setValidity(boolean validity) {
        isValidResponse = validity;
    }

    /**
     * Parse the payload from the reponse into ResponseItem objects and add to parameterizedResponse arraylist
     * Example of raw data could be id1=value1;id2=value2;id3=value3;
     *
     * @param requestType The type as specified from subclass of class RequestType
     * @param fieldSeparator The separator between items. <b>;</b> in the example
     * @param valuepairSeparator The separator between id and value. <b>=</b> in the example
     * @return True if the splitting of the payload was successful
     * @throws PBMException
     */
    public boolean payloadSplitValuePairs(int requestType, String fieldSeparator, String valuepairSeparator)
            throws PBMException {
        byte[] payloadAsBytes = getPayload();
        if (payloadAsBytes.length > 0) {
            String[] fields = new String(payloadAsBytes).split(fieldSeparator);
            String[] splitPair = null;
            for (String item : fields) {
                splitPair = item.split(valuepairSeparator);
                // FIXME Handle errors when no field separator (;) or no pair separator (=)
                ResponseItem responseItem = new ResponseItem(Integer.toString(requestType), splitPair[0], splitPair[1]);
                parameterizedResponse.add(responseItem);
            }

        }
        if (parameterizedResponse.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Parse the payload from the reponse into ResponseItem objects and add to parameterizedResponse arraylist
     * Example of raw data could be id1=value1,value2,value3;
     *
     * @param requestType
     * @param valuepairSeparator
     * @param listSeparator
     * @return
     * @throws PBMException
     */
    public boolean payloadSplitList(int requestType, String valuepairSeparator, String listSeparator)
            throws PBMException {
        byte[] payloadAsBytes = getPayload();
        if (payloadAsBytes.length > 0) {
            String[] fields = new String(payloadAsBytes).split(valuepairSeparator);
            // String[] splitPair = null;
            if (fields.length == 2) {
                String[] list = fields[1].split(listSeparator);
                int index = 0;
                for (String item : list) {
                    // splitPair = item.split(valuepairSeparator);
                    // FIXME Handle errors when no field separator (;) or no pair separator (=)
                    ResponseItem responseItem = new ResponseItem(Integer.toString(requestType),
                            fields[0] + Integer.toString(index), item);
                    parameterizedResponse.add(responseItem);
                    index += 1;
                }
            } else {
                return false;
            }
        }
        if (parameterizedResponse.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Implement in subclass to return the payload of the response
     *
     * @return Byte array containing the response payload
     * @throws PBMException
     */
    protected abstract byte[] getPayload() throws PBMException;

    /**
     * Returns a string containing the value of the specified item ID.
     * This is used in the subclasses to return the appropriate values
     *
     * @param itemID ID of the item as text
     * @return Value of that item
     */
    protected String getItemValueByID(String itemID) {
        String value = "";
        for (ResponseItem item : parameterizedResponse) {
            if (item.getId().equalsIgnoreCase(itemID)) {
                value = item.getValue();
                break;
            }
        }
        return value;
    }

    /**
     * Returns the burner state
     *
     * @return
     */
    public abstract String getBurnerState();

    /**
     * Returns the burner substate
     *
     * @return
     */
    public abstract String getBurnerSubState();

    /**
     * Returns the silo contents
     *
     * @return
     */
    public abstract String getSiloContents();

    /**
     * Returns the silo contents warning limit
     *
     * @return
     */
    public abstract String getSiloMinimumContents();

    /**
     * Returns the auger consumption
     *
     * @return
     */
    public abstract String getAugerConsumption();

    /**
     * Returns the allowed degrees above the target temperature before burner stops
     *
     * @return
     */
    public abstract String getTemperatureLimitAbove();

    /**
     * Returns the allowed degrees below the target temperature before burner starts
     *
     * @return
     */
    public abstract String getTemperatureLimitBelow();

    /**
     * Returns the amount of pellets remaining until the ash tray should be cleaned
     *
     * @return
     */
    public abstract String getCleaningCountdown();

    /**
     * Returns the current temperature of the burner
     *
     * @return
     */
    public abstract String getTemperatureCurrent();

    /**
     * Returns the current power output of the burner in percentages
     *
     * @return
     */
    public abstract String getPowerOutputPercentage();

    /**
     * Returns the current power output of the burner in kW
     *
     * @return
     */
    public abstract String getPowerOutputKilowatts();

    /**
     * Returns the target temperature of the burner
     *
     * @return
     */
    public abstract String getTemperatureTarget();

    /**
     * Returns the current time of the burner
     *
     * @return
     */
    public abstract String getCurrentTime();

    /**
     * Returns the consumption of pellets in the previous full hour
     *
     * @return
     */
    public abstract String getConsumptionPreviousHour() throws PBMException;

    /**
     * Returns the alarm code.
     * 
     * @return
     */
    public int getAlarmCode() {
        return alarmCode;
    }

    /**
     * Return the alarm text. This is the textual representation of the {@link #alarmCode}
     * 
     * @return
     */
    public String getAlarmText() {
        return alarmText;
    }

    /**
     * Sets the alarm code. For NBE this is generated based on {@link #getBurnerState()} and
     * {@link #getBurnerSubState()}
     * 
     * @param alarmCode
     */
    public void setAlarmCode(int alarmCode) {
        this.alarmCode = alarmCode;
    }

    /**
     * Sets the alarm text. This is the textual representation of the {@link #alarmCode}
     * 
     * @param alarmText
     */
    public void setAlarmText(String alarmText) {
        this.alarmText = alarmText;
    }
}
