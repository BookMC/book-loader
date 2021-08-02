package org.bookmc.loader.api.candidate;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.classloader.ClassLoaderURLAppender;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.candidate.ZipModCandidate;
import org.bookmc.loader.impl.resolve.ClasspathModResolver;
import org.bookmc.loader.impl.resolve.DevelopmentModResolver;

/**
 * An interface to allow for 3rd parties and alternative implementations of ModCandidates.
 * The ModCandidates system allows us to internally allow alternate candidates but also the default candidates
 * such as {@link ZipModCandidate} which checks for the required template needed to load a Book mod such as a valid
 * book.mod.json.
 * <p>
 * If you were to create your own compatibility layer you would most likely create your own implementation of
 * this interface to allow for your custom candidate to be loaded. Enjoy developers!
 *
 * @author ChachyDev
 * @since 0.2.0
 */
public interface ModCandidate {
    /**
     * If the candidate has succeeded the {@link ModCandidate#isResolvable()} phase
     * you can now continue onto retrieving the vessel and loading it.
     *
     * @return ModVessel
     */
    ModVessel[] getVessels();

    /**
     * Decides whether a discovered mod should be registered (indicating it will be loaded).
     * If you were to create a compatability layer you would create your own ModCandidate implementation
     * instead of using the current existing ones as they're heavily focused on the Book loader itself
     * rather than customisation for external loaders.
     *
     * @return Whether the given candidate should be accepted. You should receive the required parameters
     * to perform this check via the constructor of your ModCandidate implementation.
     */
    boolean isResolvable();

    /**
     * Invoked directly before the vessel is registered to add the candidate to the classpath. In some cases
     * such as {@link DevelopmentModResolver} or {@link ClasspathModResolver}
     * this method may not be needed at all as the mod candidates should already be present on the classpath or something is going wrong!
     *
     * @param appender A custom class to abstract appending to the current URLClassLoader. (Currently {@link LaunchClassLoader})
     */
    void addToClasspath(ClassLoaderURLAppender appender);
}
