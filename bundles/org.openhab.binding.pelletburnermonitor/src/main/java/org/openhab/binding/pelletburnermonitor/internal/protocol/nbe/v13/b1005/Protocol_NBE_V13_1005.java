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
import org.openhab.binding.pelletburnermonitor.internal.controller.Options;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMException;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMExceptionType;
import org.openhab.binding.pelletburnermonitor.internal.protocol.Protocol;
import org.openhab.binding.pelletburnermonitor.internal.protocol.Request;
import org.openhab.binding.pelletburnermonitor.internal.protocol.RequestType;
import org.openhab.binding.pelletburnermonitor.internal.protocol.Response;

/**
 * === Protocol information ===
 * Don't know where else to put this, so for now it's put here:
 * 1. This version of the burner software will not work with a local port > 9999
 */

/**
 * The {@link Protocol_NBE_V13_1005} class defines the specific protocol version.
 *
 * @author Jonas Overgaard - Initial contribution
 */
@NonNullByDefault
public class Protocol_NBE_V13_1005 extends Protocol {
    private final String PAYLOAD_ITEM_SEPARATOR = ";";
    private final String PAYLOAD_VALUEPAIR_SEPARATOR = "=";
    private final String PAYLOAD_LIST_SEPARATOR = ",";

    public Protocol_NBE_V13_1005(Options options) {
        super(options);
    }

    /**
     * Singleton
     */
    @Override
    protected Request getRequest() {
        if (request == null) {
            // FIXME Request should never be reused?
            request = new Request_NBE_V13_1005(options);
        }
        return request;
    }

    /**
     * Singleton
     */
    @Override
    protected Response getResponse(boolean newResponse) {
        if (response == null || newResponse) {
            response = new Response_NBE_V13_1005();
        }
        return response;
    }

    /**
     * Will retry sending request and receiving response in case of a timeout
     *
     * @param datatype
     * @return
     * @throws PBMException
     */
    private Response getDataAndRetryOnTimeout(String datatype) throws PBMException {
        Response response = getResponse(false);
        final int MAX_RETRIES_ON_TIMEOUT = 4;

        // Try up to four times when TIMEOUT
        for (int i = 0; i < MAX_RETRIES_ON_TIMEOUT; i++) {
            try {
                response = sendAndRecieve();
                return response;
            } catch (PBMException e) {
                // In case of the exception being a timeout, retry. Otherwise reraise the exception as something could
                // be wrong with the configuration etc.
                if (e.getErrorCode() == PBMExceptionType.TIMEOUT) {
                    if (i < MAX_RETRIES_ON_TIMEOUT) {
                        logger.debug("PBM: Retry fetching '{}' data because of timeout", datatype);
                    }
                    // response = sendAndRecieve();
                } else {
                    logger.debug("PBM: Error fetching {} data: {}", datatype, e.getErrorCode());
                    throw e;
                }
            }
        }
        return response;
    }

    /**
     * Fetches all data needed for the Thing channels to be updated
     */
    @Override
    public Response getAllData() throws PBMException {
        // FIXME This could/should be done more intelligently. Perhaps it's possible to figure out which channels are
        // being used, and only fetch the data relevant for these channels?

        // FIXME Quick fix to avoid subsequent calls to getAllData to keep the responses from the previous calls, as
        // that would make the responseitem list grow, and the same data would be reported to the channels as it's only
        // the first value for each channel that's used
        // Request request = getRequest();
        // Response response = getResponse();
        request = getRequest();
        response = getResponse(true);

        // FIXME The below commented request types are not currently used in the binding (there's no channels for them),
        // so no need to fetch the data.
        // When needed just uncomment the lines, as the fetching of the data works fine

        request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_OPERATING_DATA);
        getDataAndRetryOnTimeout("operating");

