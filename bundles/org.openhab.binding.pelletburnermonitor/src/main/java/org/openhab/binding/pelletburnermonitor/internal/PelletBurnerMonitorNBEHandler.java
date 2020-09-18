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
package org.openhab.binding.pelletburnermonitor.internal;

import static org.openhab.binding.pelletburnermonitor.internal.PelletBurnerMonitorBindingConstants.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.pelletburnermonitor.internal.controller.Connection;
import org.openhab.binding.pelletburnermonitor.internal.controller.Options;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMException;
import org.openhab.binding.pelletburnermonitor.internal.controller.PBMExceptionType;
import org.openhab.binding.pelletburnermonitor.internal.protocol.ImplementedProtocol;
import org.openhab.binding.pelletburnermonitor.internal.protocol.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link PelletBurnerMonitorNBEHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Jonas Overgaard - Initial contribution
 */
@NonNullByDefault
public class PelletBurnerMonitorNBEHandler extends BaseThingHandler {
    // Log functionality
    private final Logger logger = LoggerFactory.getLogger(PelletBurnerMonitorNBEHandler.class);

    // Attributes
    private static final long INITIAL_DELAY_IN_SECONDS = 20;

    // Type definitions for channel values
    private static final int TYPE_DECIMAL = 0;
    private static final int TYPE_PERCENTAGE = 1;
    private static final int TYPE_QUANTITY_TEMPERATURE = 2;
    private static final int TYPE_QUANTITY_WEIGHT_G = 3;
    private static final int TYPE_QUANTITY_WEIGHT_KG = 4;
    private static final int TYPE_QUANTITY_POWER = 5;
    private static final int TYPE_TEXT = 6;
    private static final int TYPE_INTEGER = 7;

    // Background job that will poll the boiler at an interval
    private @Nullable ScheduledFuture<?> refreshJob;

    private @Nullable PelletBurnerMonitorConfiguration config;

    /**
     * Lookup of ImplementedProtocol based on the chosen thing type
     *
     * @return
     * @throws PBMException
     */
    private ImplementedProtocol thingTypeToProtocol() throws PBMException {
        if (getThing().getThingTypeUID().equals(PelletBurnerMonitorBindingConstants.THING_TYPE_NBE_V13_1005)) {
            logger.debug("PBM: Using protocol {}", ImplementedProtocol.NBE_V13_1005.toString());
            return ImplementedProtocol.NBE_V13_1005;
        } else {
            throw new PBMException("Unknown protocol", PBMExceptionType.UNKNOWN_PROTOCOL);
        }
    }

    /**
     * Try to find the burner based on the configuration of the Thing
     *
     * @param retryOnFail
     * @return
     */
    private boolean discoverBurner(boolean retryOnFail) {
        logger.debug("PBM: Try to discover burner");
        boolean status = false; // Becomes only true if successful

        try {
            if (Connection.getInstance().discoverBurner(retryOnFail)) {
                logger.debug("PBM: Success discovering burner");
                status = true;
            }
        } catch (PBMException e) {
            logger.error("PBM: Unable to discover burner: #{};{}", e.getErrorCode(), e.getMessage());
        }
        return status;
    }

    /**
     * Will try to discover the burner, fetch data and update the appropriate channels
     *
     * @return
     */
    private boolean updateBoilerData() {
        boolean status = false;

        // If not previously discovered, try again
        if (getThing().getStatus() != ThingStatus.ONLINE) {
            discoverBurnerAndSetThingStatus(true);
            // If still not discovered, abort the attempted data retrieval
            if (getThing().getStatus() != ThingStatus.ONLINE) {
                logger.error("PBM: Update aborted as burner was not discovered");
                return status;
            }
        }

        logger.debug("PBM: Start updating boiler data");

        // Fetch data, validate it and update the appropriate channels
        Response response;
        try {
            response = Connection.getInstance().getAllData();

            if (response.isValidResponse()) {
                updateStateData(response);
                status = true;
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Invalid response from remote host");
            }
        } catch (PBMException e) {
            logger.error("PBM: Unable to fetch data: #{};{}", e.getErrorCode(), e.getMessage());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }
        logger.debug("PBM: Thing status {}", getThing().getStatus().toString());
        logger.debug("PBM: End updating boiler data");
        return status;
    }

