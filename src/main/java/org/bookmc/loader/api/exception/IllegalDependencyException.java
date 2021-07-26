package org.bookmc.loader.api.exception;

import org.bookmc.loader.api.vessel.ModVessel;

public class IllegalDependencyException extends Exception {
    public IllegalDependencyException(ModVessel vessel) {
        super(vessel.getName() + " cannot have any dependencies. This may be due to it being a compatability layer.");
    }
}
