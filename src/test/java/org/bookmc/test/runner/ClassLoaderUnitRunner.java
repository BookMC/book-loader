package org.bookmc.test.runner;

import org.bookmc.test.runner.classloader.JUnit4ClassLoader;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class ClassLoaderUnitRunner extends BlockJUnit4ClassRunner {
    public static final JUnit4ClassLoader classLoader = new JUnit4ClassLoader();

    public ClassLoaderUnitRunner(Class<?> testClass) throws InitializationError {
        super(switchContext(testClass));
    }

    /**
     * Changes the classloader of the requested classloader so that it can find URLs appended
     * at runtime (also the reason we use JUnit4)
     *
     * @param clazz The class to have it's classloader "switched"
     * @return The "switched" class
     * @throws InitializationError Failed to find the class requested
     */
    public static Class<?> switchContext(Class<?> clazz) throws InitializationError {
        try {
            return classLoader.loadClass(clazz.getName());
        } catch (ClassNotFoundException e) {
            throw new InitializationError(e);
        }
    }
}
