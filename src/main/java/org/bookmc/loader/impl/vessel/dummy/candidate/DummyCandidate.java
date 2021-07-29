package org.bookmc.loader.impl.vessel.dummy.candidate;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.vessel.ModVessel;

public class DummyCandidate implements ModCandidate {
    private final ModVessel[] vessels;

    public DummyCandidate(ModVessel[] vessels) {
        this.vessels = vessels;
    }

    @Override
    public ModVessel[] getVessels() {
        return vessels;
    }

    @Override
    public boolean isAcceptable() {
        return true;
    }

    @Override
    public void addToClasspath(LaunchClassLoader classLoader) {

    }
}