    /**
     * Will update the channels based on the data provided in Response
     *
     * @param response Received data from burner, which has been validated and parsed
     * @throws PBMException
     */
    private void updateStateData(Response response) throws PBMException {
        logger.debug("PBM: Channel updates started");

        String siloContents = response.getSiloContents();
        String siloMinimumContents = response.getSiloMinimumContents();

        updateStateIfValidValue(CHANNEL_SILO_MINIMUM_CONTENTS, siloMinimumContents, TYPE_QUANTITY_WEIGHT_KG);
        updateStateIfValidValue(CHANNEL_AUGER_CONSUMPTION, response.getAugerConsumption(), TYPE_QUANTITY_WEIGHT_G);
        updateStateIfValidValue(CHANNEL_BOILER_LIMIT_TEMPERATURE_ABOVE, response.getTemperatureLimitAbove(),
                TYPE_QUANTITY_TEMPERATURE);
        updateStateIfValidValue(CHANNEL_BOILER_LIMIT_TEMPERATURE_BELOW, response.getTemperatureLimitBelow(),
                TYPE_QUANTITY_TEMPERATURE);
        updateStateIfValidValue(CHANNEL_BOILER_CLEANING_COUNTDOWN, response.getCleaningCountdown(),
                TYPE_QUANTITY_WEIGHT_KG);

        updateStateIfValidValue(CHANNEL_SILO_CONTENTS, siloContents, TYPE_QUANTITY_WEIGHT_KG);
        updateStateIfValidValue(CHANNEL_BOILER_CURRENT_TEMPERATURE, response.getTemperatureCurrent(),
                TYPE_QUANTITY_TEMPERATURE);
        updateStateIfValidValue(CHANNEL_BOILER_POWER_OUTPUT_PERCENTAGE, response.getPowerOutputPercentage(),
                TYPE_PERCENTAGE);
        updateStateIfValidValue(CHANNEL_BOILER_POWER_OUTPUT_KILOWATTS, response.getPowerOutputKilowatts(),
                TYPE_QUANTITY_POWER);

        updateStateIfValidValue(CHANNEL_BOILER_TARGET_TEMPERATURE, response.getTemperatureTarget(),
                TYPE_QUANTITY_TEMPERATURE);

        // FIXME Use settings/ignition/ignition_number to keep track of the amount of ignitions executed. Around 1000
        // should be 1 year.

        // Update refill status of silo
        updateRefillSiloStatus(siloContents, siloMinimumContents);

        // FIXME Might need to create a single channel called consumption, which will
        // show the current consumption
        // Update consumption of previous hour
        updateStateIfValidValue(CHANNEL_CONSUMPTION_PREVIOUS_HOUR, response.getConsumptionPreviousHour(),
                TYPE_QUANTITY_WEIGHT_KG);

        // Update alarm code and text
        updateStateIfValidValue(CHANNEL_ALARM_CODE, response.getAlarmCode(), TYPE_INTEGER);
        updateStateIfValidValue(CHANNEL_ALARM_TEXT, response.getAlarmText(), TYPE_TEXT);

        // Update the current timestamp as the last thing, as if we reach this point, the update was successful
        State state = new DateTimeType();
        updateState(CHANNEL_TIMESTAMP_LAST_UPDATE, state);
        logger.debug("PBM: Channel '{}' set to '{}'", CHANNEL_TIMESTAMP_LAST_UPDATE, state.toString());

        logger.debug("PBM: Channel updates completed");
    }

    /**
     * Will calculate the state of the RefillSilo channel based on current silo contents and warning limit for silo
     *
     * @param siloContents
     * @param siloMinimumContents
     */
    private void updateRefillSiloStatus(String siloContents, String siloMinimumContents) {
        try {
            boolean state;
            int siloContentsInt = Integer.parseInt(siloContents);
            int siloMinimumContentsInt = Integer.parseInt(siloMinimumContents);
            if (siloContentsInt < siloMinimumContentsInt) {
                state = true;
            } else {
                state = false;
            }
            updateStateIfValidValue(CHANNEL_REFILL_SILO, state, TYPE_QUANTITY_WEIGHT_KG);
        } catch (NumberFormatException e) {
            logger.error("PBM: Unable to determine if silo should be refilled");
        }
    }

    /**
     * Update channel which is of type OnOff
     *
     * @param channel
     * @param isOn
     * @param type
     * @return
     */
    private boolean updateStateIfValidValue(String channel, boolean isOn, int type) {
        OnOffType state;
        if (isOn) {
            state = OnOffType.ON;
        } else {
            state = OnOffType.OFF;
        }

        updateState(channel, state);
        logger.debug("PBM: Channel {} set to {}", channel, state.toString());

        return true;
    }

    /**
     * Update channel which is of type int
     *
     * @param channel
     * @param valueAsInt
     * @param type
     * @return
     */
    private boolean updateStateIfValidValue(String channel, int valueAsInt, int type) {
        String valueAsString = String.valueOf(valueAsInt);
        return updateStateIfValidValue(channel, valueAsString, type);
    }

