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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link PelletBurnerMonitorBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Jonas Overgaard - Initial contribution
 */
@NonNullByDefault
public class PelletBurnerMonitorBindingConstants {
    // Binding ID
    private static final String BINDING_ID = "pelletburnermonitor";

    // Thing IDs
    public static final ThingTypeUID THING_TYPE_NBE_V13_1005 = new ThingTypeUID(BINDING_ID,
            "pelletburner_nbe_v13_1005");

    // Setup attributes
    public static final String PROPERTY_LOCALADDRESS = "localaddress";
    public static final String PROPERTY_LOCALPORT = "localport";
    public static final String PROPERTY_REMOTEADDRESS = "remoteaddress";
    public static final String PROPERTY_REMOTEPORT = "remoteport";
    public static final String PROPERTY_SERIAL = "serial";
    public static final String PROPERTY_PASSWORD = "password";
    public static final String PROPERTY_REFRESH = "refresh";

    // List of all Channel ids
    public static final String CHANNEL_BOILER_CURRENT_TEMPERATURE = "boilerCurrentTemperature";
    public static final String CHANNEL_BOILER_TARGET_TEMPERATURE = "boilerTargetTemperature";
    public static final String CHANNEL_BOILER_LIMIT_TEMPERATURE_BELOW = "boilerLimitTemperatureBelow";
    public static final String CHANNEL_BOILER_LIMIT_TEMPERATURE_ABOVE = "boilerLimitTemperatureAbove";

    public static final String CHANNEL_SILO_CONTENTS = "siloContents";
    public static final String CHANNEL_SILO_MINIMUM_CONTENTS = "siloMinimumContents";
    public static final String CHANNEL_AUGER_CONSUMPTION = "augerConsumption";
    public static final String CHANNEL_BOILER_CLEANING_COUNTDOWN = "boilerCleaningCountdown";
    public static final String CHANNEL_CONSUMPTION_PREVIOUS_HOUR = "consumptionPreviousHour";

    public static final String CHANNEL_BOILER_POWER_OUTPUT_PERCENTAGE = "boilerPowerOutputPercentage";
    public static final String CHANNEL_BOILER_POWER_OUTPUT_KILOWATTS = "boilerPowerOutputKilowatts";

    public static final String CHANNEL_ALARM_CODE = "alarmCode";
    public static final String CHANNEL_ALARM_TEXT = "alarmText";
    public static final String CHANNEL_REFILL_SILO = "refillSilo";
}
