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

/**
 * The {@link PelletBurnerMonitorConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Jonas Overgaard - Initial contribution
 */
@NonNullByDefault
public class PelletBurnerMonitorConfiguration {

    public String localaddress = "";
    public int localport = 8483;
    public String remoteaddress = "";
    public int remoteport = 8483;
    public String serial = "";
    public String password = "";
    public long refresh = 15;
}