    /**
     * Update channel which is of type String
     *
     * @param channel
     * @param value
     * @param type
     * @return
     */
    private boolean updateStateIfValidValue(String channel, String value, int type) {
        try {
            State state = new StringType();
            // FIXME the different types should be implemented instead of always using DecimalType
            switch (type) {
                case TYPE_DECIMAL:// Number
                case TYPE_PERCENTAGE:// %
                case TYPE_QUANTITY_TEMPERATURE:// C
                case TYPE_QUANTITY_WEIGHT_G: // G
                case TYPE_QUANTITY_WEIGHT_KG: // KG
                case TYPE_QUANTITY_POWER: // kW
                    double valueAsDouble = Double.parseDouble(value);
                    state = new DecimalType(valueAsDouble);
                    break;
                case TYPE_TEXT: // Text string
                    state = new StringType(value);
                    break;
                case TYPE_INTEGER: // Integer (whole number)
                    int valueAsInteger = Integer.parseInt(value);
                    state = new DecimalType(valueAsInteger);
                    break;
                default:
                    // logger.error("PBM: Unable to convert '" + value + "' into type #" + type);
                    logger.error("PBM: Unable to convert '{}' into type #{} for channel {}", value, type, channel);
                    return false;
            }

            updateState(channel, state);
            logger.debug("PBM: Channel '{}' set to '{}'", channel, state.toString());

        } catch (NumberFormatException e) {
            logger.debug("PBM: Unable to convert string to int. : '{}', for channel '{}'", value, channel);
            return false;
        }

        return true;
    }

    /**
     * Try to discover burner. If successful, will set Thing to ONLINE. Otherwise it'll be set OFFLINE
     *
     * @param retryOnFail
     */
    @SuppressWarnings("null")
    private void discoverBurnerAndSetThingStatus(boolean retryOnFail) {
        if (discoverBurner(retryOnFail)) {
            updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Unable to discover burner " + config.serial + " at " + config.localaddress);
            logger.error(
                    "PBM: Thing status {}. Check Thing configuration, and availability of remotehost if this keeps appearing",
                    getThing().getStatus().toString());
        }
        logger.debug("PBM: Thing status {}", getThing().getStatus().toString());
    }

    /**
     * Constructor. Doesn't do much
     *
     * @param thing
     */
    public PelletBurnerMonitorNBEHandler(Thing thing) {
        super(thing);
        logger.debug("PBM: Starting");
    }

    /**
     * Command processing. Doesn't do much as the refresh job fetches data
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // logger.debug("PBM: Processing command '{}' for channel {}", command.toString(), channelUID.getId());
    }

    /**
     * Initialization of binding. Loads config, and starts refresh job based on config
     */
    @SuppressWarnings("null")
    @Override
    public void initialize() {
        logger.debug("PBM: Start initializing");

        config = getConfigAs(PelletBurnerMonitorConfiguration.class);

        try {
            // Initialize the appropriate protocol
            Options options = new Options(config.localaddress, config.localport, config.remoteaddress,
                    config.remoteport, thingTypeToProtocol(), config.serial, config.password);
            Connection.getInstance().setOptions(options);
            Connection.getInstance().setLogger(logger);

            // If refreshJob is already running, stop it, and start a new one to avoid having two running instances
            if (refreshJob != null) {
                // Perhaps the issues with "Handler PelletBurnerMonitorNBEHandler of thing <...> was already disposed"
                // was caused by the parameter being true?
                logger.debug("PBM: Cancelling refresh job");
                refreshJob.cancel(false);
            }

            // Start a new refreshJob
            // if (refreshJob == null || refreshJob.isCancelled()) {
            logger.debug("PBM: Start refresh job at interval {} min.", config.refresh);
            refreshJob = scheduler.scheduleWithFixedDelay(this::updateBoilerData, INITIAL_DELAY_IN_SECONDS,
                    TimeUnit.MINUTES.toSeconds(config.refresh), TimeUnit.SECONDS);
            // }

            // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
            // the framework is then able to reuse the resources from the thing handler initialization.
            // we set this upfront to reliably check status updates in unit tests.
            updateStatus(ThingStatus.UNKNOWN);
            logger.debug("PBM: Thing status {}", getThing().getStatus().toString());

        } catch (PBMException e) {
            logger.error("PBM: {}", e.getMessage());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
            logger.debug("PBM: Thing status {}", getThing().getStatus().toString());
        }
    }
}
