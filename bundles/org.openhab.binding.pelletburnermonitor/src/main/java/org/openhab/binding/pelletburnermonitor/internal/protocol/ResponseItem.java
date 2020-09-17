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

/**
 * Represents an item received from burner.
 *
 * @author Jonas Overgaard - Initial contribution
 *
 */
@NonNullByDefault
public class ResponseItem {
    private String group;
    private String id;
    private String value;

    /**
     * Creates the item, setting group, ID and value
     *
     * @param group The group the item is in
     * @param id The ID of the field
     * @param value The value
     */
    public ResponseItem(String group, String id, String value) {
        this.group = group;
        this.id = id;
        this.value = value;
    }

    /**
     * Provides the group of the item
     *
     * @return The group as text
     */
    public String getGroup() {
        return group;
    }

    /**
     * Provides the id of the item
     *
     * @return The id as text
     */
    public String getId() {
        return id;
    }

    /**
     * Provides the value of the item
     *
     * @return The value as text
     */
    public String getValue() {
        return value;
    }

    /**
     * Generates a string to output the item to i.e. the commandline
     */
    @Override
    public String toString() {
        return group + ": " + id + "=" + value;
    }
}
