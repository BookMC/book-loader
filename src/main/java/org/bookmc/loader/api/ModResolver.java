package org.bookmc.loader.api;

import org.bookmc.loader.api.candidate.ModCandidate;

/**
 * The resolver API allows us to make the loader more fail-safe by allow ourselves to
 * handle whether we want/can load what we're about to load instead of depending on the
 * loader to handle itself what to load.
 * <p>
 * A mod cannot implement a resolver unless it plans what it has resolved after the initial load
 * since it is an internal API used at the beginning of launch.
 *
 * @author ChachyDev
 */
public interface ModResolver {
    /**
     * This non-default provides the discoverer with an array of files (if wanted).
     * It should after either resolve those files by registering them as candidates
     * or provide it's own files to resolve.
     *
     * For more on registering candidates see {@link org.bookmc.loader.impl.Loader#registerCandidate(ModCandidate)}
     */
    void resolve();
}
