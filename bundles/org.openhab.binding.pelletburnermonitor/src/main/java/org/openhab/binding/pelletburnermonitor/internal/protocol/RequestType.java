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

/***
 * Superclass for request types. Should be extended in a subclass for the respective subclass,to add additional types**
 *
 * @author Jonas Overgaard - Initial contribution
 *
 */
@NonNullByDefault
public class RequestType {
    // FIXME How can static ints be avoided an something like an enum be used instead? Currently the issue is that enum
    // is not inheritable, but Protocol uses RequestType values. It must be a concept in which both some superclass
    // values can be created, as well as some subclass values
    /**
     * Discovery of burner. Works like pinging an IP
     */
    public static final int REQUEST_TYPE_DISCOVERY = 0;

    /**
     * Discovery of burner by broadcasting
     */
    public static final int REQUEST_TYPE_DISCOVERY_BROADCAST = 1;
}
