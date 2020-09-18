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

import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link PelletBurnerMonitorHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Jonas Overgaard - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.pelletburnermonitor", service = ThingHandlerFactory.class)
public class PelletBurnerMonitorHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .singleton(PelletBurnerMonitorBindingConstants.THING_TYPE_NBE_V13_1005);

    /**
     * Returns true if the Thing is supported by this binding
     */
    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    /**
     * Creates the appropriate handler based on the config
     */
    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        // The idea here, is to have the same Handler for each productline/model, and then different Protocol subclasses
        // for the different software versions of the same productline/model. I.e. all NBE products uses
        // PelletBurnerMonitorNBEHandler
        if (PelletBurnerMonitorBindingConstants.THING_TYPE_NBE_V13_1005.equals(thingTypeUID)) {
            return new PelletBurnerMonitorNBEHandler(thing);
        }

        return null;
    }
}
