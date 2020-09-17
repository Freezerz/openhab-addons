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

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The different possible exception types
 *
 * @author Jonas Overgaard - Initial contribution
 *
 */
@NonNullByDefault
public enum PBMExceptionType {
    UNINITIALIZED,
    UNKNOWN_PROTOCOL,
    UNABLE_TO_BIND_LOCAL_PORT,
    NO_CONNECTION_TO_REMOTE_SERVER,
    INVALID_REQUEST,
    INVALID_RESPONSE,
    ERROR_SENDING,
    ERROR_RECEIVING,
    UNKNOWN_LOCAL_HOST,
    UNKNOWN_REMOTE_HOST,
    ERROR_ACCESSING_DATA,
    SLEEP_PROBLEM,
    UNKNOWN_BURNER_STATE,
    UNABLE_TO_DETERMINE_TIME_OF_DAY,
    TIMEOUT;
}
