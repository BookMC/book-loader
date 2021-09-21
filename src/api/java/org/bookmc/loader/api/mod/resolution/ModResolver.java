package org.bookmc.loader.api.mod.resolution;

import org.bookmc.loader.api.mod.ModCandidate;

public interface ModResolver {
    ModCandidate[] resolveMods();
}
