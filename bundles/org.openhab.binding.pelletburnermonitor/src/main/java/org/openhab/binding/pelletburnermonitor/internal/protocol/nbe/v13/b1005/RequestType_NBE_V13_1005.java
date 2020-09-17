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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.pelletburnermonitor.internal.protocol.RequestType;

/**
 * The {@link RequestType_NBE_V13_1005} class defines the request types for this specific protocol version.
 *
 * @author Jonas Overgaard - Initial contribution
 */
@NonNullByDefault
public class RequestType_NBE_V13_1005 extends RequestType {
    public static final int REQUEST_TYPE_OPERATING_DATA = 11;
    public static final int REQUEST_TYPE_ADVANCED_DATA = 12;
    public static final int REQUEST_TYPE_CONSUMPTION_DATA_HOURS = 13;
    public static final int REQUEST_TYPE_CONSUMPTION_DATA_DAYS = 14;
    public static final int REQUEST_TYPE_CONSUMPTION_DATA_MONTHS = 15;
    public static final int REQUEST_TYPE_CONSUMPTION_DATA_YEARS = 16;
    public static final int REQUEST_TYPE_SETTINGS_DATA_HOPPER = 17;
    public static final int REQUEST_TYPE_SETTINGS_DATA_BOILER = 18;
    public static final int REQUEST_TYPE_SETTINGS_DATA_CLEANING = 19;
    public static final int REQUEST_TYPE_SETTINGS_DATA_MISC = 20;
    public static final int REQUEST_TYPE_SETTINGS_DATA_ALARM = 21;
}
