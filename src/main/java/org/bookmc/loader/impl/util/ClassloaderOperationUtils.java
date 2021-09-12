package org.bookmc.loader.impl.util;

import org.bookmc.loader.impl.classloader.ModClassLoader;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.dependency.ModDependency;
import org.bookmc.loader.impl.Loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ClassloaderOperationUtils {
    /**
     * This is the main system to sort dependencies into different classloaders
     * It recursively calls the method {@link ClassloaderOperationUtils#sortClassLoader(ModVessel)}
     * to check if it has any dependencies and if it does add the dependencies and itself to the classpath
     * if not stay on it's own classpath.
     * <p>
     * This was quite mentally exhausting to plan out how to make :)
     *
     * @param vessels The vessels to have their classloaders sorted.
     */
    public static void sortClassLoaders(List<ModVessel> vessels) {
        for (ModVessel vessel : vessels) {
            sortClassLoader(vessel);
        }
    }

    private static void sortClassLoader(ModVessel parent) {
        if (parent.getClassLoader() == null) {
            URL[] urls = new URL[0];
            if (parent.getFile() != null) {
                try {
                    urls = new URL[]{parent.getFile().toURI().toURL()};
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            parent.setClassLoader(new ModClassLoader(urls));
        }
        for (ModDependency dependency : parent.getDependsOn()) {
            sortClassLoaderDependsOn(dependency, parent);
        }
        for (ModDependency suggestion : parent.getSuggestions()) {
            sortClassLoaderSuggests(suggestion, parent);
        }
    }

    private static void sortClassLoaderDependsOn(ModDependency dependency, ModVessel parent) {
        if (Loader.getModVesselsMap().containsKey(dependency.getId())) {
            ModVessel dependencyVessel = Loader.getModVesselsMap().get(dependency.getId());

            if (dependencyVessel.getClassLoader() == null) {
                sortClassLoader(dependencyVessel);
            }

            if (dependencyVessel.getClassLoader() != parent.getClassLoader() && !dependencyVessel.isInternal()) {
                URL[] urls = dependencyVessel.getClassLoader().getURLs();
                for (URL url : urls) {
                    parent.getClassLoader().addURL(url);
                }
                dependencyVessel.setClassLoader(parent.getClassLoader());
                for (ModDependency modDependency : dependencyVessel.getDependsOn()) {
                    sortClassLoaderDependsOn(modDependency, dependencyVessel);
                }
            }
        }
    }

    private static void sortClassLoaderSuggests(ModDependency dependency, ModVessel parent) {
        if (Loader.getModVesselsMap().containsKey(dependency.getId())) {
            ModVessel dependencyVessel = Loader.getModVesselsMap().get(dependency.getId());
            if (dependencyVessel.getClassLoader() != parent.getClassLoader()) {
                URL[] urls = dependencyVessel.getClassLoader().getURLs();
                for (URL url : urls) {
                    parent.getClassLoader().addURL(url);
                }
                dependencyVessel.setClassLoader(parent.getClassLoader());
                for (ModDependency modDependency : dependencyVessel.getSuggestions()) {
                    sortClassLoaderSuggests(modDependency, dependencyVessel);
                }
            }
        }
    }
}
