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
 * Exception class for this binding
 *
 * @author Jonas Overgaard - Initial contribution
 *
 */
@NonNullByDefault
public class PBMException extends Exception {
    private static final long serialVersionUID = -8355397984411689451L;
    private PBMExceptionType errorCode = PBMExceptionType.UNINITIALIZED;

    /**
     * Constructor for exception class with Throwable
     * 
     * @param errorMessage
     * @param errorCode
     * @param e
     */
    public PBMException(String errorMessage, PBMExceptionType errorCode, Throwable e) {
        super(errorMessage, e);
        this.errorCode = errorCode;
    }

    /**
     * Constructor for exception class without Throwable
     * 
     * @param errorMessage
     * @param errorCode
     */
    public PBMException(String errorMessage, PBMExceptionType errorCode) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    /**
     * Returns the enumerable from PBMExceptionType
     * 
     * @return
     */
    public PBMExceptionType getErrorCode() {
        return errorCode;
    }
}