        request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_ADVANCED_DATA);
        response = getDataAndRetryOnTimeout("advanced");

        request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_HOURS);
        response = getDataAndRetryOnTimeout("consumption hours");

        // request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_DAYS);
        // response = getDataAndRetryOnTimeout("consumption days");
        //
        // request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_MONTHS);
        // response = getDataAndRetryOnTimeout("consumption months");
        //
        // request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_YEARS);
        // response = getDataAndRetryOnTimeout("consumption years");

        request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_HOPPER);
        response = getDataAndRetryOnTimeout("settings hopper");

        request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_BOILER);
        response = getDataAndRetryOnTimeout("settings boiler");

        request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_CLEANING);
        response = getDataAndRetryOnTimeout("settings cleaning");

        // request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_MISC);
        // response = getDataAndRetryOnTimeout("settings misc");
        //
        // request.setRequestType(RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_ALARM);
        // response = getDataAndRetryOnTimeout("settings alarm");

        return response;
    }

    /**
     * Validates the reponse - a lot of the request and the response must be identical
     */
    @Override
    protected boolean validateResponse() throws PBMException {
        byte[] requestAppId = ((Request_NBE_V13_1005) request).getAppId();
        byte[] responseAppId = ((Response_NBE_V13_1005) response).getAppId();
        byte[] requestSerial = ((Request_NBE_V13_1005) request).getSerial();
        byte[] responseSerial = ((Response_NBE_V13_1005) response).getSerial();
        byte requestStartByte = ((Request_NBE_V13_1005) request).getStartByte();
        byte responseStartByte = ((Response_NBE_V13_1005) response).getStartByte();
        byte[] requestFunctionCode = ((Request_NBE_V13_1005) request).getFunctionCode();
        byte[] responseFunctionCode = ((Response_NBE_V13_1005) response).getFunctionCode();
        byte[] requestSequenceNumber = ((Request_NBE_V13_1005) request).getSequenceNumber();
        byte[] responseSequenceNumber = ((Response_NBE_V13_1005) response).getSequenceNumber();
        byte requestEndByte = ((Request_NBE_V13_1005) request).getEndByte();
        byte responseEndByte = ((Response_NBE_V13_1005) response).getEndByte();

        // TODO Notify which field was the first to fail (raise exception instead of returning boolean)
        if (!Arrays.equals(requestAppId, responseAppId)) {
            return false;
        } else if (!Arrays.equals(requestSerial, responseSerial)) {
            return false;
        } else if (Byte.compare(requestStartByte, responseStartByte) != 0) {
            return false;
        } else if (!Arrays.equals(requestFunctionCode, responseFunctionCode)) {
            return false;
        } else if (!Arrays.equals(requestSequenceNumber, responseSequenceNumber)) {
            return false;
        } else if (Byte.compare(requestEndByte, responseEndByte) != 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     */
    @Override
    protected void parseResponsePayload() throws PBMException {
        switch (request.getRequestType()) {
            // This request type provides the state of the burner, thus setting the alarmCode and alarmText
            case RequestType_NBE_V13_1005.REQUEST_TYPE_OPERATING_DATA:
                response.payloadSplitValuePairs(request.getRequestType(), PAYLOAD_ITEM_SEPARATOR,
                        PAYLOAD_VALUEPAIR_SEPARATOR);
                parseStateAndSubstate();
                break;

            // The following types need to be handled identically, thus no break between them
            case RequestType.REQUEST_TYPE_DISCOVERY:
            case RequestType_NBE_V13_1005.REQUEST_TYPE_ADVANCED_DATA:
            case RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_BOILER:
            case RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_CLEANING:
            case RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_HOPPER:
            case RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_MISC:
            case RequestType_NBE_V13_1005.REQUEST_TYPE_SETTINGS_DATA_ALARM:
                response.payloadSplitValuePairs(request.getRequestType(), PAYLOAD_ITEM_SEPARATOR,
                        PAYLOAD_VALUEPAIR_SEPARATOR);
                break;

            // The following types need to be handled identically, thus no break between them
            case RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_HOURS:
            case RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_DAYS:
            case RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_MONTHS:
            case RequestType_NBE_V13_1005.REQUEST_TYPE_CONSUMPTION_DATA_YEARS:
                response.payloadSplitList(request.getRequestType(), PAYLOAD_VALUEPAIR_SEPARATOR,
                        PAYLOAD_LIST_SEPARATOR);
                break;

            // TODO Implement RequestType.REQUEST_TYPE_DISCOVERY_BROADCAST

            default:
                // FIXME Should raise an exception
                // An error occured, so set response as invalid
                response.setValidity(false);
                break;
        }
        // If all is good, set response as valid
        response.setValidity(true);
    }

    /**
     * Discovers the burner
     */
    @Override
    public boolean discoverBurner(boolean retryOnFail) throws PBMException {
        return discoverBurnerByRequestType(RequestType.REQUEST_TYPE_DISCOVERY, retryOnFail);
    }

    /**
     * Discovers the burner by broadcasting
     */
    @Override
    public boolean discoverBurnerByBroadcast(boolean retryOnFail) throws PBMException {
        // FIXME This doesn't currently work
        return discoverBurnerByRequestType(RequestType.REQUEST_TYPE_DISCOVERY_BROADCAST, retryOnFail);
    }

    /**
     * Processes burner state and substate, and sets alarmCode and alarmText accordingly
     *
     * @throws PBMException
     */
    private void parseStateAndSubstate() throws PBMException {
        String stateAsString = response.getBurnerState();
        String substateAsString = response.getBurnerSubState();
        int alarmCode = -1;
        String alarmText = "";
        // FIXME Perhaps this could/should be done differently?
        // FIXME Probably a lot of different states are missing below
        try {
            int state = Integer.valueOf(stateAsString);
            int substate = Integer.valueOf(substateAsString);

            switch (state) {
                case 0:
                    alarmCode = state;
                    alarmText = "Please wait"; // Vent et øjeblik
                    break;

                case 2:
                    alarmText = "Ignite fire: ";
                    switch (substate) {
                        case 1:
                            // Ignition 1, ventilation of boiler
                            // operating_data/state=2
                            // operating_data/substate=1
                            // operating_data/off_on_alarm=1
                            alarmCode = 201;
                            alarmText += "Ventilation of boiler";
                            break;
                        case 2:
                            // Ignition 1, feeding pellets
                            // operating_data/state=2
                            // operating_data/substate=2
                            // operating_data/off_on_alarm=1
                            alarmCode = 202;
                            alarmText += "Feeding pellets";
                            break;
                        case 4:
                            // Ignition 1, elstarter
                            // operating_data/state=2
                            // operating_data/substate=4
                            // operating_data/off_on_alarm=1
                            // operating_data/substate_sec=271 //countdown to timeout
                            alarmCode = 204;
                            alarmText += "Electric ignition"; // Eltænding
                            break;
                        case 16:
                            // Ignition 1, <unknown>
                            // operating_data/state=2
                            // operating_data/substate=16
                            // operating_data/off_on_alarm=1
                            alarmCode = 216;
                            alarmText += "Using internal auger"; // Efterløb intern snegl
                            break;
                        default:
                            // For substates not matching above cases
                            alarmCode = 299;
                            alarmText += "Unknown action";
                            break;
                    }
                    break;

                case 5:
                    alarmCode = state;
                    alarmText = "Boiler is on. No issues";
                    break;

                case 9:
                    alarmText = "Boiler is stopped: ";
                    switch (substate) {
                        case 0:
                            alarmCode = 900;
                            alarmText += "Temperature reached"; // Stoppet - temperatur er opnået
                            break;
                        case 8:
                            alarmCode = 908;
                            alarmText += "Ash cleaning"; // Stoppet - udfører asketømning
                            break;
                        case 13:
                            alarmCode = 913;
                            alarmText += "Compressor cleaning valve 3"; // Stoppet - kompressor rens ventil 3
                            break;
                        case 14:
                            alarmCode = 914;
                            alarmText += "Valve 3 is active"; // Stoppet - ventil 3 aktiv
                            break;
                        default:
                            // For substates not matching above cases
                            alarmCode = 999;
                            alarmText += "Unknown action";
                            break;
                    }
                    break;

                case 11:
                    alarmCode = state;
                    alarmText = "Boiler is too hot. Do not restart without fixing the cause!";
                    break;

                case 13:
                    alarmCode = state;
                    alarmText = "Error igniting!";
                    break;

                case 14:
                    // Boiler is off, but there's no alarm:
                    // operating_data/state=14
                    // operating_data/substate=0
                    // operating_data/off_on_alarm=0
                    alarmCode = state;
                    alarmText = "Boiler is manually turned off";
                    break;

                case 20:
                    // Boiler is off and there's an alarm:
                    // operating_data/state=20
                    // operating_data/substate=0
                    // operating_data/off_on_alarm=2
                    alarmCode = state;
                    alarmText = "Boiler failed to ignite fire. Missing pellets?";
                    break;

                case 23:
                    alarmText = "Boiler is on. Stopped as per schedule: ";
                    switch (substate) {
                        case 0:
                            alarmCode = 2300;
                            alarmText += "No issue";
                            break;
                        case 8:
                            alarmCode = 2308;
                            alarmText += "Unknown (08)"; // Don't know the purpose
                            break;
                        case 13:
                            alarmCode = 2313;
                            alarmText += "Unknown (13)"; // Don't know the purpose
                            break;
                        default:
                            // For substates not matching above cases
                            alarmCode = 2399;
                            alarmText += "Unknown action";
                            break;
                    }
                    break;

                default:
                    // For states not matching above cases
                    alarmCode = state;
                    alarmText = "Unknown state " + state + " and substate " + substate;
                    break;
            }
            // Save the alarm code and text in the response
            response.setAlarmCode(alarmCode);
            response.setAlarmText(alarmText);
        } catch (NumberFormatException e) {
            throw new PBMException(
                    "Unable to determine state. Received: '" + stateAsString + "' and '" + substateAsString + "'",
                    PBMExceptionType.UNKNOWN_BURNER_STATE, e);
        }
    }
}
