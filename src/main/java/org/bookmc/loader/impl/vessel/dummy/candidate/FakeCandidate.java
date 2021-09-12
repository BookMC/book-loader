package org.bookmc.loader.impl.vessel.dummy.candidate;

import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.classloader.AbstractBookClassLoader;
import org.bookmc.loader.api.vessel.ModVessel;

public record FakeCandidate(ModVessel[] vessels) implements ModCandidate {
    @Override
    public ModVessel[] getVessels() {
        return vessels;
    }

    @Override
    public boolean isResolvable() {
        return true;
    }

    @Override
    public void addToClasspath(AbstractBookClassLoader classLoader) {
        // It's fake, this probably doesn't actually exist...
        // Even if we wanted to what are we adding to the classpath
        // we don't know what to add...
    }
}
